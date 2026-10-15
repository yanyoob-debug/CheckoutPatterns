package br.com.designpatterns.checkout.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Um item usa valores decimais exatos e exige quantidade e preço positivos. */
public record Item(String name, BigDecimal unitPrice, int quantity) {
    public Item {
        Objects.requireNonNull(name, "Nome obrigatório.");
        Objects.requireNonNull(unitPrice, "Preço obrigatório.");
        if (name.isBlank() || unitPrice.signum() <= 0 || quantity <= 0) {
            throw new IllegalArgumentException("Informe nome, preço e quantidade válidos.");
        }
        unitPrice = unitPrice.setScale(2, RoundingMode.UNNECESSARY);
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
