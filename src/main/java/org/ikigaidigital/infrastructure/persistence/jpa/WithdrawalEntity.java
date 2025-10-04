package org.ikigaidigital.infrastructure.persistence.jpa;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "withdrawals")
@Getter
@Setter
public class WithdrawalEntity {
    @Id
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "time_deposit_id")
    private TimeDepositEntity timeDeposit;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;
}
