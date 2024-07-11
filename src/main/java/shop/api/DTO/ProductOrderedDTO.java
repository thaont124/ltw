package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Photo;
import shop.api.models.Product;

import java.util.List;
import java.util.Map;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductOrderedDTO {
    private Long id;

    private String productName;

    private String productCode;

    private Float totalPrice;

    private Integer quantity;

    private List<PhotoResponseDTO> image;

    private VariantResponseDTO variantsDTO;

    public ProductOrderedDTO(Product product, String domain, List<Photo> photoList){
        this.id = product.getId();
        this.productName = product.getProductName();
        this.productCode = product.getProductCode();

        this.image = DTO.convertImageToLink(photoList, domain);

    }
}
