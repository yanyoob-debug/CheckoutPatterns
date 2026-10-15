package br.com.designpatterns.checkout;

import br.com.designpatterns.checkout.domain.Item;
import br.com.designpatterns.checkout.domain.Order;
import br.com.designpatterns.checkout.domain.Receipt;
import br.com.designpatterns.checkout.facade.CheckoutFacade;
import br.com.designpatterns.checkout.strategy.NoDiscount;
import br.com.designpatterns.checkout.strategy.PercentageDiscount;
import br.com.designpatterns.checkout.validation.OrderValidationException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        var checkout = new CheckoutFacade();
        var order = new Order("cliente@example.com", List.of(
                new Item("Teclado", new BigDecimal("150.00"), 1),
                new Item("Mouse", new BigDecimal("75.00"), 2)));

        print("Compra sem desconto", checkout.checkout(order, new NoDiscount()));
        print("Compra com desconto de 10%", checkout.checkout(order,
                new PercentageDiscount(new BigDecimal("10"))));

        try {
            checkout.checkout(new Order("cliente@example.com", List.of()), new NoDiscount());
        } catch (OrderValidationException exception) {
            System.out.println("Pedido recusado: " + exception.getMessage());
        }
    }

    private static void print(String title, Receipt receipt) {
        System.out.printf(Locale.forLanguageTag("pt-BR"),
                "%s%nSubtotal: R$ %.2f | Desconto: R$ %.2f | Total: R$ %.2f%n%n",
                title, receipt.subtotal(), receipt.discount(), receipt.total());
    }
}
