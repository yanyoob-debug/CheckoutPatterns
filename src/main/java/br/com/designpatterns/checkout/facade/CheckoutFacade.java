package br.com.designpatterns.checkout.facade;

import br.com.designpatterns.checkout.domain.Order;
import br.com.designpatterns.checkout.domain.Receipt;
import br.com.designpatterns.checkout.strategy.DiscountStrategy;
import br.com.designpatterns.checkout.validation.CartValidator;
import br.com.designpatterns.checkout.validation.CustomerValidator;
import br.com.designpatterns.checkout.validation.OrderValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/** Facade: expõe uma operação simples que coordena validação e desconto. */
public final class CheckoutFacade {
    private final OrderValidator validators;

    public CheckoutFacade() {
        validators = new CustomerValidator(new CartValidator(null));
    }

    public Receipt checkout(Order order, DiscountStrategy strategy) {
        Objects.requireNonNull(order, "Pedido obrigatório.");
        Objects.requireNonNull(strategy, "Estratégia obrigatória.");
        validators.validate(order);
        BigDecimal subtotal = order.subtotal();
        BigDecimal discount = Objects.requireNonNull(strategy.calculate(subtotal), "Desconto obrigatório.")
                .setScale(2, RoundingMode.UNNECESSARY);
        if (discount.signum() < 0 || discount.compareTo(subtotal) > 0) {
            throw new IllegalArgumentException("Desconto deve estar entre zero e o subtotal.");
        }
        return new Receipt(order.customerEmail(), subtotal, discount, subtotal.subtract(discount));
    }
}
