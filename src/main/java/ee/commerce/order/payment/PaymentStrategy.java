package ee.commerce.order.payment;

import ee.commerce.order.exception.PaymentFailedException;

import java.math.BigDecimal;

/**
 * Strategy interface for payment processing.
 * 
 * This is the core of the Strategy pattern implementation.
 * Different payment methods implement this interface with their specific logic.
 * 
 * Following the Strategy pattern from Gang of Four design patterns,
 * this allows the payment algorithm to vary independently from clients that use it.
 */
public interface PaymentStrategy {
    
    /**
     * Processes a payment for the given amount.
     * 
     * @param amount the amount to charge
     * @param orderReference the order reference/ID
     * @return transaction ID if successful
     * @throws PaymentFailedException if payment processing fails
     */
    String processPayment(BigDecimal amount, String orderReference) throws PaymentFailedException;
    
    /**
     * Gets the name of this payment method.
     * 
     * @return payment method name
     */
    String getPaymentMethodName();
    
    /**
     * Validates if this payment method can process the given amount.
     * 
     * @param amount the amount to validate
     * @return true if the amount can be processed
     */
    boolean canProcess(BigDecimal amount);
}
