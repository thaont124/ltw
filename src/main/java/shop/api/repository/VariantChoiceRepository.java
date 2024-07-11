package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import shop.api.models.Choice;
import shop.api.models.Variant;
import shop.api.models.VariantChoice;

import java.util.List;

public interface VariantChoiceRepository extends JpaRepository<VariantChoice, Long> {
    boolean existsVariantChoiceByChoiceAndVariant(Choice choice, Variant variant);

    VariantChoice findVariantChoiceByChoiceAndVariant(Choice choice, Variant variant);

    List<VariantChoice> findVariantChoiceByVariant(Variant variant);
}
