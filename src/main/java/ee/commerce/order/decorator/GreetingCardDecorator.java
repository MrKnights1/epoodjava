package ee.commerce.order.decorator;

import ee.commerce.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Concrete decorator that adds a personal greeting card service to an order.
 * Adds 2€ to the total price.
 * 
 * The greeting card can include a personalized message for the recipient.
 * Can be combined with other services like gift wrapping or express shipping.
 */
public class GreetingCardDecorator extends OrderDecorator {
    
    private static final Logger logger = LoggerFactory.getLogger(GreetingCardDecorator.class);
    private static final BigDecimal GREETING_CARD_COST = new BigDecimal("2.00");
    private static final String SERVICE_DESCRIPTION = "Tervituskaart";
    
    private final String message;
    
    /**
     * Creates a greeting card decorator with a custom message.
     * 
     * @param order the order to add greeting card to
     * @param message the personalized message for the card (optional)
     */
    public GreetingCardDecorator(Order order, String message) {
        super(order);
        this.message = (message != null && !message.trim().isEmpty()) 
                       ? message.trim() 
                       : "Palju õnne!";
        logger.info("Added greeting card service (+{}€) with message: '{}'", 
                   GREETING_CARD_COST, this.message);
    }
    
    /**
     * Creates a greeting card decorator with default message.
     * 
     * @param order the order to add greeting card to
     */
    public GreetingCardDecorator(Order order) {
        this(order, null);
    }
    
    /**
     * Calculates total price including greeting card cost.
     * Uses recursive delegation through the decorator chain.
     * 
     * @return total price with greeting card added
     */
    @Override
    public BigDecimal calculateTotal() {
        BigDecimal baseTotal = wrappedOrder.calculateTotal();
        BigDecimal newTotal = baseTotal.add(GREETING_CARD_COST);
        logger.debug("Greeting card: {} + {} = {}", baseTotal, GREETING_CARD_COST, newTotal);
        return newTotal;
    }
    
    /**
     * Returns description including greeting card service.
     * 
     * @return full description with greeting card
     */
    @Override
    public String getDescription() {
        return wrappedOrder.getDescription() + " + " + SERVICE_DESCRIPTION;
    }
    
    /**
     * Gets the personalized message on the greeting card.
     * 
     * @return greeting card message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the cost of greeting card service.
     * 
     * @return greeting card cost
     */
    public static BigDecimal getServiceCost() {
        return GREETING_CARD_COST;
    }
    
    @Override
    public String toString() {
        return String.format("GreetingCardDecorator{wrapping=%s, message='%s', cost=%.2f€}", 
                           wrappedOrder, message, GREETING_CARD_COST);
    }
}
