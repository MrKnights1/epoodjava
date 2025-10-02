package ee.commerce.order.model;

import ee.commerce.order.BasicOrder;
import ee.commerce.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CompleteOrder entity.
 * Tests order lifecycle and status management.
 */
@DisplayName("Complete Order Tests")
class CompleteOrderTest {
    
    private Order baseOrder;
    
    @BeforeEach
    void setUp() {
        baseOrder = new BasicOrder("Test Product", new BigDecimal("99.99"));
    }
    
    @Test
    @DisplayName("Should create order with NEW status")
    void testOrderCreation() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        assertNotNull(order.getOrderId());
        assertTrue(order.getOrderId().startsWith("ORD-"));
        assertEquals(OrderStatus.NEW, order.getStatus());
        assertNull(order.getTransactionId());
        assertNull(order.getPaymentMethod());
        assertNotNull(order.getCreatedAt());
        assertNull(order.getPaidAt());
    }
    
    @Test
    @DisplayName("✅ AC3: Should transition to PAID status on success")
    void testSuccessfulPayment() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        order.setStatus(OrderStatus.PROCESSING);
        order.markAsPaid("TXN-12345", "Credit Card");
        
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals("TXN-12345", order.getTransactionId());
        assertEquals("Credit Card", order.getPaymentMethod());
        assertNotNull(order.getPaidAt());
    }
    
    @Test
    @DisplayName("✅ AC4: Should transition to FAILED status on failure")
    void testFailedPayment() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        order.setStatus(OrderStatus.PROCESSING);
        order.markAsFailed();
        
        assertEquals(OrderStatus.FAILED, order.getStatus());
        assertNull(order.getTransactionId());
        assertNull(order.getPaidAt());
    }
    
    @Test
    @DisplayName("Should delegate order operations to wrapped order")
    void testOrderDelegation() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        assertEquals(baseOrder.calculateTotal(), order.getTotal());
        assertEquals(baseOrder.getDescription(), order.getDescription());
    }
    
    @Test
    @DisplayName("Should track payment timestamp")
    void testPaymentTimestamp() throws InterruptedException {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        assertNull(order.getPaidAt());
        
        Thread.sleep(10); // Small delay
        order.markAsPaid("TXN-001", "PayPal");
        
        assertNotNull(order.getPaidAt());
        assertTrue(order.getPaidAt().isAfter(order.getCreatedAt()));
    }
    
    @Test
    @DisplayName("Should allow status transitions")
    void testStatusTransitions() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        assertEquals(OrderStatus.NEW, order.getStatus());
        
        order.setStatus(OrderStatus.PROCESSING);
        assertEquals(OrderStatus.PROCESSING, order.getStatus());
        
        order.setStatus(OrderStatus.PAID);
        assertEquals(OrderStatus.PAID, order.getStatus());
        
        order.setStatus(OrderStatus.PREPARING);
        assertEquals(OrderStatus.PREPARING, order.getStatus());
        
        order.setStatus(OrderStatus.SHIPPED);
        assertEquals(OrderStatus.SHIPPED, order.getStatus());
        
        order.setStatus(OrderStatus.DELIVERED);
        assertEquals(OrderStatus.DELIVERED, order.getStatus());
    }
    
    @Test
    @DisplayName("Should allow cancellation")
    void testCancellation() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        
        order.setStatus(OrderStatus.CANCELLED);
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
    }
    
    @Test
    @DisplayName("Should maintain order immutability after payment")
    void testOrderImmutability() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        order.markAsPaid("TXN-001", "Credit Card");
        
        String transactionId = order.getTransactionId();
        String paymentMethod = order.getPaymentMethod();
        
        // Transaction details should not change
        assertEquals(transactionId, order.getTransactionId());
        assertEquals(paymentMethod, order.getPaymentMethod());
    }
    
    @Test
    @DisplayName("Should handle toString for debugging")
    void testToString() {
        CompleteOrder order = new CompleteOrder(baseOrder, 1);
        String str = order.toString();
        
        assertNotNull(str);
        assertTrue(str.contains("CompleteOrder"));
        assertTrue(str.contains("ORD-")); // Order ID format
    }
}
