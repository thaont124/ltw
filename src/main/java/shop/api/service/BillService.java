package shop.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import shop.api.DTO.*;
import shop.api.exception.InvalidInputException;
import shop.api.exception.NotFoundException;
import shop.api.models.*;
import shop.api.repository.BillRepository;
import shop.api.repository.DeliveryRepository;
import shop.api.repository.PhotoRepository;
import shop.api.repository.VariantRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class BillService {
    @Autowired
    private BillRepository billRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private BillProductService billProductService;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private SaleService saleService;
    @Autowired
    private VariantRepository variantRepository;

    @Autowired
    private PhotoService photoService;

    @Value("${domain}")
    private String domain;
    @Autowired
    private DeliveryRepository deliveryRepository;

    public Bill addBill(Bill request){
        return billRepository.save(request);
    }

    private  int compareLocalDateTime(LocalDateTime a, LocalDateTime b) {
        if (a.isBefore(b)) {
            return -1;
        } else if (a.isAfter(b)) {
            return 1;
        } else {
            return 0;
        }
    }

    public OrderResponseDTO addOrder(OrderRequestDTO orderRequestDTO){
        List<Variant> variantList =new ArrayList<>();
        List<ProductBuyDTO> productsBuy = orderRequestDTO.getProductList();
        for(ProductBuyDTO productBuy:productsBuy){
            Product product =productService.findById(productBuy.getId());
            Variant variant = variantRepository.findById(productBuy.getProductVariantId()).orElse(null);
            if(product==null || variant ==null){
                throw  new NotFoundException("productList is not exits");
            }
            variantList.add(variant);
        }
        Bill newBill =new Bill(null, LocalDateTime.now());
        addBill(newBill);
        Delivery delivery =new Delivery(null, orderRequestDTO.getArrived(),orderRequestDTO.getEmail(),orderRequestDTO.getPhone(),"Đang Giao Hàng",newBill);
        deliveryService.addDelivery(delivery);

        OrderResponseDTO orderResponse = new OrderResponseDTO();
        orderResponse.setDelivery(delivery);
        orderResponse.setOrderDate(newBill.getOrderDate().toString());
        orderResponse.setId(newBill.getId());

        List<ProductOrderedDTO> productsDTO = new ArrayList<>();

        for(int j = 0; j < variantList.size(); j++){
            Variant variant = variantList.get(j);
            BillVariant newBillVariant =new BillVariant();
            List<Sale> saleList =saleService.findSaleByProductVariantId(variant.getId());
            newBillVariant.setBill(newBill);
            newBillVariant.setVariant(variant);
            newBillVariant.setPrice(variant.getOriginPrice());

            for(int i=0;i<saleList.size();i++){
                if(compareLocalDateTime(LocalDateTime.now(),saleList.get(i).getStartDate())>=0&&compareLocalDateTime(saleList.get(i).getStartDate(),LocalDateTime.now())<=0){
                    newBillVariant.setPrice(variant.getOriginPrice()*(100-saleList.get(i).getNumberSale())/100);
                    break;
                }
            }
            newBillVariant.setQuantity(orderRequestDTO.getProductList().get(j).getQuantity());

            billProductService.addBillProduct(newBillVariant);

            Product product = variantRepository.findProductByProductVariantId(variant.getId());
            List<Photo> photoList = photoService.getPhotoByProduct(product);
            ProductOrderedDTO productResponse = new ProductOrderedDTO(product, domain, photoList);
            productResponse.setQuantity(newBillVariant.getQuantity());
            productResponse.setTotalPrice(newBillVariant.getPrice());
            productResponse.setVariantsDTO(new VariantResponseDTO(variant)) ;
            productsDTO.add(productResponse);
        }

        orderResponse.setProducts(productsDTO);

    return  orderResponse;
    }

    public List<Bill> getBills(LocalDateTime fromDate, LocalDateTime toDate){
        return billRepository.getBillBetweenFromDateAndToDate(fromDate, toDate);
    }

    public Bill getBillById(Long idBill){
        return billRepository.findById(idBill).orElse(null);
    }

    public OrderResponseDTO changeStatus(Long idOrder) {
        Bill bill = billRepository.findById(idOrder).orElse(null);
        Delivery delivery = deliveryRepository.getDeliveryByBill_Id(idOrder);
        if(bill==null)
            throw new InvalidInputException("Product Id is not existed");
        if (delivery.getStatus().equals("Đang Giao Hàng"))
            delivery.setStatus("Đã giao hàng");
        deliveryRepository.save(delivery);
        OrderResponseDTO orderDTO = new OrderResponseDTO();
        orderDTO.setDelivery(delivery);
        orderDTO.setId(bill.getId());
        orderDTO.setOrderDate(bill.getOrderDate().toString());
        List<BillVariant> billVariants = billProductService.getBillVariantByBillId(bill.getId());
        List<ProductOrderedDTO> productsResponse = new ArrayList<>();
        for (BillVariant billVariant : billVariants){
            Product product = productService.findById(billVariant.getVariant().getProduct().getId());
            Variant variant = variantRepository.findById(billVariant.getVariant().getId()).get();
            VariantResponseDTO variantResponseDTO = new VariantResponseDTO(variant);
            List<Photo> photoList = photoService.getPhotoByProduct(product);
            ProductOrderedDTO productDTO = new ProductOrderedDTO(product, domain, photoList);
            productDTO.setTotalPrice(billVariant.getPrice());
            productDTO.setVariantsDTO(variantResponseDTO);
            productDTO.setQuantity(billVariant.getQuantity());
            productsResponse.add(productDTO);
        }
        orderDTO.setProducts(productsResponse);
        return orderDTO;
    }
}
