package ee.commerce.order.payment;

import ee.commerce.order.exception.PaymentFailedException;
import ee.commerce.order.model.OrderStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Payment processor that uses the Strategy pattern to process payments.
 * 
 * This is the Context class in the Strategy pattern.
 * It maintains a reference to a PaymentStrategy and delegates payment processing to it.
 * 
 * Following SOLID principles:
 * - Single Responsibility: Only handles payment processing coordination
 * - Open/Closed: Open for extension (new payment strategies), closed for modification
 * - Dependency Inversion: Depends on PaymentStrategy abstraction, not concrete implementations
 */
public class PaymentProcessor {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessor.class);
    
    private PaymentStrategy paymentStrategy;
    
    /**
     * Creates a payment processor with a specific payment strategy.
     * 
     * @param paymentStrategy the payment strategy to use
     */
    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        if (paymentStrategy == null) {
            throw new IllegalArgumentException("Payment strategy cannot be null");
        }
        this.paymentStrategy = paymentStrategy;
        logger.debug("Payment processor initialized with strategy: {}", 
                    paymentStrategy.getPaymentMethodName());
    }
    
    /**
     * Sets a new payment strategy.
     * Allows changing payment method at runtime (Strategy pattern benefit).
     * 
     * @param paymentStrategy the new payment strategy
     */
    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        if (paymentStrategy == null) {
            throw new IllegalArgumentException("Payment strategy cannot be null");
        }
        logger.info("Switching payment strategy from {} to {}", 
                   this.paymentStrategy.getPaymentMethodName(),
                   paymentStrategy.getPaymentMethodName());
        this.paymentStrategy = paymentStrategy;
    }
    
    /**
     * Processes a payment using the current strategy.
     * 
     * @param amount the amount to charge
     * @param orderReference the order reference/ID
     * @return PaymentResult containing transaction details
     */
    public PaymentResult processPayment(BigDecimal amount, String orderReference) {
        logger.info("Starting payment process for order {} using {}", 
                   orderReference, paymentStrategy.getPaymentMethodName());
        
        // Validate amount
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid payment amount: {}", amount);
            return new PaymentResult(false, null, OrderStatus.FAILED, 
                                   "Invalid amount", paymentStrategy.getPaymentMethodName());
        }
        
        // Check if payment method can process this amount
        if (!paymentStrategy.canProcess(amount)) {
            logger.warn("Payment method {} cannot process amount {}€", 
                       paymentStrategy.getPaymentMethodName(), amount);
            return new PaymentResult(false, null, OrderStatus.FAILED,
                                   "Amount exceeds payment method limit",
                                   paymentStrategy.getPaymentMethodName());
        }
        
        try {
            // Delegate to strategy
            String transactionId = paymentStrategy.processPayment(amount, orderReference);
            
            logger.info("Payment processed successfully via {}: Transaction ID {}",
                       paymentStrategy.getPaymentMethodName(), transactionId);
            
            return new PaymentResult(true, transactionId, OrderStatus.PAID,
                                   "Payment successful", paymentStrategy.getPaymentMethodName());
            
        } catch (PaymentFailedException e) {
            logger.error("Payment failed via {}: {}", 
                        paymentStrategy.getPaymentMethodName(), e.getReason(), e);
            
            return new PaymentResult(false, null, OrderStatus.FAILED,
                                   e.getReason(), paymentStrategy.getPaymentMethodName());
        } catch (Exception e) {
            logger.error("Unexpected error during payment processing", e);
            
            return new PaymentResult(false, null, OrderStatus.FAILED,
                                   "Unexpected error: " + e.getMessage(),
                                   paymentStrategy.getPaymentMethodName());
        }
    }
    
    /**
     * Gets the current payment strategy.
     * 
     * @return current payment strategy
     */
    public PaymentStrategy getPaymentStrategy() {
        return paymentStrategy;
    }
    
    /**
     * Result object containing payment processing details.
     * Immutable value object following best practices.
     */
    public static class PaymentResult {
        private final boolean success;
        private final String transactionId;
        private final OrderStatus orderStatus;
        private final String message;
        private final String paymentMethod;
        
        public PaymentResult(boolean success, String transactionId, OrderStatus orderStatus,
                           String message, String paymentMethod) {
            this.success = success;
            this.transactionId = transactionId;
            this.orderStatus = orderStatus;
            this.message = message;
            this.paymentMethod = paymentMethod;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getTransactionId() {
            return transactionId;
        }
        
        public OrderStatus getOrderStatus() {
            return orderStatus;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getPaymentMethod() {
            return paymentMethod;
        }
        
        @Override
        public String toString() {
            return String.format("PaymentResult{success=%s, transactionId='%s', status=%s, method=%s}",
                               success, transactionId, orderStatus, paymentMethod);
        }
    }
}
