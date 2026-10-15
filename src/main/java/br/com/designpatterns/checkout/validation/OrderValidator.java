package br.com.designpatterns.checkout.validation;

import br.com.designpatterns.checkout.domain.Order;

/** Chain of Responsibility: uma falha interrompe a propagação ao próximo elo. */
public abstract class OrderValidator {
    private final OrderValidator next;

    protected OrderValidator(OrderValidator next) {
        this.next = next;
    }

    public final void validate(Order order) {
        check(order);
        if (next != null) {
            next.validate(order);
        }
    }

    protected abstract void check(Order order);
}
