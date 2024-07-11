package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.api.models.Category;
import shop.api.models.CategoryProduct;
import shop.api.models.Product;

import java.util.List;

public interface CategoryProductRepository extends JpaRepository<CategoryProduct, Long> {
    boolean existsCategoryProductByCategoryAndProduct(Category category, Product product);

    List<CategoryProduct> findCategoryProductByProduct(Product product);
}
