package br.com.designpatterns.checkout.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/** Cópia imutável dos itens; as regras de checkout ficam na cadeia de validação. */
public record Order(String customerEmail, List<Item> items) {
    public Order {
        Objects.requireNonNull(customerEmail, "E-mail obrigatório.");
        items = List.copyOf(items);
    }

    public BigDecimal subtotal() {
        return items.stream().map(Item::subtotal).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
}
