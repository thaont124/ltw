package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shop.api.models.Sale;
import shop.api.repository.SaleRepository;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {
    @Autowired
    private SaleRepository saleRepository;
    public List<Sale> findSaleByProductVariantId(Long idVariant){
        return saleRepository.findSaleByProductVariantId(idVariant);
    }

    public Sale findById(Long idSale){
        return saleRepository.findById(idSale).orElse(null);
    }

    public void saveSale(Sale sale){
        saleRepository.save(sale);
    }

    public void saveSale(List<Sale> sales, Long variantId){
        List<Sale> oldSales = saleRepository.findSaleByProductVariantId(variantId);
        for (Sale sale : oldSales){
            if (sale.getEndDate().isBefore(LocalDateTime.now()))
                saleRepository.delete(sale);
            if (!sales.contains(sale))
                saleRepository.delete(sale);
            else sales.remove(sale);
        }
        for (Sale sale : sales){
            saleRepository.save(sale);
        }
    }

    public Sale assignSale(@Valid Sale sale) {
        return saleRepository.save(sale);
    }

    public boolean existedSale(LocalDateTime start, LocalDateTime end, Long id){
        return saleRepository.existsBetweenDates(start,end, id);
    }

    public Sale getSaleByProductVariantIdToday(Long idVariant){
        return saleRepository.getSaleByProductVariantIdToday(idVariant);
    }

    public Sale findSaleBetweenDates(LocalDateTime startDate, LocalDateTime enDate, Long variantId){
        return saleRepository.findSaleBetweenDates(startDate,enDate,variantId);
    }

}