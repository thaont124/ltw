package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.Sale;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends JpaRepository<Sale, Long> {

    @Query("SELECT COUNT(s) > 0 FROM Sale s WHERE s.startDate <= :end AND s.endDate >= :start AND s.variant.id = :id")
    boolean existsBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("id") Long id);

    @Query("SELECT s FROM Sale s WHERE s.startDate <= :end AND s.endDate >= :start AND s.variant.id = :id")
    Sale findSaleBetweenDates(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("id") Long id);


    @Query("SELECT s FROM Sale s WHERE s.startDate <= current_timestamp AND s.endDate >= current_timestamp AND s.variant.id = :productVariantId")
    Sale getSaleByProductVariantIdToday(@Param("productVariantId") Long productVariantId);

    @Query("SELECT s FROM Sale s WHERE s.endDate >= current_timestamp AND s.variant.id = :productVariantId")
    List<Sale> findSaleByProductVariantId(@Param("productVariantId") Long productVariantId);

}