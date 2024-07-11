package shop.api.DTO;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@NoArgsConstructor


public class CategoryResponseDTO {
    private Long id;
    private String categoryName;
    private List<CategoryResponseDTO> children;

    public CategoryResponseDTO(Long id, String categoryName, List<CategoryResponseDTO> children) {
        this.id = id;
        this.categoryName = categoryName;

        this.children = children;

    }


}