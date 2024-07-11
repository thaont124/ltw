package shop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import shop.api.DTO.*;
import shop.api.exception.InvalidInputException;
import shop.api.models.*;
import shop.api.service.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/v1.0")
public class OrderController {
    @Autowired
    private BillService billService;

    @Autowired
    private BillProductService billProductService;

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private VariantService variantService;

    @Autowired
    private PhotoService photoService;

    @Autowired
    private ChoiceService choiceService;

    @Autowired
    private SaleService saleService;

    @Value("${domain}")
    private String domain;
    @PostMapping("/Order")
    public ResponseEntity<?> newOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO){
        if(orderRequestDTO.getProductList() == null){
            throw new InvalidInputException("Please choose at least a product");
        } else if (orderRequestDTO.getProductList() != null &&orderRequestDTO.getProductList().size() == 0){
            throw new InvalidInputException("Please choose at least a product");
        }
        return new ResponseEntity<>(billService.addOrder(orderRequestDTO), HttpStatus.CREATED);
    }
    @Secured({"ROLE_ADMIN"})
    @GetMapping("/OrdersPage")
    public ResponseEntity<?> getListOrderPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "8") Integer pageSize,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime toDate
    ){
        if (fromDate == null) {
            // Nếu fromDate không được cung cấp, sử dụng LocalDate hiện tại trừ đi 30 ngày
            fromDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        }

        if (toDate == null) {
            // Nếu toDate không được cung cấp, sử dụng LocalDate hiện tại
            toDate = LocalDateTime.now();
        }
        if (!fromDate.isBefore(toDate))
            throw new InvalidInputException("The end day must be after the start-day");
        List<OrderResponseDTO> ordersResponse = new ArrayList<>();
        List<Bill> bills = billService.getBills(fromDate, toDate);
        for (Bill bill : bills){
            OrderResponseDTO orderDTO = new OrderResponseDTO();
            Delivery delivery = deliveryService.getDelivery(bill.getId());
            orderDTO.setDelivery(delivery);
            orderDTO.setId(bill.getId());
            orderDTO.setOrderDate(bill.getOrderDate().toString());
            List<BillVariant> billVariants = billProductService.getBillVariantByBillId(bill.getId());
            List<ProductOrderedDTO> productsResponse = new ArrayList<>();
            for (BillVariant billVariant : billVariants){
                Product product = productService.findById(billVariant.getVariant().getProduct().getId());
                Variant variant = variantService.findVariantByid(billVariant.getVariant().getId());
                VariantResponseDTO variantResponseDTO = new VariantResponseDTO(variant);
                List<Photo> photoList = photoService.getPhotoByProduct(product);
                ProductOrderedDTO productDTO = new ProductOrderedDTO(product, domain, photoList);
                productDTO.setTotalPrice(billVariant.getPrice());
                productDTO.setVariantsDTO(variantResponseDTO);
                productDTO.setQuantity(billVariant.getQuantity());
                productsResponse.add(productDTO);
            }
            orderDTO.setProducts(productsResponse);
            ordersResponse.add(orderDTO);
        }

        Collections.sort(ordersResponse, (o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));


        int totalProducts = ordersResponse.size();
        int startIndex = Math.min((pageNo - 1) * pageSize, totalProducts);
        int endIndex = Math.min(startIndex + pageSize, totalProducts);

        List<OrderResponseDTO> paginatedProducts = ordersResponse.subList(startIndex, endIndex);
        Page<OrderResponseDTO> page = new PageImpl<>(paginatedProducts, PageRequest.of(pageNo - 1, pageSize), totalProducts);
        return new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/Orders")
    public ResponseEntity<?> getListOrder(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss") LocalDateTime toDate
    ){
        if (fromDate == null) {
            // Nếu fromDate không được cung cấp, sử dụng LocalDate hiện tại trừ đi 30 ngày
            fromDate = LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        }

        if (toDate == null) {
            // Nếu toDate không được cung cấp, sử dụng LocalDate hiện tại
            toDate = LocalDateTime.now();
        }
        if (!fromDate.isBefore(toDate))
            throw new InvalidInputException("The end day must be after the start-day");
        List<OrderResponseDTO> ordersResponse = new ArrayList<>();
        List<Bill> bills = billService.getBills(fromDate, toDate);
        for (Bill bill : bills){
            OrderResponseDTO orderDTO = new OrderResponseDTO();
            Delivery delivery = deliveryService.getDelivery(bill.getId());
            orderDTO.setDelivery(delivery);
            orderDTO.setId(bill.getId());
            orderDTO.setOrderDate(bill.getOrderDate().toString());
            List<BillVariant> billVariants = billProductService.getBillVariantByBillId(bill.getId());
            List<ProductOrderedDTO> productsResponse = new ArrayList<>();
            for (BillVariant billVariant : billVariants){
                Product product = productService.findById(billVariant.getVariant().getProduct().getId());
                Variant variant = variantService.findVariantByid(billVariant.getVariant().getId());
                VariantResponseDTO variantResponseDTO = new VariantResponseDTO(variant);
                List<Photo> photoList = photoService.getPhotoByProduct(product);
                ProductOrderedDTO productDTO = new ProductOrderedDTO(product, domain, photoList);
                productDTO.setTotalPrice(billVariant.getPrice());
                productDTO.setVariantsDTO(variantResponseDTO);
                productDTO.setQuantity(billVariant.getQuantity());
                List<Variant> variants = productService.findVariant(product.getId());

                //convert variant to variantDTO
                VariantResponseDTO variantResponse = new VariantResponseDTO(variant);

                List<Choice> savedChoices = choiceService.findChoiceByVariantId(variant.getId());
                List<ChoiceDTO> savedChoicesResponse = new ArrayList<>();
                if (savedChoices != null) {
                    if (savedChoices.size() > 0) {
                        for (Choice choice : savedChoices) {
                            savedChoicesResponse.add(new ChoiceDTO(choice));
                        }
                        variantResponse.setChoices(savedChoicesResponse);
                    }
                }


                List<Sale> sales = saleService.findSaleByProductVariantId(variant.getId());
                Collections.sort(sales, new Comparator<Sale>() {
                    @Override
                    public int compare(Sale sale1, Sale sale2) {
                        return sale1.getStartDate().compareTo(sale2.getStartDate());
                    }
                });
                List<SaleResponseDTO> salesResponse = new ArrayList<>();
                for (Sale sale : sales) {
                    salesResponse.add(new SaleResponseDTO(sale));
                }
                variantResponse.setSale(salesResponse);
                productDTO.setVariantsDTO(variantResponse);
                productsResponse.add(productDTO);
            }
            orderDTO.setProducts(productsResponse);
            ordersResponse.add(orderDTO);
        }

        Collections.sort(ordersResponse, (o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()));


        return new ResponseEntity<>(ordersResponse, HttpStatus.OK);
    }

    @PatchMapping("changeDeliveryStatus/{idBill}")
    public ResponseEntity<?> changeDeliveryStatus(@PathVariable("idBill") Long idBill){
        Bill bill = billService.getBillById(idBill);
        if(bill==null)
            throw new InvalidInputException("Bill Id is not existed");

        Delivery delivery = deliveryService.changeStatus(idBill);

        OrderResponseDTO orderResponse = new OrderResponseDTO();
        orderResponse.setDelivery(delivery);

        return new ResponseEntity<>(orderResponse, HttpStatus.OK);
    }

    @PatchMapping("changeOrderStatus/{idOrder}")
    public ResponseEntity<?> changeProOrderStatus(@PathVariable("idOrder") Long idOrder){

        return new ResponseEntity<>(billService.changeStatus(idOrder), HttpStatus.OK);
    }

    public void convertListVariantToVariantDTO(List<Variant> variants, List<VariantResponseDTO> variantsDTO) {
        for (Variant variant : variants) {
            VariantResponseDTO variantResponse = new VariantResponseDTO(variant);

            List<Choice> savedChoices = choiceService.findChoiceByVariantId(variant.getId());
            List<ChoiceDTO> savedChoicesResponse = new ArrayList<>();
            if (savedChoices != null) {
                if (savedChoices.size() > 0) {
                    for (Choice choice : savedChoices) {
                        savedChoicesResponse.add(new ChoiceDTO(choice));
                    }
                    variantResponse.setChoices(savedChoicesResponse);
                }
            }


            List<Sale> sales = saleService.findSaleByProductVariantId(variant.getId());
            Collections.sort(sales, new Comparator<Sale>() {
                @Override
                public int compare(Sale sale1, Sale sale2) {
                    return sale1.getStartDate().compareTo(sale2.getStartDate());
                }
            });
            List<SaleResponseDTO> salesResponse = new ArrayList<>();
            for (Sale sale : sales) {
                salesResponse.add(new SaleResponseDTO(sale));
            }
            variantResponse.setSale(salesResponse);
            variantsDTO.add(variantResponse);
        }
    }

}
