package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductBuyDTO {
    private Long id;
    @NotNull(message = "quantity is required")
    @NotBlank(message = "quantity is required")
    private Integer quantity;

    private Long productVariantId;
}
