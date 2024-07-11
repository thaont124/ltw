package shop.api.models;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull
    @Min(value = 0, message = "numberSale must be greater than 0 and smaller than 100")
    @Max(value = 100, message = "numberSale must be greater than 0 and smaller than 100")
    private Long numberSale;

    @NotNull
    private LocalDateTime startDate;

    @NotNull()
    private LocalDateTime endDate;
    @ManyToOne
    @JoinColumn(name = "variant_id", referencedColumnName = "id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Variant variant;
}
