package ee.commerce.order.model;

import ee.commerce.order.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Complete order entity with status, payment, and tracking information.
 * 
 * This class wraps the decorator-based Order and adds lifecycle management,
 * demonstrating composition over inheritance.
 */
public class CompleteOrder {
    
    private final String orderId;
    private final Order order; // The decorated order
    private final int productId;
    private OrderStatus status;
    private String transactionId;
    private String paymentMethod;
    private final LocalDateTime createdAt;
    private LocalDateTime paidAt;
    
    /**
     * Creates a new complete order.
     * 
     * @param order the decorated order
     * @param productId the base product ID
     */
    public CompleteOrder(Order order, int productId) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        this.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.order = order;
        this.productId = productId;
        this.status = OrderStatus.NEW;
        this.createdAt = LocalDateTime.now();
    }
    
    /**
     * Marks the order as paid.
     * 
     * @param transactionId the payment transaction ID
     * @param paymentMethod the payment method used
     */
    public void markAsPaid(String transactionId, String paymentMethod) {
        this.status = OrderStatus.PAID;
        this.transactionId = transactionId;
        this.paymentMethod = paymentMethod;
        this.paidAt = LocalDateTime.now();
    }
    
    /**
     * Marks the order as failed.
     */
    public void markAsFailed() {
        this.status = OrderStatus.FAILED;
    }
    
    /**
     * Marks the order as processing.
     */
    public void markAsProcessing() {
        this.status = OrderStatus.PROCESSING;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public Order getOrder() {
        return order;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public OrderStatus getStatus() {
        return status;
    }
    
    public void setStatus(OrderStatus status) {
        this.status = status;
    }
    
    public String getTransactionId() {
        return transactionId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getPaidAt() {
        return paidAt;
    }
    
    /**
     * Gets the total amount of the order.
     * 
     * @return order total
     */
    public BigDecimal getTotal() {
        return order.calculateTotal();
    }
    
    /**
     * Gets the description of the order.
     * 
     * @return order description
     */
    public String getDescription() {
        return order.getDescription();
    }
    
    @Override
    public String toString() {
        return String.format("CompleteOrder{id='%s', status=%s, total=%.2f€, description='%s'}", 
                           orderId, status, getTotal(), getDescription());
    }
}
