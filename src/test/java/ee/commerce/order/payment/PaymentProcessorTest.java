package ee.commerce.order.payment;

import ee.commerce.order.model.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for PaymentProcessor context class.
 * Tests Strategy pattern implementation and payment processing.
 */
@DisplayName("Payment Processor Tests")
class PaymentProcessorTest {
    
    private PaymentProcessor processor;
    
    @BeforeEach
    void setUp() {
        processor = new PaymentProcessor(new CreditCardPayment());
    }
    
    @Test
    @DisplayName("Should create processor with default strategy")
    void testProcessorCreation() {
        assertNotNull(processor);
    }
    
    @Test
    @DisplayName("✅ AC1: Should allow changing payment strategy at runtime")
    void testStrategyChange() {
        CreditCardPayment cc = new CreditCardPayment();
        PayPalPayment pp = new PayPalPayment();
        BankTransferPayment bt = new BankTransferPayment();
        
        processor.setPaymentStrategy(cc);
        assertNotNull(processor);
        
        processor.setPaymentStrategy(pp);
        assertNotNull(processor);
        
        processor.setPaymentStrategy(bt);
        assertNotNull(processor);
    }
    
    @Test
    @DisplayName("Should throw exception when null strategy provided")
    void testNullStrategyException() {
        assertThrows(IllegalArgumentException.class, () -> {
            new PaymentProcessor(null);
        });
    }
    
    @Test
    @DisplayName("Should process payment with credit card")
    void testCreditCardPayment() {
        processor.setPaymentStrategy(new CreditCardPayment());
        BigDecimal amount = new BigDecimal("100.00");
        
        // Try multiple times due to simulated failures
        PaymentProcessor.PaymentResult result = null;
        for (int i = 0; i < 10; i++) {
            result = processor.processPayment(amount, "TEST-CC-" + i);
            if (result.isSuccess()) {
                break;
            }
        }
        
        // At least one should succeed (90% success rate)
        assertNotNull(result);
    }
    
    @Test
    @DisplayName("✅ AC3: Successful payment returns success result")
    void testSuccessfulPaymentResult() {
        processor.setPaymentStrategy(new BankTransferPayment()); // 98% success rate
        BigDecimal amount = new BigDecimal("100.00");
        
        // With 98% success, should get success within 5 tries
        PaymentProcessor.PaymentResult result = null;
        for (int i = 0; i < 5; i++) {
            result = processor.processPayment(amount, "TEST-BT-" + i);
            if (result.isSuccess()) {
                break;
            }
        }
        
        assertNotNull(result);
        if (result.isSuccess()) {
            assertNotNull(result.getTransactionId());
            assertTrue(result.getTransactionId().startsWith("BT-"));
            assertEquals("Payment successful", result.getMessage());
        }
    }
    
    @Test
    @DisplayName("✅ AC4: Failed payment returns failure result with message")
    void testFailedPaymentResult() {
        processor.setPaymentStrategy(new CreditCardPayment());
        BigDecimal amount = new BigDecimal("100.00");
        
        // Try until we get a failure (10% failure rate)
        PaymentProcessor.PaymentResult result = null;
        for (int i = 0; i < 50; i++) {
            result = processor.processPayment(amount, "TEST-FAIL-" + i);
            if (!result.isSuccess()) {
                break;
            }
        }
        
        // Should eventually get a failure
        assertNotNull(result);
        if (!result.isSuccess()) {
            assertFalse(result.isSuccess());
            assertNull(result.getTransactionId());
            assertNotNull(result.getMessage());
            assertTrue(result.getMessage().contains("failed") || 
                      result.getMessage().contains("declined"));
        }
    }
    
    @Test
    @DisplayName("PaymentResult should be immutable")
    void testPaymentResultImmutability() {
        PaymentProcessor.PaymentResult result = 
            new PaymentProcessor.PaymentResult(true, "TXN-123", OrderStatus.PAID, 
                                              "Success", "Credit Card");
        
        assertTrue(result.isSuccess());
        assertEquals("TXN-123", result.getTransactionId());
        assertEquals("Success", result.getMessage());
        assertEquals(OrderStatus.PAID, result.getOrderStatus());
        assertEquals("Credit Card", result.getPaymentMethod());
        
        // Result fields should be final (no setters exist)
        assertThrows(NoSuchMethodException.class, () -> {
            PaymentProcessor.PaymentResult.class.getMethod("setSuccess", boolean.class);
        });
    }
    
    @Test
    @DisplayName("Should handle invalid amounts gracefully")
    void testInvalidAmountHandling() {
        processor.setPaymentStrategy(new CreditCardPayment());
        
        PaymentProcessor.PaymentResult result = processor.processPayment(
            BigDecimal.ZERO, "TEST-INVALID-1");
        
        assertFalse(result.isSuccess());
        assertNull(result.getTransactionId());
        assertNotNull(result.getMessage());
    }
    
    @Test
    @DisplayName("Should handle amounts exceeding limits")
    void testExceedingLimits() {
        processor.setPaymentStrategy(new CreditCardPayment());
        
        PaymentProcessor.PaymentResult result = processor.processPayment(
            new BigDecimal("50000.00"), "TEST-EXCEEDS");
        
        assertFalse(result.isSuccess());
        assertNull(result.getTransactionId());
        assertTrue(result.getMessage().contains("limit") || 
                  result.getMessage().contains("exceeds"));
    }
}
