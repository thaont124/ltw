package shop.api.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import shop.api.validation.UniqueField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Long.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table( uniqueConstraints = { @UniqueConstraint(columnNames = { "productCode" }) })
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @UniqueField(message = "Product is exist", entityClass = Product.class, fieldName = "productCode")
    private  String productCode;

    @NotNull(message = "productName is not  null")
    private String productName;

    @NotNull
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String description;

    @NotNull
    private LocalDateTime publishedDate;

    @NotNull
    private String status;

    private Long orderSort;



}
