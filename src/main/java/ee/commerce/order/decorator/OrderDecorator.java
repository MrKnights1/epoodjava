package ee.commerce.order.decorator;

import ee.commerce.order.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Abstract base decorator for Order.
 * This class is the core of the Decorator pattern implementation.
 * 
 * It implements the Order interface and wraps another Order object,
 * delegating all calls to the wrapped object. Concrete decorators
 * extend this class to add additional functionality.
 * 
 * This follows the Decorator pattern from Gang of Four design patterns,
 * allowing behavior to be added to individual objects dynamically.
 */
public abstract class OrderDecorator implements Order {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderDecorator.class);
    
    /**
     * The wrapped order object.
     * Protected to allow subclasses to access it if needed.
     */
    protected final Order wrappedOrder;
    
    /**
     * Creates a new order decorator.
     * 
     * @param order the order to decorate/wrap
     * @throws IllegalArgumentException if order is null
     */
    protected OrderDecorator(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("Wrapped order cannot be null");
        }
        this.wrappedOrder = order;
        logger.debug("Created {} wrapping {}", this.getClass().getSimpleName(), order.getClass().getSimpleName());
    }
    
    /**
     * Calculates total by delegating to wrapped order and adding decorator's cost.
     * This method should be overridden by concrete decorators to add their specific cost.
     * 
     * @return total price including wrapped order and this decorator's addition
     */
    @Override
    public BigDecimal calculateTotal() {
        return wrappedOrder.calculateTotal();
    }
    
    /**
     * Gets description by delegating to wrapped order.
     * Concrete decorators should override this to add their service description.
     * 
     * @return description including wrapped order's description
     */
    @Override
    public String getDescription() {
        return wrappedOrder.getDescription();
    }
    
    /**
     * Gets the wrapped order.
     * 
     * @return the wrapped order
     */
    public Order getWrappedOrder() {
        return wrappedOrder;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderDecorator that = (OrderDecorator) o;
        return Objects.equals(wrappedOrder, that.wrappedOrder);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(wrappedOrder);
    }
}
