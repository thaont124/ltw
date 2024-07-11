package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.BillVariant;
import shop.api.models.Product;

import java.util.List;

public interface BillVariantRepository extends JpaRepository<BillVariant, Long> {

    @Query("SELECT v FROM Variant v JOIN BillVariant bv WHERE bv.bill.id = :idBill")
    List<Product> getProductByBill_Id(@Param("idBill") Long idBill);

    List<BillVariant> getBillVariantByBill_Id(Long idBill);
}
