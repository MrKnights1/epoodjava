package ee.commerce.order.payment;

import ee.commerce.order.exception.PaymentFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Credit card payment strategy implementation.
 * 
 * Simulates credit card payment processing with validation and 
 * occasional failures to demonstrate error handling.
 */
public class CreditCardPayment implements PaymentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(CreditCardPayment.class);
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("10000.00");
    private static final int SUCCESS_RATE = 90; // 90% success rate for simulation
    
    private final Random random = new Random();
    
    @Override
    public String processPayment(BigDecimal amount, String orderReference) throws PaymentFailedException {
        logger.info("Processing credit card payment: {}€ for order {}", amount, orderReference);
        
        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid payment amount: {}", amount);
            throw new PaymentFailedException("Credit Card", "Invalid amount");
        }
        
        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            logger.warn("Amount exceeds credit card limit: {}€ (max: {}€)", amount, MAX_TRANSACTION_AMOUNT);
            throw new PaymentFailedException("Credit Card", 
                String.format("Amount exceeds limit of %.2f€", MAX_TRANSACTION_AMOUNT));
        }
        
        // Simulate payment processing delay
        try {
            Thread.sleep(500 + random.nextInt(500)); // 0.5-1 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentFailedException("Credit Card", "Processing interrupted", e);
        }
        
        // Simulate occasional payment failures (10% failure rate)
        if (random.nextInt(100) >= SUCCESS_RATE) {
            logger.error("Credit card payment declined for order {}", orderReference);
            throw new PaymentFailedException("Credit Card", "Card declined by bank");
        }
        
        // Generate transaction ID
        String transactionId = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        logger.info("Credit card payment successful: Transaction ID {}", transactionId);
        return transactionId;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && 
               amount.compareTo(MAX_TRANSACTION_AMOUNT) <= 0;
    }
    
    /**
     * Gets the maximum transaction amount for credit cards.
     * 
     * @return maximum amount
     */
    public static BigDecimal getMaxTransactionAmount() {
        return MAX_TRANSACTION_AMOUNT;
    }
}
