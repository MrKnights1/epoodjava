package ee.commerce.order.payment;

import ee.commerce.order.exception.PaymentFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

/**
 * Bank transfer payment strategy implementation.
 * 
 * Simulates bank transfer payment processing which typically takes longer
 * but has higher transaction limits and lower failure rates.
 */
public class BankTransferPayment implements PaymentStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(BankTransferPayment.class);
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("50000.00");
    private static final int SUCCESS_RATE = 98; // 98% success rate
    
    private final Random random = new Random();
    
    @Override
    public String processPayment(BigDecimal amount, String orderReference) throws PaymentFailedException {
        logger.info("Processing bank transfer payment: {}€ for order {}", amount, orderReference);
        
        // Validate amount
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Invalid payment amount: {}", amount);
            throw new PaymentFailedException("Bank Transfer", "Invalid amount");
        }
        
        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            logger.warn("Amount exceeds bank transfer limit: {}€ (max: {}€)", amount, MAX_TRANSACTION_AMOUNT);
            throw new PaymentFailedException("Bank Transfer", 
                String.format("Amount exceeds limit of %.2f€", MAX_TRANSACTION_AMOUNT));
        }
        
        // Simulate bank verification
        logger.debug("Verifying bank account details...");
        try {
            Thread.sleep(600 + random.nextInt(600)); // 0.6-1.2 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentFailedException("Bank Transfer", "Verification interrupted", e);
        }
        
        // Simulate transfer processing (bank transfers are slower)
        logger.debug("Initiating bank transfer...");
        try {
            Thread.sleep(800 + random.nextInt(800)); // 0.8-1.6 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PaymentFailedException("Bank Transfer", "Transfer interrupted", e);
        }
        
        // Simulate occasional payment failures (2% failure rate)
        if (random.nextInt(100) >= SUCCESS_RATE) {
            logger.error("Bank transfer failed for order {}", orderReference);
            throw new PaymentFailedException("Bank Transfer", "Insufficient funds or invalid account");
        }
        
        // Generate transaction ID
        String transactionId = "BT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        logger.info("Bank transfer successful: Transaction ID {} (Note: Actual transfer may take 1-3 business days)", 
                   transactionId);
        return transactionId;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Bank Transfer";
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && 
               amount.compareTo(MAX_TRANSACTION_AMOUNT) <= 0;
    }
    
    /**
     * Gets the maximum transaction amount for bank transfers.
     * 
     * @return maximum amount
     */
    public static BigDecimal getMaxTransactionAmount() {
        return MAX_TRANSACTION_AMOUNT;
    }
}
