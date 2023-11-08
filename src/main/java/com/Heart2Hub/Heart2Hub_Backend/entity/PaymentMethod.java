package com.Heart2Hub.Heart2Hub_Backend.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "paymentMethod")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @NotNull
    @Column(unique = true)
    private UUID paymentMethodNehrId = UUID.randomUUID();

    @NotNull
    private Long paymentMethodTokenId;

    public PaymentMethod(Long paymentMethodTokenId) {
        this.paymentMethodTokenId = paymentMethodTokenId;
    }

    public PaymentMethod(){}
}
