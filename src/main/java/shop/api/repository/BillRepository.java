package shop.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import shop.api.models.Bill;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    @Query("select b from Bill b where b.orderDate between :fromDate and :toDate")
    List<Bill> getBillBetweenFromDateAndToDate(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);


}
