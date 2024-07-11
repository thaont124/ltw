package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.models.Bill;
import shop.api.models.Delivery;
import shop.api.repository.DeliveryRepository;

@Service
public class DeliveryService {
    @Autowired
    private DeliveryRepository deliveryRepository;
    public Delivery addDelivery(Delivery request){
        return deliveryRepository.save(request);
    }

    public Delivery getDelivery(Long idBill){
        return deliveryRepository.getDeliveryByBill_Id(idBill);
    }

    public Delivery changeStatus(Long idBill){
        Delivery delivery = deliveryRepository.getDeliveryByBill_Id(idBill);
        delivery.setStatus("Đã giao");
        return deliveryRepository.save(delivery);
    }
}
