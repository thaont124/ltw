package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import shop.api.models.Category;
import shop.api.models.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery,Long> {
    Delivery getDeliveryByBill_Id(Long idBill);

}
