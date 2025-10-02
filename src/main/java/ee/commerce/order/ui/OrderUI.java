package ee.commerce.order.ui;

import ee.commerce.order.BasicOrder;
import ee.commerce.order.Order;
import ee.commerce.order.decorator.ExpressShippingDecorator;
import ee.commerce.order.decorator.GiftWrappingDecorator;
import ee.commerce.order.decorator.GreetingCardDecorator;
import ee.commerce.order.model.*;
import ee.commerce.order.payment.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive console-based UI for the order system.
 * Allows users to create orders and add services interactively.
 */
public class OrderUI {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderUI.class);
    private final Scanner scanner;
    private final List<OrderItem> orderHistory;
    
    /**
     * Represents a completed order in history.
     */
    private static class OrderItem {
        final String description;
        final BigDecimal total;
        final OrderStatus status;
        final String paymentMethod;
        final String transactionId;
        
        OrderItem(String description, BigDecimal total, OrderStatus status, 
                 String paymentMethod, String transactionId) {
            this.description = description;
            this.total = total;
            this.status = status;
            this.paymentMethod = paymentMethod;
            this.transactionId = transactionId;
        }
    }
    
    public OrderUI() {
        this.scanner = new Scanner(System.in);
        this.orderHistory = new ArrayList<>();
    }
    
    /**
     * Starts the interactive UI.
     */
    public void start() {
        printWelcome();
        
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    createNewOrder();
                    break;
                case "2":
                    browseProducts();
                    break;
                case "3":
                    viewOrderHistory();
                    break;
                case "4":
                    showServicePrices();
                    break;
                case "5":
                    showAbout();
                    break;
                case "0":
                    running = false;
                    printGoodbye();
                    break;
                default:
                    System.out.println("❌ Vigane valik! Palun vali 0-5.");
                    break;
            }
        }
        
        scanner.close();
    }
    
    /**
     * Prints the welcome message.
     */
    private void printWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("    🛍️  E-POE TELLIMUSTE SÜSTEEM  🛍️");
        System.out.println("=".repeat(60));
        System.out.println("Tere tulemast! Siin saad luua tellimusi ja lisada lisateenuseid.");
        System.out.println("=".repeat(60) + "\n");
    }
    
    /**
     * Prints the main menu.
     */
    private void printMainMenu() {
        System.out.println("\n📋 PEAMENÜÜ:");
        System.out.println("  [1] 🛒 Loo uus tellimus");
        System.out.println("  [2] � Sirvi tooteid");
        System.out.println("  [3] �📜 Vaata tellimuste ajalugu");
        System.out.println("  [4] 💰 Vaata teenuste hinnad");
        System.out.println("  [5] ℹ️  Info rakenduse kohta");
        System.out.println("  [0] 🚪 Välju");
        System.out.print("\nSinu valik: ");
    }
    
    /**
     * Creates a new order interactively.
     */
    private void createNewOrder() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("📦 UUS TELLIMUS");
        System.out.println("─".repeat(60));
        
        // Show product catalog
        Product selectedProduct = selectProduct();
        if (selectedProduct == null) {
            return; // User cancelled
        }
        
        // Display selected product
        System.out.println("\n✅ Valitud toode:");
        System.out.println("   " + selectedProduct.getName());
        System.out.println("   💰 " + String.format("%.2f€", selectedProduct.getPrice()));
        if (!selectedProduct.getDescription().isEmpty()) {
            System.out.println("   📝 " + selectedProduct.getDescription());
        }
        
        // Create base order
        Order order;
        try {
            order = new BasicOrder(selectedProduct.getName(), selectedProduct.getPrice());
            logger.info("Created new order: {} - {}€", selectedProduct.getName(), selectedProduct.getPrice());
        } catch (Exception e) {
            System.out.println("❌ Viga tellimuse loomisel: " + e.getMessage());
            return;
        }
        
        // Add services
        order = addServices(order);
        
        // Create complete order
        CompleteOrder completeOrder = new CompleteOrder(order, selectedProduct.getId());
        
        // Show order summary
        System.out.println("\n" + "═".repeat(60));
        System.out.println("📋 TELLIMUSE KOKKUVÕTE");
        System.out.println("═".repeat(60));
        System.out.println("📝 Tellimus: " + order.getDescription());
        System.out.println("💰 KOGUSUMMA: " + String.format("%.2f€", order.calculateTotal()));
        System.out.println("🆔 Tellimuse number: " + completeOrder.getOrderId());
        System.out.println("═".repeat(60));
        
        // Process payment
        PaymentProcessor.PaymentResult paymentResult = processPayment(completeOrder);
        
        if (paymentResult.isSuccess()) {
            // Mark order as paid
            completeOrder.markAsPaid(paymentResult.getTransactionId(), 
                                    paymentResult.getPaymentMethod());
            
            // Decrease inventory
            InventoryManager inventory = InventoryManager.getInstance();
            if (inventory.reserveStock(selectedProduct.getId(), 1)) {
                logger.info("Stock reserved for product {}", selectedProduct.getId());
            } else {
                logger.warn("Failed to reserve stock for product {}", selectedProduct.getId());
            }
            
            // Show success
            System.out.println("\n" + "═".repeat(60));
            System.out.println("✅ TELLIMUS EDUKALT LOODUD JA MAKSTUD!");
            System.out.println("═".repeat(60));
            System.out.println("🎉 Täname ostu eest!");
            System.out.println("🆔 Tellimuse number: " + completeOrder.getOrderId());
            System.out.println("💳 Maksemeetod: " + paymentResult.getPaymentMethod());
            System.out.println("🔖 Tehingu ID: " + paymentResult.getTransactionId());
            System.out.println("📦 Staatus: " + completeOrder.getStatus().getEstonianName());
            System.out.println("═".repeat(60));
            
            // Add to history
            orderHistory.add(new OrderItem(order.getDescription(), order.calculateTotal(),
                                          completeOrder.getStatus(), paymentResult.getPaymentMethod(),
                                          paymentResult.getTransactionId()));
        } else {
            // Payment failed - order stays in cart
            completeOrder.markAsFailed();
            
            System.out.println("\n" + "═".repeat(60));
            System.out.println("❌ MAKSE EBAÕNNESTUS");
            System.out.println("═".repeat(60));
            System.out.println("⚠️  " + paymentResult.getMessage());
            System.out.println("📋 Tellimus jääb ostukorvi avatuks");
            System.out.println("💡 Proovi uuesti või vali teine maksemeetod");
            System.out.println("═".repeat(60));
        }
        
        // Ask if user wants to continue
        System.out.print("\nVajuta ENTER et jätkata...");
        scanner.nextLine();
    }
    
    /**
     * Allows user to select a product from the catalog.
     * 
     * @return selected product or null if cancelled
     */
    private Product selectProduct() {
        System.out.println("\n🛒 TOOTEKATALOOG");
        System.out.println("─".repeat(60));
        
        List<Product> products = ProductCatalog.getAllProducts();
        
        for (Product product : products) {
            System.out.println("\n[" + product.getId() + "] " + product.getName());
            System.out.println("    💰 " + String.format("%.2f€", product.getPrice()));
            System.out.println("    📝 " + product.getDescription());
        }
        
        System.out.println("\n[0] ❌ Tühista");
        
        while (true) {
            System.out.print("\nVali toode (number): ");
            String input = scanner.nextLine().trim();
            
            try {
                int productId = Integer.parseInt(input);
                
                if (productId == 0) {
                    return null; // Cancel
                }
                
                Product product = ProductCatalog.getProductById(productId);
                if (product != null) {
                    return product;
                } else {
                    System.out.println("❌ Toodet numbriga " + productId + " ei leitud! Proovi uuesti.");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Palun sisesta number!");
            }
        }
    }
    
    /**
     * Allows user to add services to an order.
     */
    private Order addServices(Order order) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("🎁 LISA LISATEENUSEID");
        System.out.println("─".repeat(60));
        
        boolean addingServices = true;
        while (addingServices) {
            System.out.println("\nPraegune tellimus: " + order.getDescription());
            System.out.println("Praegune hind: " + String.format("%.2f€", order.calculateTotal()));
            
            System.out.println("\n📦 Saadaolevad teenused:");
            System.out.println("  [1] 🎁 Kingituspakend (+5.00€)");
            System.out.println("  [2] 🚀 Kiirtoimetamine (+10.00€)");
            System.out.println("  [3] 💌 Tervituskaart (+2.00€)");
            System.out.println("  [0] ✔️  Lõpeta teenuste lisamine");
            System.out.print("\nMida soovid lisada? ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    order = new GiftWrappingDecorator(order);
                    System.out.println("✅ Kingituspakend lisatud! (+5.00€)");
                    break;
                case "2":
                    order = new ExpressShippingDecorator(order);
                    System.out.println("✅ Kiirtoimetamine lisatud! (+10.00€)");
                    break;
                case "3":
                    System.out.print("💬 Sisesta tervitussõnum (või jäta tühjaks): ");
                    String message = scanner.nextLine().trim();
                    if (message.isEmpty()) {
                        order = new GreetingCardDecorator(order);
                    } else {
                        order = new GreetingCardDecorator(order, message);
                    }
                    System.out.println("✅ Tervituskaart lisatud! (+2.00€)");
                    break;
                case "0":
                    addingServices = false;
                    break;
                default:
                    System.out.println("❌ Vigane valik! Palun vali 0-3.");
                    break;
            }
        }
        
        return order;
    }
    
    /**
     * Processes payment for an order.
     * 
     * @param completeOrder the complete order to process payment for
     * @return payment result
     */
    private PaymentProcessor.PaymentResult processPayment(CompleteOrder completeOrder) {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("💳 MAKSEMEETODI VALIK");
        System.out.println("─".repeat(60));
        
        System.out.println("\nSaadaolevad maksemeetodid:");
        System.out.println("  [1] 💳 Krediitkaart (kiire, limiit: 10 000€)");
        System.out.println("  [2] 🅿️  PayPal (turvaline, limiit: 15 000€)");
        System.out.println("  [3] 🏦 Pangaülekanne (suurte summade jaoks, limiit: 50 000€)");
        System.out.println("  [0] ❌ Tühista tellimus");
        
        while (true) {
            System.out.print("\nVali maksemeetod: ");
            String choice = scanner.nextLine().trim();
            
            PaymentStrategy strategy = null;
            
            switch (choice) {
                case "1":
                    strategy = new CreditCardPayment();
                    break;
                case "2":
                    strategy = new PayPalPayment();
                    break;
                case "3":
                    strategy = new BankTransferPayment();
                    break;
                case "0":
                    System.out.println("❌ Tellimus tühistatud");
                    return new PaymentProcessor.PaymentResult(false, null, OrderStatus.CANCELLED,
                                                             "Kasutaja tühistas", "None");
                default:
                    System.out.println("❌ Vigane valik! Palun vali 0-3.");
                    continue;
            }
            
            // Process payment with selected strategy
            completeOrder.markAsProcessing();
            System.out.println("\n⏳ Makset töödeldakse...");
            
            PaymentProcessor processor = new PaymentProcessor(strategy);
            PaymentProcessor.PaymentResult result = processor.processPayment(
                completeOrder.getTotal(), completeOrder.getOrderId());
            
            return result;
        }
    }
    
    /**
     * Browse available products.
     */
    private void browseProducts() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("📦 TOOTEKATALOOG");
        System.out.println("─".repeat(60));
        
        List<Product> products = ProductCatalog.getAllProducts();
        
        for (Product product : products) {
            System.out.println("\n" + product.getName());
            System.out.println("   💰 Hind: " + String.format("%.2f€", product.getPrice()));
            System.out.println("   📝 " + product.getDescription());
            System.out.println("   🆔 Toote ID: " + product.getId());
        }
        
        System.out.println("\n" + "─".repeat(60));
        System.out.println("💡 Tellimuse loomiseks vali 'Loo uus tellimus' peamenüüst");
        
        System.out.print("\nVajuta ENTER et jätkata...");
        scanner.nextLine();
    }
    
    /**
     * Displays order history.
     */
    private void viewOrderHistory() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("📜 TELLIMUSTE AJALUGU");
        System.out.println("─".repeat(60));
        
        if (orderHistory.isEmpty()) {
            System.out.println("\n📭 Tellimuste ajalugu on tühi.");
            System.out.println("Loo esmalt uus tellimus!");
        } else {
            BigDecimal totalRevenue = BigDecimal.ZERO;
            int paidOrders = 0;
            
            for (int i = 0; i < orderHistory.size(); i++) {
                OrderItem item = orderHistory.get(i);
                System.out.println("\n🛒 Tellimus #" + (i + 1));
                System.out.println("   📝 " + item.description);
                System.out.println("   💰 " + String.format("%.2f€", item.total));
                System.out.println("   📦 Staatus: " + item.status.getEstonianName());
                if (item.paymentMethod != null) {
                    System.out.println("   💳 Maksemeetod: " + item.paymentMethod);
                }
                if (item.transactionId != null) {
                    System.out.println("   🔖 Tehingu ID: " + item.transactionId);
                }
                
                if (item.status == OrderStatus.PAID) {
                    totalRevenue = totalRevenue.add(item.total);
                    paidOrders++;
                }
            }
            
            System.out.println("\n" + "─".repeat(60));
            System.out.println("📊 Tellimusi kokku: " + orderHistory.size());
            System.out.println("✅ Makstud tellimusi: " + paidOrders);
            System.out.println("💵 Käive kokku: " + String.format("%.2f€", totalRevenue));
            if (paidOrders > 0) {
                System.out.println("💰 Keskmine tellimus: " + 
                    String.format("%.2f€", totalRevenue.divide(
                        new BigDecimal(paidOrders), 2, java.math.RoundingMode.HALF_UP)));
            }
        }
        
        System.out.print("\nVajuta ENTER et jätkata...");
        scanner.nextLine();
    }
    
    /**
     * Shows service prices.
     */
    private void showServicePrices() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("💰 TEENUSTE HINNAKIRI");
        System.out.println("─".repeat(60));
        System.out.println("\n🎁 Kingituspakend .................... 5.00€");
        System.out.println("   Ilus pakendikott või paber koos paelaga");
        System.out.println("\n🚀 Kiirtoimetamine .................. 10.00€");
        System.out.println("   Kaup jõuab kohale 1-2 tööpäevaga");
        System.out.println("\n💌 Tervituskaart ..................... 2.00€");
        System.out.println("   Personaliseeritud tervituskaart sinu sõnumiga");
        System.out.println("\n" + "─".repeat(60));
        System.out.println("ℹ️  Kõiki teenuseid saab kombineerida!");
        System.out.println("ℹ️  Näiteks: Kõik 3 teenust kokku = 17.00€");
        
        System.out.print("\nVajuta ENTER et jätkata...");
        scanner.nextLine();
    }
    
    /**
     * Shows information about the application.
     */
    private void showAbout() {
        System.out.println("\n" + "─".repeat(60));
        System.out.println("ℹ️  INFO RAKENDUSE KOHTA");
        System.out.println("─".repeat(60));
        System.out.println("\n📦 E-poe tellimuste süsteem");
        System.out.println("🔧 Versioon: 1.0-SNAPSHOT");
        System.out.println("\n📚 Arhitektuur:");
        System.out.println("   • Objektorienteeritud Java");
        System.out.println("   • Decorator design pattern");
        System.out.println("   • SOLID põhimõtted");
        System.out.println("\n✨ Funktsioonid:");
        System.out.println("   • Tellimuste loomine");
        System.out.println("   • Lisateenuste lisamine");
        System.out.println("   • Dünaamiline hinnakujundus");
        System.out.println("   • Tellimuste ajalugu");
        System.out.println("\n💡 Tehnoloogiad:");
        System.out.println("   • Java 17");
        System.out.println("   • Maven");
        System.out.println("   • SLF4J + Logback");
        System.out.println("   • JUnit 5");
        
        System.out.print("\nVajuta ENTER et jätkata...");
        scanner.nextLine();
    }
    
    /**
     * Prints goodbye message.
     */
    private void printGoodbye() {
        System.out.println("\n" + "═".repeat(60));
        System.out.println("👋 Täname kasutamast e-poe tellimuste süsteemi!");
        if (!orderHistory.isEmpty()) {
            System.out.println("📊 Lõid täna " + orderHistory.size() + " tellimust.");
        }
        System.out.println("═".repeat(60) + "\n");
    }
}
