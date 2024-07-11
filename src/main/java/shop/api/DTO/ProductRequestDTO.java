package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDTO {


    private Long id;

    @NotNull(message = "Product Name is required")
    private String productName;

    @NotNull(message = "Product Code is required")
    private String productCode;

    private Float originPrice;

    @NotNull(message = "Image is required")
    private List<MultipartFile> image;

    @NotNull(message = "Description is required")
    private String description;

    @NotNull
    private List<Long> categoryId;

    private String status;

    private List<VariantRequestDTO> variantsDTO;
}