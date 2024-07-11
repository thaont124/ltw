package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import shop.api.models.Product;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDTO {
    @NotNull(message = "email is required")
    @NotBlank(message = "email is required")
    private String email;

    @NotNull(message = "product is required")
    private List<ProductBuyDTO> productList;



    @NotNull(message = "arrived is required")
    @NotBlank(message = "arrived is required")
    private String arrived;

    @NotNull(message = "phone is required")
    @NotBlank(message = "phone is required")
    private String phone;
}
