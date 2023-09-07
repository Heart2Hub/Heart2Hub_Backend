package com.Heart2Hub.Heart2Hub_Backend.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
@Table(name = "paymentMethod")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentMethodId;

    @NotNull
    private Long paymentMethodTokenId;

    public PaymentMethod(Long paymentMethodTokenId) {
        this.paymentMethodTokenId = paymentMethodTokenId;
    }

    public PaymentMethod(){}
}
