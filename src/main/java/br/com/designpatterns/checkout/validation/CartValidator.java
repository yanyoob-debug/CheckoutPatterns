package br.com.designpatterns.checkout.validation;

import br.com.designpatterns.checkout.domain.Order;

public final class CartValidator extends OrderValidator {
    public CartValidator(OrderValidator next) {
        super(next);
    }

    @Override
    protected void check(Order order) {
        if (order.items().isEmpty()) {
            throw new OrderValidationException("O carrinho deve conter pelo menos um item.");
        }
    }
}
