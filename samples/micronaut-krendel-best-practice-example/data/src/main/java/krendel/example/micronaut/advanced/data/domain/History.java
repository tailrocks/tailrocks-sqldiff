package krendel.example.micronaut.advanced.data.domain;

import com.scentbird.hurma.hibernate.jpa.MutableEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;

@Entity
public class History extends MutableEntity {

    @Column(nullable = false)
    @NotNull
    private YearMonth yearMonth;

    @Column(nullable = false)
    @NotNull
    private String message;

    public YearMonth getYearMonth() {
        return yearMonth;
    }

    public History setYearMonth(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
        return this;
    }

}
