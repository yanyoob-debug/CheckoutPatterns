package br.com.designpatterns.checkout.strategy;

import java.math.BigDecimal;

/** Strategy: permite trocar o cálculo do desconto sem alterar o checkout. */
@FunctionalInterface
public interface DiscountStrategy {
    BigDecimal calculate(BigDecimal subtotal);
}
