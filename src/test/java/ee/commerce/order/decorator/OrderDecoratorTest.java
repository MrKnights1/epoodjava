package ee.commerce.order.decorator;

import ee.commerce.order.BasicOrder;
import ee.commerce.order.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for all concrete decorators.
 * Verifies all acceptance criteria for the user story.
 */
@DisplayName("Order Decorator Tests")
class OrderDecoratorTest {
    
    private Order baseOrder;
    
    @BeforeEach
    void setUp() {
        baseOrder = new BasicOrder("Test Product", new BigDecimal("100.00"));
    }
    
    // ✅ Acceptance Criteria: Client can add gift wrapping (+5€)
    @Test
    @DisplayName("✅ AC1: Should add gift wrapping service (+5€)")
    void testGiftWrappingDecorator() {
        Order order = new GiftWrappingDecorator(baseOrder);
        
        assertEquals(new BigDecimal("105.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Kingituspakend"));
    }
    
    // ✅ Acceptance Criteria: Client can add express shipping (+10€)
    @Test
    @DisplayName("✅ AC2: Should add express shipping service (+10€)")
    void testExpressShippingDecorator() {
        Order order = new ExpressShippingDecorator(baseOrder);
        
        assertEquals(new BigDecimal("110.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Kiirtoimetamine"));
    }
    
    // ✅ Acceptance Criteria: Client can add personal greeting card (+2€)
    @Test
    @DisplayName("✅ AC3: Should add greeting card service (+2€)")
    void testGreetingCardDecorator() {
        Order order = new GreetingCardDecorator(baseOrder);
        
        assertEquals(new BigDecimal("102.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Tervituskaart"));
    }
    
    @Test
    @DisplayName("Should support custom greeting card message")
    void testGreetingCardWithCustomMessage() {
        GreetingCardDecorator decorator = new GreetingCardDecorator(baseOrder, "Happy Birthday!");
        
        assertEquals("Happy Birthday!", decorator.getMessage());
        assertEquals(new BigDecimal("102.00"), decorator.calculateTotal());
    }
    
    @Test
    @DisplayName("Should use default message when null provided")
    void testGreetingCardWithNullMessage() {
        GreetingCardDecorator decorator = new GreetingCardDecorator(baseOrder, null);
        
        assertEquals("Palju õnne!", decorator.getMessage());
    }
    
    // ✅ Acceptance Criteria: Services are combinable (2 services)
    @Test
    @DisplayName("✅ AC4: Should combine gift wrapping and express shipping")
    void testCombineGiftWrappingAndExpressShipping() {
        Order order = new GiftWrappingDecorator(baseOrder);
        order = new ExpressShippingDecorator(order);
        
        // 100 + 5 + 10 = 115
        assertEquals(new BigDecimal("115.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Kingituspakend"));
        assertTrue(order.getDescription().contains("Kiirtoimetamine"));
    }
    
    @Test
    @DisplayName("✅ AC4: Should combine gift wrapping and greeting card")
    void testCombineGiftWrappingAndGreetingCard() {
        Order order = new GiftWrappingDecorator(baseOrder);
        order = new GreetingCardDecorator(order);
        
        // 100 + 5 + 2 = 107
        assertEquals(new BigDecimal("107.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Kingituspakend"));
        assertTrue(order.getDescription().contains("Tervituskaart"));
    }
    
    @Test
    @DisplayName("✅ AC4: Should combine express shipping and greeting card")
    void testCombineExpressShippingAndGreetingCard() {
        Order order = new ExpressShippingDecorator(baseOrder);
        order = new GreetingCardDecorator(order);
        
        // 100 + 10 + 2 = 112
        assertEquals(new BigDecimal("112.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Kiirtoimetamine"));
        assertTrue(order.getDescription().contains("Tervituskaart"));
    }
    
    // ✅ Acceptance Criteria: All services are combinable
    // ✅ Acceptance Criteria: Final price calculated with all services
    @Test
    @DisplayName("✅ AC5: Should combine all three services")
    void testCombineAllServices() {
        Order order = new GiftWrappingDecorator(baseOrder);
        order = new ExpressShippingDecorator(order);
        order = new GreetingCardDecorator(order, "Thank you!");
        
        // 100 + 5 + 10 + 2 = 117
        assertEquals(new BigDecimal("117.00"), order.calculateTotal());
        assertTrue(order.getDescription().contains("Test Product"));
        assertTrue(order.getDescription().contains("Kingituspakend"));
        assertTrue(order.getDescription().contains("Kiirtoimetamine"));
        assertTrue(order.getDescription().contains("Tervituskaart"));
    }
    
    @Test
    @DisplayName("Should apply decorators in any order with same result")
    void testDecoratorOrderIndependence() {
        // Order 1: Gift -> Express -> Card
        Order order1 = new GiftWrappingDecorator(baseOrder);
        order1 = new ExpressShippingDecorator(order1);
        order1 = new GreetingCardDecorator(order1);
        
        // Order 2: Card -> Gift -> Express
        Order order2 = new GreetingCardDecorator(baseOrder);
        order2 = new GiftWrappingDecorator(order2);
        order2 = new ExpressShippingDecorator(order2);
        
        // Order 3: Express -> Card -> Gift
        Order order3 = new ExpressShippingDecorator(baseOrder);
        order3 = new GreetingCardDecorator(order3);
        order3 = new GiftWrappingDecorator(order3);
        
        assertEquals(order1.calculateTotal(), order2.calculateTotal());
        assertEquals(order2.calculateTotal(), order3.calculateTotal());
    }
    
    @Test
    @DisplayName("Should throw exception when wrapping null order")
    void testNullOrderWrapping() {
        assertThrows(IllegalArgumentException.class, () -> {
            new GiftWrappingDecorator(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new ExpressShippingDecorator(null);
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            new GreetingCardDecorator(null);
        });
    }
    
    @Test
    @DisplayName("Should allow multiple applications of same decorator")
    void testMultipleSameDecorators() {
        // Edge case: applying same decorator multiple times
        Order order = new GiftWrappingDecorator(baseOrder);
        order = new GiftWrappingDecorator(order);
        
        // 100 + 5 + 5 = 110
        assertEquals(new BigDecimal("110.00"), order.calculateTotal());
    }
    
    @Test
    @DisplayName("Should work with zero-price base order")
    void testWithZeroPriceOrder() {
        Order freeOrder = new BasicOrder("Free Sample", BigDecimal.ZERO);
        Order decorated = new GiftWrappingDecorator(freeOrder);
        decorated = new ExpressShippingDecorator(decorated);
        decorated = new GreetingCardDecorator(decorated);
        
        // 0 + 5 + 10 + 2 = 17
        assertEquals(new BigDecimal("17.00"), decorated.calculateTotal());
    }
    
    @Test
    @DisplayName("Should provide access to wrapped order")
    void testGetWrappedOrder() {
        GiftWrappingDecorator decorator = new GiftWrappingDecorator(baseOrder);
        
        assertNotNull(decorator.getWrappedOrder());
        assertEquals(baseOrder, decorator.getWrappedOrder());
    }
    
    @Test
    @DisplayName("Should have correct static service costs")
    void testStaticServiceCosts() {
        assertEquals(new BigDecimal("5.00"), GiftWrappingDecorator.getServiceCost());
        assertEquals(new BigDecimal("10.00"), ExpressShippingDecorator.getServiceCost());
        assertEquals(new BigDecimal("2.00"), GreetingCardDecorator.getServiceCost());
    }
}
