package shop.api.DTO;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Sale;

import java.time.LocalDate;


@Data
@NoArgsConstructor
public class SaleRequestDTO {
    private Long id;

    @NotNull(message = "Type is required")
    @Min(value = 0, message = "numberSale must be greater than 0 and smaller than 100")
    @Max(value = 100, message = "numberSale must be greater than 0 and smaller than 100")
    private Long numberSale;

    @NotNull(message = "When does sale begin?")
    private String startDate;

    @NotNull(message = "When does sale end?")
    private String endDate;

    @JsonIgnore
    private Long productVariantId;


}
