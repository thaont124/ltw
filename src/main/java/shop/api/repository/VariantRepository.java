package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.Product;
import shop.api.models.Variant;

import java.util.List;

public interface VariantRepository extends JpaRepository<Variant,Long> {

    @Query("SELECT v FROM Variant v WHERE v.product.id = :idProduct")
    List<Variant> getProductVariantByProductId(@Param("idProduct") Long idProduct);

    Variant getVariantByProductAndOriginPrice(Product product, Float originPrice);

    @Query("SELECT p FROM Product p " +
            "JOIN Variant v ON p.id = v.product.id " +
            "WHERE v.id = :variantId")
    Product findProductByProductVariantId (@Param("variantId") Long variantId);
}
