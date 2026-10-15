package br.com.designpatterns.checkout.validation;

public final class OrderValidationException extends IllegalArgumentException {
    public OrderValidationException(String message) {
        super(message);
    }
}
