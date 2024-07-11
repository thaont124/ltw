package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.models.Choice;
import shop.api.models.Variant;
import shop.api.models.VariantChoice;
import shop.api.repository.ChoiceRepository;
import shop.api.repository.VariantChoiceRepository;

import java.util.List;

@Service
public class VariantChoiceService {
    @Autowired
    private VariantChoiceRepository variantChoiceRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    public VariantChoice saveVariantChoice(Variant variant, Choice choice) {
        VariantChoice variantChoice = new VariantChoice(null, variant, choice);
        if (!variantChoiceRepository.existsVariantChoiceByChoiceAndVariant(choice, variant))
            return variantChoiceRepository.save(variantChoice);
        return variantChoiceRepository.findVariantChoiceByChoiceAndVariant(choice, variant);
    }

    public void saveVariantChoice(Variant variant, List<Choice> choices) {

        List<VariantChoice> variantChoiceList = variantChoiceRepository.findVariantChoiceByVariant(variant);
        for (Choice choice : choices) {
            if (!variantChoiceRepository.existsVariantChoiceByChoiceAndVariant(choice, variant)) {
                VariantChoice variantChoice = new VariantChoice(null, variant, choice);
                variantChoiceRepository.save(variantChoice);
            } else {
                VariantChoice variantChoice = variantChoiceRepository.findVariantChoiceByChoiceAndVariant(choice, variant);
                variantChoiceList.remove(variantChoice);
            }
        }
        for (VariantChoice variantChoice : variantChoiceList) {
            variantChoiceRepository.delete(variantChoice);

        }

    }
}
