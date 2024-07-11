package shop.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import shop.api.DTO.ProductResponseDTO;

import java.util.Collections;
import java.util.List;

public class CommonService {

    public static ResponseEntity<?> getResponsePagination(@RequestParam(defaultValue = "1") Integer pageNo, @RequestParam(defaultValue = "10") Integer pageSize, List<ProductResponseDTO> productsResponse) {
        Collections.sort(productsResponse, (o1, o2) -> Long.compare(o2.getOrderSort(), o1.getOrderSort()));

        int totalProducts = productsResponse.size();
        int startIndex = Math.min((pageNo - 1) * pageSize, totalProducts);
        int endIndex = Math.min(startIndex + pageSize, totalProducts);

        List<ProductResponseDTO> paginatedProducts = productsResponse.subList(startIndex, endIndex);
        Page<ProductResponseDTO> page = new PageImpl<>(paginatedProducts, PageRequest.of(pageNo - 1, pageSize), totalProducts);

        return  new ResponseEntity<>(page.getContent(), HttpStatus.OK);
    }


}
