package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.Choice;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice,Long> {
    Choice findChoiceByChoiceNameAndChoiceValue(String choiceName, String choiceValue);

    boolean existsByChoiceNameAndChoiceValue(String choiceName, String choiceValue);

    @Query("SELECT c FROM Choice c JOIN VariantChoice vc ON c.id = vc.choice.id WHERE vc.variant.id = :variantId")
    List<Choice> findChoiceByVariantId(@Param("variantId") Long variantId);
}
