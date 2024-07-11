package shop.api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.Product;
import shop.api.models.Sale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


public interface ProductRepository extends JpaRepository<Product, Long> {


    @Query("SELECT p from Product p join CategoryProduct cp on cp.product.id = p.id join Category c on c.id = cp.category.id where c.id=:idCategory")
    List<Product> findListProductByIdCategory(@Param("idCategory") Long idCategory);

    @Query("SELECT p from  Product p where lower(p.productName) like LOWER(CONCAT('%', :value, '%'))")
    List<Product> getListByName(@Param("value") String value);
    @Query("SELECT p FROM Product p " +
            "JOIN CategoryProduct cp ON p.id = cp.product.id " +
            "JOIN Variant v ON p.id = v.product.id " +
            "JOIN BillVariant bv ON v.product.id = bv.variant.id " +
            "JOIN Bill b ON bv.bill.id = b.id " +
            "WHERE cp.category.id = :categoryId " +
            "AND b.orderDate >= :fromDate " +
            "AND b.orderDate <= :toDate " +
            "GROUP BY p.id " +
            "ORDER BY SUM(bv.quantity) DESC")
    Page<Product> findBestSellingProductsByCategoryAndDateRange(
            @Param("categoryId") Long categoryId,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );

    @Query("SELECT p FROM Product p " +
            "JOIN Variant v ON p.id = v.product.id " +
            "JOIN BillVariant bv ON bv.variant.id = v.id " +
            "JOIN Bill b ON b.id = bv.bill.id " +
            "WHERE b.orderDate >= :fromDate " +
            "AND b.orderDate <= :toDate " +
            "GROUP BY p.id " +
            "ORDER BY SUM(bv.quantity) DESC")
    List<Product> findBestSellingProducts(
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate
    );
    boolean existsProductByProductCode(String code);

    Product findProductByProductCode(String productCode);



}
