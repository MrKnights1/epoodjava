package ee.commerce.order.model;

/**
 * Enumeration of possible order statuses.
 * Represents the lifecycle of an order through the system.
 */
public enum OrderStatus {
    /**
     * Order is newly created but not yet paid.
     */
    NEW("Uus tellimus"),
    
    /**
     * Payment is being processed.
     */
    PROCESSING("Makse töötlemisel"),
    
    /**
     * Payment successful, order is paid.
     */
    PAID("Makstud"),
    
    /**
     * Payment failed.
     */
    FAILED("Makse ebaõnnestus"),
    
    /**
     * Order is being prepared for shipping.
     */
    PREPARING("Ettevalmistamisel"),
    
    /**
     * Order has been shipped.
     */
    SHIPPED("Saadetud"),
    
    /**
     * Order has been delivered.
     */
    DELIVERED("Kohale toimetatud"),
    
    /**
     * Order has been cancelled.
     */
    CANCELLED("Tühistatud");
    
    private final String estonianName;
    
    OrderStatus(String estonianName) {
        this.estonianName = estonianName;
    }
    
    /**
     * Gets the Estonian name of the status.
     * 
     * @return Estonian status name
     */
    public String getEstonianName() {
        return estonianName;
    }
    
    @Override
    public String toString() {
        return estonianName;
    }
}
