package sqldiff.spring.boot.advanced.sample.data.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
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
