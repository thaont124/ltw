package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.models.BillVariant;
import shop.api.models.Product;
import shop.api.repository.BillVariantRepository;

import java.util.List;

@Service

public class BillProductService {
    @Autowired
    private BillVariantRepository billVariantRepository;
    public BillVariant addBillProduct(BillVariant billVariant){
        return billVariantRepository.save(billVariant);
    }

    public List<Product> getProductByBillId(Long idBill){
        return billVariantRepository.getProductByBill_Id(idBill);
    }

    public List<BillVariant> getBillVariantByBillId(Long idBill){
        return billVariantRepository.getBillVariantByBill_Id(idBill);
    }
}
