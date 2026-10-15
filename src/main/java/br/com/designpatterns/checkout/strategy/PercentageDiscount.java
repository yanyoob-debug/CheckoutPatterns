package br.com.designpatterns.checkout.strategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class PercentageDiscount implements DiscountStrategy {
    private final BigDecimal percentage;

    public PercentageDiscount(BigDecimal percentage) {
        this.percentage = Objects.requireNonNull(percentage, "Percentual obrigatório.");
        if (percentage.signum() < 0 || percentage.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Percentual deve estar entre 0 e 100.");
        }
    }

    @Override
    public BigDecimal calculate(BigDecimal subtotal) {
        return subtotal.multiply(percentage).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }
}
