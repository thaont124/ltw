package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shop.api.models.Category;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Long> {

    @Query("SELECT c from Category c where c.parent.id =:idCategory")
    List<Category> getListCategoryChildren(@Param("idCategory") long idCategory);

    List<Category> findByParentIsNull();
    List<Category> findByCategoryName(String categoryName);

    @Query("select c from Category c join CategoryProduct cp on c.id = cp.category.id where cp.product.id = :idProduct")
    List<Category> findCategoryByProductId(@Param("idProduct") Long idProduct);

    @Query("SELECT COUNT(*) > 0 FROM Category c WHERE c.id != :idCategory AND c.categoryName = :categoryName")
    boolean existsByCategoryNameIgnoreId(@Param("categoryName") String categoryName, @Param("idCategory") Long idCategory);
}
