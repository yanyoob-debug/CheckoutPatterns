package br.com.designpatterns.checkout.domain;

import java.math.BigDecimal;

/** Resultado do cálculo, sem cobrança ou persistência. */
public record Receipt(String customerEmail, BigDecimal subtotal, BigDecimal discount, BigDecimal total) {
}
