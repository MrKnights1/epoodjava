package ee.commerce.order;

import ee.commerce.order.decorator.ExpressShippingDecorator;
import ee.commerce.order.decorator.GiftWrappingDecorator;
import ee.commerce.order.decorator.GreetingCardDecorator;
import ee.commerce.order.ui.OrderUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

/**
 * Main application demonstrating the E-commerce Order System.
 * 
 * This class demonstrates all acceptance criteria:
 * ✅ Client can add gift wrapping (+5€)
 * ✅ Client can add express shipping (+10€)
 * ✅ Client can add personal greeting card (+2€)
 * ✅ Services are combinable
 * ✅ Final price is calculated with all services
 * 
 * Uses the Decorator pattern to add services dynamically to orders.
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    public static void main(String[] args) {
        // Check if demo mode is requested
        if (args.length > 0 && args[0].equals("--demo")) {
            runDemo();
        } else {
            // Start interactive UI
            OrderUI ui = new OrderUI();
            ui.start();
        }
    }
    
    /**
     * Runs the automated demonstration of all acceptance criteria.
     */
    private static void runDemo() {
        logger.info("=== E-poe tellimuste süsteem ===");
        logger.info("Starting order system demonstration...\n");
        
        try {
            // Demonstration of all acceptance criteria
            demonstrateBasicOrder();
            demonstrateGiftWrapping();
            demonstrateExpressShipping();
            demonstrateGreetingCard();
            demonstrateCombinedServices();
            demonstrateAllServices();
            
            logger.info("\n=== All demonstrations completed successfully ===");
            
        } catch (Exception e) {
            logger.error("Error during demonstration", e);
            System.exit(1);
        }
    }
    
    /**
     * Demonstrates a basic order without any additional services.
     */
    private static void demonstrateBasicOrder() {
        logger.info("\n--- Test 1: Basic Order ---");
        Order order = new BasicOrder("Sülearvuti", new BigDecimal("899.99"));
        
        printOrderDetails(order);
    }
    
    /**
     * ✅ Acceptance Criteria: Client can add gift wrapping (+5€)
     */
    private static void demonstrateGiftWrapping() {
        logger.info("\n--- Test 2: Order with Gift Wrapping (+5€) ---");
        Order order = new BasicOrder("Raamat", new BigDecimal("25.50"));
        order = new GiftWrappingDecorator(order);
        
        printOrderDetails(order);
        
        BigDecimal expected = new BigDecimal("30.50");
        assert order.calculateTotal().compareTo(expected) == 0 : 
            "Gift wrapping calculation incorrect";
    }
    
    /**
     * ✅ Acceptance Criteria: Client can add express shipping (+10€)
     */
    private static void demonstrateExpressShipping() {
        logger.info("\n--- Test 3: Order with Express Shipping (+10€) ---");
        Order order = new BasicOrder("Klaviatuur", new BigDecimal("79.99"));
        order = new ExpressShippingDecorator(order);
        
        printOrderDetails(order);
        
        BigDecimal expected = new BigDecimal("89.99");
        assert order.calculateTotal().compareTo(expected) == 0 : 
            "Express shipping calculation incorrect";
    }
    
    /**
     * ✅ Acceptance Criteria: Client can add personal greeting card (+2€)
     */
    private static void demonstrateGreetingCard() {
        logger.info("\n--- Test 4: Order with Greeting Card (+2€) ---");
        Order order = new BasicOrder("Lilled", new BigDecimal("45.00"));
        order = new GreetingCardDecorator(order, "Suur tänu!");
        
        printOrderDetails(order);
        
        BigDecimal expected = new BigDecimal("47.00");
        assert order.calculateTotal().compareTo(expected) == 0 : 
            "Greeting card calculation incorrect";
    }
    
    /**
     * ✅ Acceptance Criteria: Services are combinable (2 services)
     */
    private static void demonstrateCombinedServices() {
        logger.info("\n--- Test 5: Order with Gift Wrapping + Express Shipping ---");
        Order order = new BasicOrder("Kohvimasin", new BigDecimal("199.00"));
        order = new GiftWrappingDecorator(order);
        order = new ExpressShippingDecorator(order);
        
        printOrderDetails(order);
        
        // 199.00 + 5.00 + 10.00 = 214.00
        BigDecimal expected = new BigDecimal("214.00");
        assert order.calculateTotal().compareTo(expected) == 0 : 
            "Combined services calculation incorrect";
    }
    
    /**
     * ✅ Acceptance Criteria: All services are combinable
     * ✅ Acceptance Criteria: Final price is calculated with all services
     */
    private static void demonstrateAllServices() {
        logger.info("\n--- Test 6: Order with ALL Services (Gift + Express + Card) ---");
        Order order = new BasicOrder("Nutitelefon", new BigDecimal("599.00"));
        
        // Add all three services
        order = new GiftWrappingDecorator(order);
        order = new ExpressShippingDecorator(order);
        order = new GreetingCardDecorator(order, "Head sünnipäeva!");
        
        printOrderDetails(order);
        
        // 599.00 + 5.00 + 10.00 + 2.00 = 616.00
        BigDecimal expected = new BigDecimal("616.00");
        assert order.calculateTotal().compareTo(expected) == 0 : 
            "All services calculation incorrect";
        
        logger.info("✅ ALL ACCEPTANCE CRITERIA VERIFIED:");
        logger.info("   ✅ Gift wrapping can be added (+5€)");
        logger.info("   ✅ Express shipping can be added (+10€)");
        logger.info("   ✅ Greeting card can be added (+2€)");
        logger.info("   ✅ Services are combinable");
        logger.info("   ✅ Final price calculated correctly with all services");
    }
    
    /**
     * Utility method to print order details in a formatted way.
     * 
     * @param order the order to print
     */
    private static void printOrderDetails(Order order) {
        String description = order.getDescription();
        BigDecimal total = order.calculateTotal();
        
        System.out.println("Tellimus: " + description);
        System.out.println("Koguhind: " + String.format("%.2f€", total));
        System.out.println();
        
        logger.info("Order created - Description: '{}', Total: {}€", description, total);
    }
}
