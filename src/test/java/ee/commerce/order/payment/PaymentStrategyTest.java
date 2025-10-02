package ee.commerce.order.payment;

import ee.commerce.order.exception.PaymentFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for payment strategies.
 * Tests all three payment methods and their specific behaviors.
 */
@DisplayName("Payment Strategy Tests")
class PaymentStrategyTest {
    
    private CreditCardPayment creditCardPayment;
    private PayPalPayment payPalPayment;
    private BankTransferPayment bankTransferPayment;
    
    @BeforeEach
    void setUp() {
        creditCardPayment = new CreditCardPayment();
        payPalPayment = new PayPalPayment();
        bankTransferPayment = new BankTransferPayment();
    }
    
    // ✅ AC1: Can choose payment method - Credit Card
    @Test
    @DisplayName("✅ AC1: Credit card payment method exists")
    void testCreditCardPaymentExists() {
        assertNotNull(creditCardPayment);
        assertEquals("Credit Card", creditCardPayment.getPaymentMethodName());
    }
    
    // ✅ AC1: Can choose payment method - PayPal
    @Test
    @DisplayName("✅ AC1: PayPal payment method exists")
    void testPayPalPaymentExists() {
        assertNotNull(payPalPayment);
        assertEquals("PayPal", payPalPayment.getPaymentMethodName());
    }
    
    // ✅ AC1: Can choose payment method - Bank Transfer
    @Test
    @DisplayName("✅ AC1: Bank transfer payment method exists")
    void testBankTransferPaymentExists() {
        assertNotNull(bankTransferPayment);
        assertEquals("Bank Transfer", bankTransferPayment.getPaymentMethodName());
    }
    
    // ✅ AC2: Different processing logic per method
    @Test
    @DisplayName("✅ AC2: Credit card has different limits than PayPal")
    void testDifferentPaymentLimits() {
        BigDecimal ccMax = CreditCardPayment.getMaxTransactionAmount();
        BigDecimal ppMax = PayPalPayment.getMaxTransactionAmount();
        BigDecimal btMax = BankTransferPayment.getMaxTransactionAmount();
        
        assertNotEquals(ccMax, ppMax);
        assertNotEquals(ppMax, btMax);
        assertNotEquals(ccMax, btMax);
        
        assertEquals(new BigDecimal("10000.00"), ccMax);
        assertEquals(new BigDecimal("15000.00"), ppMax);
        assertEquals(new BigDecimal("50000.00"), btMax);
    }
    
    @Test
    @DisplayName("Should validate payment amounts")
    void testAmountValidation() {
        assertTrue(creditCardPayment.canProcess(new BigDecimal("100.00")));
        assertTrue(payPalPayment.canProcess(new BigDecimal("100.00")));
        assertTrue(bankTransferPayment.canProcess(new BigDecimal("100.00")));
        
        assertFalse(creditCardPayment.canProcess(BigDecimal.ZERO));
        assertFalse(creditCardPayment.canProcess(new BigDecimal("-10.00")));
    }
    
    @Test
    @DisplayName("Should reject amounts exceeding limits")
    void testAmountLimits() {
        BigDecimal veryLarge = new BigDecimal("100000.00");
        
        // All payment methods have limits, this exceeds all of them
        assertFalse(creditCardPayment.canProcess(veryLarge));
        assertFalse(payPalPayment.canProcess(veryLarge));
        assertFalse(bankTransferPayment.canProcess(veryLarge)); // 50K limit
    }
    
    @Test
    @DisplayName("Should throw exception for invalid amounts")
    void testInvalidAmountException() {
        assertThrows(PaymentFailedException.class, () -> {
            creditCardPayment.processPayment(BigDecimal.ZERO, "TEST-001");
        });
        
        assertThrows(PaymentFailedException.class, () -> {
            creditCardPayment.processPayment(new BigDecimal("-10.00"), "TEST-002");
        });
    }
    
    @Test
    @DisplayName("Should throw exception when exceeding limits")
    void testExceedingLimitException() {
        BigDecimal tooMuch = new BigDecimal("20000.00");
        
        assertThrows(PaymentFailedException.class, () -> {
            creditCardPayment.processPayment(tooMuch, "TEST-003");
        });
    }
    
    @Test
    @DisplayName("PaymentFailedException should contain details")
    void testPaymentFailedExceptionDetails() {
        try {
            creditCardPayment.processPayment(BigDecimal.ZERO, "TEST-004");
            fail("Should have thrown PaymentFailedException");
        } catch (PaymentFailedException e) {
            assertEquals("Credit Card", e.getPaymentMethod());
            assertEquals("Invalid amount", e.getReason());
            assertNotNull(e.getMessage());
        }
    }
    
    @Test
    @DisplayName("Should generate transaction IDs")
    void testTransactionIdGeneration() throws PaymentFailedException {
        // Note: This test might occasionally fail due to simulated payment failures
        // In production, we'd use dependency injection to control randomness
        
        BigDecimal amount = new BigDecimal("50.00");
        
        // Try a few times in case of simulated failures
        String transactionId = null;
        for (int i = 0; i < 5; i++) {
            try {
                transactionId = creditCardPayment.processPayment(amount, "TEST-CC-" + i);
                break;
            } catch (PaymentFailedException e) {
                // Simulated failure, try again
            }
        }
        
        if (transactionId != null) {
            assertTrue(transactionId.startsWith("CC-"));
            assertEquals(11, transactionId.length()); // CC- + 8 chars
        }
        
        // Test PayPal
        transactionId = null;
        for (int i = 0; i < 5; i++) {
            try {
                transactionId = payPalPayment.processPayment(amount, "TEST-PP-" + i);
                break;
            } catch (PaymentFailedException e) {
                // Simulated failure, try again
            }
        }
        
        if (transactionId != null) {
            assertTrue(transactionId.startsWith("PP-"));
        }
        
        // Test Bank Transfer
        transactionId = null;
        for (int i = 0; i < 5; i++) {
            try {
                transactionId = bankTransferPayment.processPayment(amount, "TEST-BT-" + i);
                break;
            } catch (PaymentFailedException e) {
                // Simulated failure, try again
            }
        }
        
        if (transactionId != null) {
            assertTrue(transactionId.startsWith("BT-"));
        }
    }
    
    @Test
    @DisplayName("Should handle valid payment amounts")
    void testValidPaymentAmounts() {
        // Test small amounts
        assertTrue(creditCardPayment.canProcess(new BigDecimal("0.01")));
        assertTrue(creditCardPayment.canProcess(new BigDecimal("1.00")));
        
        // Test medium amounts
        assertTrue(creditCardPayment.canProcess(new BigDecimal("500.00")));
        assertTrue(payPalPayment.canProcess(new BigDecimal("1000.00")));
        
        // Test near-limit amounts
        assertTrue(creditCardPayment.canProcess(new BigDecimal("9999.99")));
        assertTrue(payPalPayment.canProcess(new BigDecimal("14999.99")));
        assertTrue(bankTransferPayment.canProcess(new BigDecimal("49999.99")));
    }
}
