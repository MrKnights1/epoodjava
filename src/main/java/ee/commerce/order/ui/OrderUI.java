package ee.commerce.order.ui;

import ee.commerce.order.BasicOrder;
import ee.commerce.order.Order;
import ee.commerce.order.decorator.ExpressShippingDecorator;
import ee.commerce.order.decorator.GiftWrappingDecorator;
import ee.commerce.order.decorator.GreetingCardDecorator;
import ee.commerce.order.model.Product;
import ee.commerce.order.model.ProductCatalog;
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
        
        OrderItem(String description, BigDecimal total) {
            this.description = description;
            this.total = total;
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
        
        // Show final order
        System.out.println("\n" + "═".repeat(60));
        System.out.println("✅ TELLIMUS LOODUD!");
        System.out.println("═".repeat(60));
        System.out.println("📝 Tellimus: " + order.getDescription());
        System.out.println("💰 KOGUSUMMA: " + String.format("%.2f€", order.calculateTotal()));
        System.out.println("═".repeat(60));
        
        // Add to history
        orderHistory.add(new OrderItem(order.getDescription(), order.calculateTotal()));
        logger.info("Order completed - Total: {}€", order.calculateTotal());
        
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
            
            for (int i = 0; i < orderHistory.size(); i++) {
                OrderItem item = orderHistory.get(i);
                System.out.println("\n🛒 Tellimus #" + (i + 1));
                System.out.println("   📝 " + item.description);
                System.out.println("   💰 " + String.format("%.2f€", item.total));
                totalRevenue = totalRevenue.add(item.total);
            }
            
            System.out.println("\n" + "─".repeat(60));
            System.out.println("📊 Tellimusi kokku: " + orderHistory.size());
            System.out.println("💵 Käive kokku: " + String.format("%.2f€", totalRevenue));
            System.out.println("💰 Keskmine tellimus: " + 
                String.format("%.2f€", totalRevenue.divide(
                    new BigDecimal(orderHistory.size()), 2, java.math.RoundingMode.HALF_UP)));
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
