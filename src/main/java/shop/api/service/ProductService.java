package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import shop.api.DTO.ProductRequestDTO;
import shop.api.Storage.StorageService;
import shop.api.exception.InvalidInputException;
import shop.api.models.*;
import shop.api.repository.CategoryProductRepository;
import shop.api.repository.PhotoRepository;
import shop.api.repository.ProductRepository;
import shop.api.Storage.StorageProperties;
import shop.api.repository.VariantRepository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryProductRepository categoryProductRepository;

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private PhotoRepository photoRepository;

    public List<Variant> findVariant(Long idProduct){
        return variantRepository.getProductVariantByProductId(idProduct);
    }
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public Product findById(Long idProduct) {

        return productRepository.findById(idProduct).orElse(null);
    }

    public void deleteProduct(Long idProduct) {
        Product product = productRepository.findById(idProduct).orElse(null);
        if (product == null) {
            throw new InvalidInputException("Product is not exits");
        }
        productRepository.delete(product);
    }

    public List<Product> findListProductByIdCategory(Long id) {
        return productRepository.findListProductByIdCategory(id);
    }

    public List<Product> getListsByName(String value) {
        List<Product> productList = productRepository.getListByName(value);
        return productList;
    }

    public Product updateProduct(Long id, ProductRequestDTO productRequest) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            throw new InvalidInputException("Product is not exits");
        }
        if (productRequest.getProductName() != null) {
            product.setProductName(productRequest.getProductName());
        }
        if (productRequest.getProductCode() != null) {
            product.setProductCode(productRequest.getProductCode());
        }
        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }

        if (productRequest.getStatus() != null) {
            product.setStatus(productRequest.getStatus());
        }
//        if (productRequest.getImage() != null) {
//            product.setImage(formatFileURL(productRequest.getImage()));
//        }
        if (productRequest.getOriginPrice() != null) {
            try {
                Float price = productRequest.getOriginPrice();
                if (price > 0) {
                    //product.setOriginPrice(price);
                } else {
                    throw new InvalidInputException("Please enter an Real");
                }
            } catch (Exception e) {
                throw new InvalidInputException("Please enter an Decimal");
            }
        }

        product.setPublishedDate(LocalDateTime.now());
        return productRepository.save(product);
    }



    public Product saveProduct(Product product, List<Category> categories) {
        Product updatedProduct = productRepository.save(product);

        List<CategoryProduct> categoriesProduct = categoryProductRepository.findCategoryProductByProduct(updatedProduct);
        for (CategoryProduct categoryProduct : categoriesProduct){
            Category category = categoryProduct.getCategory();
            if (!categories.contains(category))
                categoryProductRepository.delete(categoryProduct);
            else categories.remove(category);
        }
        for (Category category : categories){
            CategoryProduct categoryProduct = new CategoryProduct(null, category, updatedProduct);
            categoryProductRepository.save(categoryProduct);
        }
        return updatedProduct;
    }

    public Page<Product> findBestSellingByCategoryId(Integer pageNo, Integer pageSize, Long categoryId, LocalDateTime fromDate, LocalDateTime toDate) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
        return productRepository.findBestSellingProductsByCategoryAndDateRange(categoryId, fromDate, toDate, pageable);
    }

    public List<Product> findBestSelling(LocalDateTime fromDate, LocalDateTime toDate) {
        return productRepository.findBestSellingProducts(fromDate, toDate);
    }

    public boolean existProductByProductCode(String code){
        return productRepository.existsProductByProductCode(code);
    }

    public boolean existProductByProductCode(String code, Long idProduct){

        return productRepository.existsProductByProductCode(code) &&
                productRepository.findProductByProductCode(code).getId() != idProduct;
    }

    public boolean existProductById(Long id){
        return  productRepository.existsById(id);
    }


    public Product changeStatus(Long idProduct){
        Product product = productRepository.findById(idProduct).orElse(null);
        if(product==null)
            throw new InvalidInputException("Product Id is not existed");
        if (product.getStatus().equals("ACTIVE"))
            product.setStatus("INACTIVE");
        else product.setStatus("ACTIVE");
        return productRepository.save(product);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
}