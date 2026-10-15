package br.com.designpatterns.checkout.strategy;

import java.math.BigDecimal;

public final class NoDiscount implements DiscountStrategy {
    @Override
    public BigDecimal calculate(BigDecimal subtotal) {
        return new BigDecimal("0.00");
    }
}
