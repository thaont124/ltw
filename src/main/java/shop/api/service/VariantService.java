package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.models.Product;
import shop.api.models.Variant;
import shop.api.repository.ChoiceRepository;
import shop.api.repository.VariantRepository;

import java.util.List;

@Service
public class VariantService {

    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private ChoiceRepository choiceRepository;

    public void saveProductVariants(List<Variant> variants, Long idProduct){
        List<Variant> variantByProduct = variantRepository.getProductVariantByProductId(idProduct);
        for (Variant variant : variantByProduct){
            if (!variants.contains(variant))
                variantRepository.delete(variant);
            else variants.remove(variant);
        }
        for (Variant variant : variants){
            variantRepository.save(variant);
        }
    }

    public Variant findVariantByProductANdOriginPrice(Product product, Float originPrice){
        return variantRepository.getVariantByProductAndOriginPrice(product, originPrice);
    }

    public Variant findVariantByid(Long id){
        return variantRepository.findById(id).get();
    }

    public void deleteVariant(Variant variant){
        variantRepository.delete(variant);
    }

    public void saveProductVariants(List<Variant> variants){

        for (Variant variant : variants){
            variantRepository.save(variant);
        }
    }
    public Variant saveVarient(Variant variant){
        return variantRepository.save(variant);

    }
}
