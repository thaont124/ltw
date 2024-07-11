package shop.api.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Variant;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class VariantResponseDTO {
    private Long id;

    private Float originPrice;

    private List<ChoiceDTO> choices;


    private List<SaleResponseDTO> sale;

    public VariantResponseDTO(Variant variant){
        this.id = variant.getId();
        this.originPrice = variant.getOriginPrice();
    }
}