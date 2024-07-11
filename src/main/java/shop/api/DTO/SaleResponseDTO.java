package shop.api.DTO;

import javax.persistence.*;
import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import shop.api.models.Sale;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
public class SaleResponseDTO {
    private Long id;

    private Long numberSale;

    private String startDate;

    private String endDate;

    public SaleResponseDTO(Sale sale) {
        this.id = sale.getId();
        this.numberSale = sale.getNumberSale();
        this.startDate = sale.getStartDate().toString();
        this.endDate = sale.getEndDate().toString();
    }

}
