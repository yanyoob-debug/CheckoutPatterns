package br.com.designpatterns.checkout;

import br.com.designpatterns.checkout.domain.Item;
import br.com.designpatterns.checkout.domain.Order;
import br.com.designpatterns.checkout.facade.CheckoutFacade;
import br.com.designpatterns.checkout.strategy.NoDiscount;
import br.com.designpatterns.checkout.strategy.PercentageDiscount;
import br.com.designpatterns.checkout.validation.OrderValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class CheckoutTest {
    private final CheckoutFacade checkout = new CheckoutFacade();

    private Order order(String price, int quantity) {
        return new Order("cliente@example.com", List.of(new Item("Livro", new BigDecimal(price), quantity)));
    }

    @Test
    void preservesSubtotalWithoutDiscount() {
        var receipt = checkout.checkout(order("75.00", 2), new NoDiscount());
        assertEquals(new BigDecimal("150.00"), receipt.subtotal());
        assertEquals(new BigDecimal("0.00"), receipt.discount());
        assertEquals(new BigDecimal("150.00"), receipt.total());
    }

    @Test
    void appliesPercentageToAllItems() {
        var order = new Order("cliente@example.com", List.of(
                new Item("Teclado", new BigDecimal("150.00"), 1),
                new Item("Mouse", new BigDecimal("75.00"), 2)));
        var receipt = checkout.checkout(order, new PercentageDiscount(new BigDecimal("10")));
        assertEquals(new BigDecimal("30.00"), receipt.discount());
        assertEquals(new BigDecimal("270.00"), receipt.total());
    }

    @Test
    void roundsDiscountHalfUpToCents() {
        var receipt = checkout.checkout(order("19.95", 1), new PercentageDiscount(new BigDecimal("10")));
        assertEquals(new BigDecimal("2.00"), receipt.discount());
        assertEquals(new BigDecimal("17.95"), receipt.total());
    }

    @Test
    void acceptsFullDiscount() {
        assertEquals(new BigDecimal("0.00"), checkout.checkout(order("50.00", 1),
                new PercentageDiscount(new BigDecimal("100"))).total());
    }

    @Test
    void rejectsEmptyCartBeforeCalculatingDiscount() {
        var called = new AtomicBoolean();
        var exception = assertThrows(OrderValidationException.class, () -> checkout.checkout(
                new Order("cliente@example.com", List.of()), subtotal -> {
                    called.set(true);
                    return BigDecimal.ZERO;
                }));
        assertEquals("O carrinho deve conter pelo menos um item.", exception.getMessage());
        assertFalse(called.get());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "cliente", "a@b", "a b@example.com", "a@@example.com"})
    void rejectsInvalidEmail(String email) {
        assertThrows(OrderValidationException.class, () -> checkout.checkout(
                new Order(email, order("10.00", 1).items()), new NoDiscount()));
    }

    @Test
    void stopsChainAtFirstFailure() {
        var exception = assertThrows(OrderValidationException.class, () -> checkout.checkout(
                new Order("inválido", List.of()), new NoDiscount()));
        assertEquals("Informe um e-mail válido.", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-1", "101"})
    void rejectsInvalidPercentage(String percentage) {
        assertThrows(IllegalArgumentException.class, () -> new PercentageDiscount(new BigDecimal(percentage)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "50.01"})
    void protectsTotalAgainstInvalidCustomStrategy(String discount) {
        assertThrows(IllegalArgumentException.class, () -> checkout.checkout(order("50.00", 1),
                subtotal -> new BigDecimal(discount)));
    }

    @Test
    void supportsNewStrategyWithoutChangingFacade() {
        var receipt = checkout.checkout(order("50.00", 1), subtotal -> new BigDecimal("5.00"));
        assertEquals(new BigDecimal("45.00"), receipt.total());
    }

    @Test
    void rejectsInvalidItems() {
        assertThrows(IllegalArgumentException.class, () -> new Item(" ", BigDecimal.ONE, 1));
        assertThrows(IllegalArgumentException.class, () -> new Item("Livro", BigDecimal.ZERO, 1));
        assertThrows(IllegalArgumentException.class, () -> new Item("Livro", BigDecimal.ONE, 0));
        assertThrows(ArithmeticException.class, () -> new Item("Livro", new BigDecimal("1.001"), 1));
    }

    @Test
    void keepsOrderIndependentFromOriginalList() {
        var items = new ArrayList<>(order("10.00", 1).items());
        var order = new Order("cliente@example.com", items);
        items.clear();
        assertEquals(new BigDecimal("10.00"), order.subtotal());
        assertThrows(UnsupportedOperationException.class, () -> order.items().clear());
    }
}
