package br.com.designpatterns.checkout.validation;

import br.com.designpatterns.checkout.domain.Order;

public final class CustomerValidator extends OrderValidator {
    public CustomerValidator(OrderValidator next) {
        super(next);
    }

    @Override
    protected void check(Order order) {
        // Verificação didática de formato, sem confirmar a existência do endereço.
        if (!order.customerEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new OrderValidationException("Informe um e-mail válido.");
        }
    }
}
