package shop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import shop.api.DTO.*;
import shop.api.exception.InvalidInputException;
import shop.api.exception.NotFoundException;
import shop.api.models.*;
import shop.api.service.*;

import static shop.api.service.CommonService.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/v1.0")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SaleService saleService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ChoiceService choiceService;

    @Value("${domain}")
    private String domain;

    @GetMapping("/ProductByCategory/{idCategory}")
    public ResponseEntity<?> getListProductByCategory(
            @PathVariable("idCategory") Long idCategory,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "8") Integer pageSize
    ){
        List<Product> products = categoryService.findAllProductByCategoryId(idCategory);
        List<ProductResponseDTO> productsResponse = new ArrayList<>();
        for (Product product : products){
            List<Photo> photoList = photoService.getPhotoByProduct(product);
            ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, photoList);
            List<Variant> variants = productService.findVariant(product.getId());
            List<VariantResponseDTO> variantsDTO = new ArrayList<>();
            convertListVariantToVariantDTO(variants, variantsDTO);

            List<CategoryResponseDTO> categories = categoryService.findCategoryByProductId(product.getId());
            productResponse.setCategory(categories);
            productResponse.setVariantsDTO(variantsDTO);
            productsResponse.add(productResponse);
        }
        return getResponsePagination(pageNo, pageSize, productsResponse);
    }



    @GetMapping("bestSellingByCategoryId/{categoryId}")
    public ResponseEntity<?> bestSellingProductByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "8") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime toDate
    ){
        if (fromDate == null) {
            // Nếu fromDate không được cung cấp, sử dụng LocalDate hiện tại trừ đi 30 ngày
            fromDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        }

        if (toDate == null) {
            // Nếu toDate không được cung cấp, sử dụng LocalDate hiện tại
            toDate = LocalDateTime.now();
        }
        if (!toDate.isAfter(fromDate))
            throw new InvalidInputException("The to-day must be after the from-day");
        List<Product> bestSellingProducts = productService.findBestSelling(fromDate, toDate);
        List<Product> productsByCategory = categoryService.findAllProductByCategoryId(categoryId);
        System.out.println("date " + fromDate + " " + toDate);
        System.out.println("bestSellingProducts: "+ bestSellingProducts);
        System.out.println("productsByCategory: "+ productsByCategory);
        List<ProductResponseDTO> productsResponse = new ArrayList<>();
        for (Product product : bestSellingProducts){
            if (productsByCategory.contains(product)){
                List<Photo> photoList = photoService.getPhotoByProduct(product);
                ProductResponseDTO productResponse = new ProductResponseDTO(product, domain, photoList);
                List<Variant> variants = productService.findVariant(product.getId());
                List<VariantResponseDTO> variantsDTO = new ArrayList<>();
                convertListVariantToVariantDTO(variants, variantsDTO);

                List<CategoryResponseDTO> categories = categoryService.findCategoryByProductId(product.getId());
                productResponse.setCategory(categories);
                productResponse.setVariantsDTO(variantsDTO);
                productsResponse.add(productResponse);
            }

        }

        int totalProducts = productsResponse.size();
        int startIndex = Math.min((pageNo - 1) * pageSize, totalProducts);
        int endIndex = Math.min(startIndex + pageSize, totalProducts);

        List<ProductResponseDTO> paginatedProducts = productsResponse.subList(startIndex, endIndex);
        Page<ProductResponseDTO> page = new PageImpl<>(paginatedProducts, PageRequest.of(pageNo - 1, pageSize), totalProducts);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PostMapping(value = "Categories")
    public ResponseEntity<?> addCategory(@RequestBody @Valid CategoryRequestDTO newCategory) {
        List<Category> categoryByName = categoryService.findByCategoryName(newCategory.getCategoryName());
        if(categoryByName.size()!=0){
            throw  new InvalidInputException("Categoryname is exits");
        }

        Category category = new Category(null,newCategory.getCategoryName(), null);
        if(newCategory.getParent()!=null){
            Category categoryParent = categoryService.findById(newCategory.getParent());
            if(categoryParent==null){
                throw  new NotFoundException("Category is not exits");
            }
            category.setParent(categoryParent);
        }
        Category savedCategory = categoryService.saveCategory(category);

        return new ResponseEntity<>(savedCategory, HttpStatus.CREATED);
    }



    @GetMapping("/Categories")
    public  ResponseEntity<?> findList(){

        return new ResponseEntity<>(categoryService.findAllCategoryTree(), HttpStatus.OK);

    }

    @Secured({"ROLE_ADMIN"})
    @DeleteMapping("/Category/{idCategory}")
    public  ResponseEntity<?> deleteCategory(@PathVariable("idCategory") Long idCategory){
        categoryService.deleteCategory(idCategory);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping(value = "CategoryFull/{idCategory}")
    public ResponseEntity<?> updateCategoryFull(@PathVariable("idCategory") Long idCategory,@RequestBody @Valid CategoryRequestDTO categoryRequest) {
        Category category = categoryService.findById(idCategory);
        if(categoryService.existsByCategoryName(categoryRequest.getCategoryName(),idCategory)){
            throw  new InvalidInputException("Categoryname is exits");
        }

        category.setCategoryName(categoryRequest.getCategoryName());
        if(categoryRequest.getParent()!=null){
            Category categoryParent = categoryService.findById(categoryRequest.getParent());
            if(categoryParent==null){
                throw  new NotFoundException("Category is not exits");
            }
            category.setParent(categoryParent);
        }
        Category savedCategory = categoryService.saveCategory(category);

        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @PutMapping(value = "Category/{idCategory}")
    public ResponseEntity<?> updateCategory(@PathVariable("idCategory") Long idCategory,@RequestBody @Valid CategoryRequestDTO categoryRequest) {
        Category category = categoryService.findById(idCategory);
        if(categoryService.existsByCategoryName(categoryRequest.getCategoryName(),idCategory)){
            throw  new InvalidInputException("Categoryname is exits");
        }

        category.setCategoryName(categoryRequest.getCategoryName());
        Category savedCategory = categoryService.saveCategory(category);

        return new ResponseEntity<>(savedCategory, HttpStatus.OK);
    }


    @GetMapping(value = "Category/{idCategory}")
    public ResponseEntity<?> getCategory(@PathVariable("idCategory") Long idCategory){
        
        return new ResponseEntity<>(categoryService.findById(idCategory), HttpStatus.OK);
    }



    public void convertListVariantToVariantDTO(List<Variant> variants, List<VariantResponseDTO> variantsDTO) {
        for (Variant variant : variants){
            VariantResponseDTO variantResponse = new VariantResponseDTO(variant);

            List<Choice> savedChoices = choiceService.findChoiceByVariantId(variant.getId());
            List<ChoiceDTO> savedChoicesResponse = new ArrayList<>();
            if(savedChoices != null){
                if(savedChoices.size() > 0){
                    for (Choice choice : savedChoices){
                        savedChoicesResponse.add(new ChoiceDTO(choice));
                    }
                    variantResponse.setChoices(savedChoicesResponse);
                }
            }


            List<Sale> sales = saleService.findSaleByProductVariantId(variant.getId());
            Collections.sort(sales, new Comparator<Sale>() {
                @Override
                public int compare(Sale sale1, Sale sale2) {
                    return sale1.getStartDate().compareTo(sale2.getStartDate());
                }
            });
            List<SaleResponseDTO> salesResponse = new ArrayList<>();
            for (Sale sale : sales){
                salesResponse.add(new SaleResponseDTO(sale));
            }
            variantResponse.setSale(salesResponse);
            variantsDTO.add(variantResponse);
        }
    }

}