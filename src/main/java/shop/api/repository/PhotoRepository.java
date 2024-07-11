package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.Photo;
import shop.api.models.Product;

import javax.transaction.Transactional;
import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> getPhotoByProduct(Product product);

    @Modifying
    @Transactional
    @Query("DELETE FROM Photo ph WHERE ph.product.id = :idProduct")
    void deletePhotoByProductId(@Param("idProduct") Long idProduct);
}
