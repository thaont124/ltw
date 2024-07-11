package shop.api.service;

import org.apache.catalina.connector.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.DTO.CategoryResponseDTO;
import shop.api.DTO.ProductResponseDTO;
import shop.api.models.Category;
import shop.api.models.Product;
import shop.api.repository.CategoryRepository;
import shop.api.repository.ProductRepository;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    public List<CategoryResponseDTO> findCategoryByProductId(Long productId){
        List<Category> categoriesByProduct = categoryRepository.findCategoryByProductId(productId);
        Map<Category, AtomicBoolean> checkIfCategoryUsed = new HashMap<>();
        List<CategoryResponseDTO> categoriesResponse = new ArrayList<>();

        for (Category category : categoriesByProduct){
            checkIfCategoryUsed.put(category, new AtomicBoolean(false));
        }
        for (Category category : categoriesByProduct){
            if (checkIfCategoryUsed.get(category).get() == false){
                checkIfCategoryUsed.put(category, new AtomicBoolean(true));
                List<CategoryResponseDTO> children = new ArrayList<>();

                categoriesResponse.add(findFather(category,children, checkIfCategoryUsed));
            }
        }
        return categoriesResponse;
    }

    private CategoryResponseDTO convertToDTO(Category category) {
        if (category == null) {
            return null;
        }
        List<Category> children = findByParentId(category.getId());
        List<CategoryResponseDTO> childrenDTO = children.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return new CategoryResponseDTO(category.getId(), category.getCategoryName(), childrenDTO);
    }

    public List<CategoryResponseDTO> findAllCategoryTree() {
        List<Category> categories = findByParentIsNull();
        List<CategoryResponseDTO> categoryResponseDTO = new ArrayList<>();
        for (Category category : categories) {
            categoryResponseDTO.add(convertToDTO(category));
        }
        return categoryResponseDTO;
    }

    public List<Product> findAllProductByCategoryId(Long id) {

        Category category = categoryRepository.findById(id).get();

        //Lấy toàn bộ category con từ id cha
        List<CategoryResponseDTO> categoriesDTO = new ArrayList<>();
        List<CategoryResponseDTO> children = convertToDTO(categoryRepository.findById(id).get()).getChildren();
        categoriesDTO.add(new CategoryResponseDTO(category.getId(), category.getCategoryName(), children));


        List<Product> products = new ArrayList<>();

        //nếu tại category có sản phẩm thì thêm sản phẩm vào danh sách
        if (productRepository.findListProductByIdCategory(id).size() > 0)
            products.addAll(productRepository.findListProductByIdCategory(id));

        if (findProductsByCategoryParent(categoriesDTO).size() > 0)
            products.addAll(findProductsByCategoryParent(categoriesDTO));

        return products;
    }

    public List<Product> findProductsByCategoryParent(List<CategoryResponseDTO> categoriesDTO) {
        List<Product> products = new ArrayList<>();

        for (CategoryResponseDTO parent : categoriesDTO) {       //Lấy toàn bộ cha
            for (CategoryResponseDTO categoryDTO : parent.getChildren()) { //Lấy con của cha
                if (productRepository.findListProductByIdCategory(categoryDTO.getId()).size() > 0)
                    products.addAll(productRepository.findListProductByIdCategory(categoryDTO.getId()));
                if (categoryDTO.getChildren().size() == 0) {
                    List<Product> productsOfChildren = productRepository.findListProductByIdCategory(categoryDTO.getId());
                    products.addAll(productsOfChildren);
                } else {
                    products.addAll(findProductsByCategoryParent(parent.getChildren()));
                }
            }
        }
        Set<Product> set = new HashSet<Product>(products);

        List<Product> productsResult = new ArrayList<Product>(set);
        return productsResult;
    }

    public List<Category> getLists(Long id) {
        List<Category> categoryList = categoryRepository.getListCategoryChildren(id);
        return categoryList;
    }

    public void deleteCategory(Long categoryId){
        categoryRepository.deleteById(categoryId);
    }

    public List<Category> findByParentId(Long idParent) {
        return categoryRepository.getListCategoryChildren(idParent);
    }

    public Category saveCategory(Category request) {
        return categoryRepository.save(request);
    }

    public Category findById(Long id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public List<Category> findByParentIsNull() {
        return categoryRepository.findByParentIsNull();
    }

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public boolean existById(Long id) {
        return categoryRepository.existsById(id);
    }

    public List<Category> findByCategoryName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }

    public boolean existsByCategoryName(String categoryName, Long idCategoryIgnore) {
        return categoryRepository.existsByCategoryNameIgnoreId(categoryName, idCategoryIgnore);
    }
    public List<Category> findCategoriesByCategoryIdList(List<Long> categoriesId) {
        List<Category> categories = new ArrayList<>();
        for (Long id : categoriesId) {
            Category category = findById(id);
            categories.add(category);
        }
        return categories;
    }

    public Map<String, String> checkCategory(List<Long> categoriesId) {
        Map<String, String> error = new HashMap<>();
        for (Long id : categoriesId) {
            if (!existById(id)) {
                error.put("Category with id " + id, "is not exist");
            }

        }
        return error;
    }

    public List<CategoryResponseDTO> filterCategories(List<CategoryResponseDTO> categoriesInput, CategoryResponseDTO parent) {
        List<CategoryResponseDTO> categoriesResult = new ArrayList<>();
        List<CategoryResponseDTO> children = parent.getChildren();
        for (CategoryResponseDTO category : children) {

            //nếu category là cấp thấp nhất => thêm vào ds
            if (!categoriesInput.contains(category) && category.getChildren().size() == 0)
                continue;
            else if (category.getChildren().size() == 0 && !categoriesResult.contains(category) )
                categoriesResult.add(category);
            else {
                //nếu category có con, cháu nhưng không trong danh sách đầu vào => thêm
                List<CategoryResponseDTO> filteredChildren = filterCategories(categoriesInput, category);
                for (CategoryResponseDTO child : filteredChildren) {
                    if (categoriesInput.contains(child)) {
                        categoriesResult.add(child);
                    }
                }
            }
        }

        return categoriesResult;
    }

    public List<Category> formatCategoryIdList(List<Long> categoriesId) {
        List<CategoryResponseDTO> categoriesInput = new ArrayList<>();
        for (Long id : categoriesId) {
            categoriesInput.add(convertToDTO(categoryRepository.findById(id).get()));
        }

        List<CategoryResponseDTO> categoriesResponse = new ArrayList<>();
        for (CategoryResponseDTO categoryDTO : categoriesInput){
            if (categoryDTO.getChildren().size() == 0)
                categoriesResponse.add(categoryDTO);
            else {
                List<CategoryResponseDTO> filterCategory = filterCategories(categoriesInput, categoryDTO);
                if (filterCategory.size() == 0)
                    categoriesResponse.add(categoryDTO);
                else categoriesResponse.addAll(filterCategory);
            }

        }
        Set<CategoryResponseDTO> set = new HashSet<CategoryResponseDTO>(categoriesResponse);

        List<CategoryResponseDTO> categoriesResult = new ArrayList<CategoryResponseDTO>(set);

        List<Category> categories = new ArrayList<>();
        for (CategoryResponseDTO category : categoriesResult) {
            categories.add(categoryRepository.findById(category.getId()).get());
        }


        return categories;
    }

    public CategoryResponseDTO findFather(Category categoryFindFather, List<CategoryResponseDTO> child,
                                          Map<Category, AtomicBoolean> checkIfCategoryUsed) {
        CategoryResponseDTO categoryResponse = new CategoryResponseDTO();
        CategoryResponseDTO categoryFindFatherDTO = new CategoryResponseDTO(categoryFindFather.getId(), categoryFindFather.getCategoryName(), new ArrayList<>());
        categoryFindFatherDTO.setChildren(child);
        if (categoryFindFather.getParent() == null)
            return categoryFindFatherDTO;
        else {
            List<CategoryResponseDTO> children = new ArrayList<>();
            children.add(categoryFindFatherDTO);

            Set<Category> input = checkIfCategoryUsed.keySet();
            for (Category category : input){
                if (category.getParent() != null){
                    if (category.getParent().getId() == categoryFindFather.getParent().getId()
                            && checkIfCategoryUsed.get(category).get() == false) {
                        checkIfCategoryUsed.put(category, new AtomicBoolean(true));
                        children.add(new CategoryResponseDTO(category.getId(), category.getCategoryName(), new ArrayList<>()));;
                    }
                }

            }
            categoryResponse = findFather(categoryFindFather.getParent(), children, checkIfCategoryUsed);

        }
        return categoryResponse;
    }
}
