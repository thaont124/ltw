package shop.api.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.models.Choice;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChoiceDTO {
    @Id
    private Long id;

    private String choiceName;

    private String choiceValue;

    public ChoiceDTO(Choice choice){
        this.id = choice.getId();
        this.choiceName = choice.getChoiceName();
        this.choiceValue = choice.getChoiceValue();
    }
}
