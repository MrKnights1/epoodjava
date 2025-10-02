package ee.commerce.order.exception;

/**
 * Custom exception thrown when payment processing fails.
 * 
 * This exception is used to handle payment failures in a clean way,
 * following the best practice of using custom exceptions for domain-specific errors.
 */
public class PaymentFailedException extends Exception {
    
    private final String paymentMethod;
    private final String reason;
    
    /**
     * Creates a new payment failed exception.
     * 
     * @param paymentMethod the payment method that failed
     * @param reason the reason for failure
     */
    public PaymentFailedException(String paymentMethod, String reason) {
        super(String.format("Payment failed via %s: %s", paymentMethod, reason));
        this.paymentMethod = paymentMethod;
        this.reason = reason;
    }
    
    /**
     * Creates a new payment failed exception with a cause.
     * 
     * @param paymentMethod the payment method that failed
     * @param reason the reason for failure
     * @param cause the underlying cause
     */
    public PaymentFailedException(String paymentMethod, String reason, Throwable cause) {
        super(String.format("Payment failed via %s: %s", paymentMethod, reason), cause);
        this.paymentMethod = paymentMethod;
        this.reason = reason;
    }
    
    /**
     * Gets the payment method that failed.
     * 
     * @return payment method name
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    /**
     * Gets the reason for failure.
     * 
     * @return failure reason
     */
    public String getReason() {
        return reason;
    }
}
