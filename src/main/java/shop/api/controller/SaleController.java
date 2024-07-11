package shop.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import shop.api.DTO.SaleResponseDTO;
import shop.api.exception.InvalidInputException;
import shop.api.exception.NotFoundException;
import shop.api.models.Product;
import shop.api.models.Sale;
import shop.api.service.BillService;
import shop.api.service.ProductService;
import shop.api.service.SaleService;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "api/v1.0", produces = "application/json")
public class SaleController {
    @Autowired
    private SaleService saleService;

    @Autowired
    private ProductService productService;
    @Autowired
    private BillService billService;
//    @Secured({"ROLE_ADMIN"})
//    @PostMapping("sale/{idProductVariant}")
//    public ResponseEntity<?> assignSale(@PathVariable Long idProductVariant, @RequestBody @Valid SaleResponseDTO saleResponseDTO){
//        Product product =productService.findById(idProductVariant);
//        if(product==null){
//            throw new NotFoundException("Product is not exits");
//        }
//        if (saleResponseDTO.getStartDate() == null || saleResponseDTO.getEndDate() == null)
//            throw new InvalidInputException("Please fill out start-day and end-day");
//        if (LocalDateTime.now().isAfter(saleResponseDTO.getEndDate()))
//            throw new InvalidInputException("That time is over");
//        if (saleService.existedSale(saleResponseDTO.getStartDate(), saleResponseDTO.getEndDate(), idProductVariant))
//            throw new InvalidInputException("Sale in that time is exist");
//
//        if (!saleResponseDTO.getEndDate().isAfter(saleResponseDTO.getStartDate()))
//            throw new InvalidInputException("The end day must be after the start-day");
//
//        Sale sale = new Sale(null, saleResponseDTO.getNumberSale(),
//                saleResponseDTO.getStartDate(), saleResponseDTO.getEndDate(),
//                null
//        );
//        return new ResponseEntity<>(saleService.assignSale(sale), HttpStatus.CREATED);
//    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("sale/{idProductVariant}")
    public ResponseEntity<?> getSale(@PathVariable Long idProductVariant){
        List<Sale> sales = saleService.findSaleByProductVariantId(idProductVariant);
        List<SaleResponseDTO> salesDTO = new ArrayList<>();
        for (Sale sale : sales){
            salesDTO.add(new SaleResponseDTO(sale));
        }
        Collections.sort(salesDTO, (o1, o2) -> o1.getStartDate().compareTo(o2.getStartDate()));
        return new ResponseEntity<>(salesDTO, HttpStatus.OK);
    }
}
