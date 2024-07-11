package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.models.Choice;
import shop.api.repository.ChoiceRepository;

import java.util.List;

@Service
public class ChoiceService {
    @Autowired
    private ChoiceRepository choiceRepository;

    @Autowired
    private VariantChoiceService variantChoiceRepository;

    public Choice save(Choice choice){
        Choice savedChoice = new Choice();
        if (!choiceRepository.existsByChoiceNameAndChoiceValue(capitalizeFirstLetter(choice.getChoiceName()), capitalizeFirstLetter(choice.getChoiceValue()))) {
            savedChoice = new Choice(choice.getId(), capitalizeFirstLetter(choice.getChoiceName()), capitalizeFirstLetter(choice.getChoiceValue()));
            return choiceRepository.save(savedChoice);
        }
        else savedChoice = choiceRepository.findChoiceByChoiceNameAndChoiceValue(capitalizeFirstLetter(choice.getChoiceName()), capitalizeFirstLetter(choice.getChoiceValue()));
        return savedChoice;
    }

    public static String capitalizeFirstLetter(String input) {
        String[] words = input.toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();

        for (String word : words) {
            if (word.length() > 0) {
                char firstLetter = Character.toUpperCase(word.charAt(0));
                String restOfWord = word.substring(1);
                sb.append(firstLetter).append(restOfWord).append(" ");
            }
        }

        return sb.toString().trim();
    }

    public List<Choice> findChoiceByVariantId(Long variantId){
        return choiceRepository.findChoiceByVariantId(variantId);
    }

    public Choice getChoiceByNameAndValue(String choiceName, String choiceValue){
        return choiceRepository.findChoiceByChoiceNameAndChoiceValue(choiceName, choiceValue);
    }
}




