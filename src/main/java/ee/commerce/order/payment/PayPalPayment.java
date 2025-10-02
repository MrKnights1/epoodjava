package ee.commerce.order.payment;

import ee.commerce.order.exception.PaymentFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * PayPal payment strategy implementation.
 * 
 * Simulates PayPal payment processing with OAuth-style authentication
 * and transaction handling.
 */
public class PayPalPayment implements PaymentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(PayPalPayment.class);
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("15000.00");
    private static final BigDecimal TRANSACTION_FEE_RATE = new BigDecimal("0.029"); // 2.9%
    private static final int SUCCESS_RATE = 95; // 95% success rate
    
    private final Random random = new Random();
    
    @Override
    public String processPayment(BigDecimal amount, String orderReference) throws PaymentFailedException {
        logger.info("Processing PayPal payment: {}€ for order {}", amount, orderReference);
        
        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid payment amount: {}", amount);
            throw new PaymentFailedException("PayPal", "Invalid amount");
        }
        
        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            logger.warn("Amount exceeds PayPal limit: {}€ (max: {}€)", amount, MAX_TRANSACTION_AMOUNT);
            throw new PaymentFailedException("PayPal", 
                String.format("Amount exceeds limit of %.2f€", MAX_TRANSACTION_AMOUNT));
        }
        
        // Calculate transaction fee
        BigDecimal fee = amount.multiply(TRANSACTION_FEE_RATE);
        logger.debug("PayPal transaction fee: {}€ ({}%)", fee, TRANSACTION_FEE_RATE.multiply(new BigDecimal("100")));
        
        // Simulate OAuth authentication
        logger.debug("Authenticating with PayPal OAuth...");
        try {
            Thread.sleep(300 + random.nextInt(300)); // 0.3-0.6 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentFailedException("PayPal", "Authentication interrupted", e);
        }
        
        // Simulate payment processing
        logger.debug("Processing PayPal transaction...");
        try {
            Thread.sleep(400 + random.nextInt(400)); // 0.4-0.8 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentFailedException("PayPal", "Transaction interrupted", e);
        }
        
        // Simulate occasional payment failures (5% failure rate)
        if (random.nextInt(100) >= SUCCESS_RATE) {
            logger.error("PayPal payment failed for order {}", orderReference);
            throw new PaymentFailedException("PayPal", "Insufficient funds or account issue");
        }
        
        // Generate transaction ID
        String transactionId = "PP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        logger.info("PayPal payment successful: Transaction ID {}", transactionId);
        return transactionId;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "PayPal";
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && 
               amount.compareTo(MAX_TRANSACTION_AMOUNT) <= 0;
    }
    
    /**
     * Gets the maximum transaction amount for PayPal.
     * 
     * @return maximum amount
     */
    public static BigDecimal getMaxTransactionAmount() {
        return MAX_TRANSACTION_AMOUNT;
    }
    
    /**
     * Gets the transaction fee rate.
     * 
     * @return fee rate as decimal (e.g., 0.029 for 2.9%)
     */
    public static BigDecimal getTransactionFeeRate() {
        return TRANSACTION_FEE_RATE;
    }
}
