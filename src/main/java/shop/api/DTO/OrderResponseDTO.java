package shop.api.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Delivery;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderResponseDTO {
    private  Long id;

    private String arrived;

    private String email;

    private String phone;

    private String status;

    private String orderDate;

    private List<ProductOrderedDTO> products;

    public void setDelivery(Delivery delivery){
        this.arrived = delivery.getArrived();
        this.email = delivery.getEmail();
        this.phone = delivery.getPhone();
        this.status = delivery.getStatus();
    }


}
