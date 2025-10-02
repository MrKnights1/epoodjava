package ee.commerce.order.decorator;

import ee.commerce.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Concrete decorator that adds gift wrapping service to an order.
 * Adds 5€ to the total price.
 * 
 * This decorator can be combined with other decorators to provide
 * multiple services on the same order.
 */
public class GiftWrappingDecorator extends OrderDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(GiftWrappingDecorator.class);
    private static final BigDecimal GIFT_WRAPPING_COST = new BigDecimal("5.00");
    private static final String SERVICE_DESCRIPTION = "Kingituspakend";
    
    /**
     * Creates a gift wrapping decorator for the given order.
     * 
     * @param order the order to add gift wrapping to
     */
    public GiftWrappingDecorator(Order order) {
        super(order);
        logger.info("Added gift wrapping service (+{}€) to order", GIFT_WRAPPING_COST);
    }
    
    /**
     * Calculates total price including gift wrapping cost.
     * Uses recursive delegation through the decorator chain.
     * 
     * @return total price with gift wrapping added
     */
    @Override
    public BigDecimal calculateTotal() {
        BigDecimal baseTotal = wrappedOrder.calculateTotal();
        BigDecimal newTotal = baseTotal.add(GIFT_WRAPPING_COST);
        logger.debug("Gift wrapping: {} + {} = {}", baseTotal, GIFT_WRAPPING_COST, newTotal);
        return newTotal;
    }
    
    /**
     * Returns description including gift wrapping service.
     * 
     * @return full description with gift wrapping
     */
    @Override
    public String getDescription() {
        return wrappedOrder.getDescription() + " + " + SERVICE_DESCRIPTION;
    }
    
    /**
     * Gets the cost of gift wrapping service.
     * 
     * @return gift wrapping cost
     */
    public static BigDecimal getServiceCost() {
        return GIFT_WRAPPING_COST;
    }
    
    @Override
    public String toString() {
        return String.format("GiftWrappingDecorator{wrapping=%s, cost=%.2f€}", 
                           wrappedOrder, GIFT_WRAPPING_COST);
    }
}
