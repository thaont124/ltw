package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Category;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class CategoryRequestDTO {

    @NotNull(message = "categoryName is required")
    @NotBlank(message = "categoryName is required")
    private String categoryName;

    private Long parent;

    public CategoryRequestDTO(Category category){

        this.categoryName = category.getCategoryName();
        this.parent = category.getParent().getId();
    }


}