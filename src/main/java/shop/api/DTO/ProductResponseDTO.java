package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Photo;
import shop.api.models.Product;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDTO {

    private Long id;

    private String productName;

    private String productCode;

    private List<PhotoResponseDTO> image;

    private String description;

    private String publishedDate;

    private String status;

    private List<CategoryResponseDTO> category;

    private Long orderSort;

    private List<VariantResponseDTO> variantsDTO;

    public ProductResponseDTO(Product product, String domain, List<Photo> photoList){
        this.id = product.getId();
        this.productName = product.getProductName();
        this.productCode = product.getProductCode();
        this.orderSort = product.getOrderSort();

        this.description = product.getDescription();
        this.image = DTO.convertImageToLink(photoList, domain);

        this.status = product.getStatus();
        this.publishedDate = product.getPublishedDate().toString();
    }



}