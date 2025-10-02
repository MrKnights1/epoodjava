package ee.commerce.order.decorator;

import ee.commerce.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Concrete decorator that adds express shipping service to an order.
 * Adds 10€ to the total price.
 * 
 * Express shipping ensures faster delivery of the order.
 * Can be combined with other services like gift wrapping or greeting card.
 */
public class ExpressShippingDecorator extends OrderDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(ExpressShippingDecorator.class);
    private static final BigDecimal EXPRESS_SHIPPING_COST = new BigDecimal("10.00");
    private static final String SERVICE_DESCRIPTION = "Kiirtoimetamine";
    
    /**
     * Creates an express shipping decorator for the given order.
     * 
     * @param order the order to add express shipping to
     */
    public ExpressShippingDecorator(Order order) {
        super(order);
        logger.info("Added express shipping service (+{}€) to order", EXPRESS_SHIPPING_COST);
    }
    
    /**
     * Calculates total price including express shipping cost.
     * Uses recursive delegation through the decorator chain.
     * 
     * @return total price with express shipping added
     */
    @Override
    public BigDecimal calculateTotal() {
        BigDecimal baseTotal = wrappedOrder.calculateTotal();
        BigDecimal newTotal = baseTotal.add(EXPRESS_SHIPPING_COST);
        logger.debug("Express shipping: {} + {} = {}", baseTotal, EXPRESS_SHIPPING_COST, newTotal);
        return newTotal;
    }
    
    /**
     * Returns description including express shipping service.
     * 
     * @return full description with express shipping
     */
    @Override
    public String getDescription() {
        return wrappedOrder.getDescription() + " + " + SERVICE_DESCRIPTION;
    }
    
    /**
     * Gets the cost of express shipping service.
     * 
     * @return express shipping cost
     */
    public static BigDecimal getServiceCost() {
        return EXPRESS_SHIPPING_COST;
    }
    
    @Override
    public String toString() {
        return String.format("ExpressShippingDecorator{wrapping=%s, cost=%.2f€}", 
                           wrappedOrder, EXPRESS_SHIPPING_COST);
    }
}
