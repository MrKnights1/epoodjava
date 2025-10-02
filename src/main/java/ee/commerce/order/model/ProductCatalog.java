package ee.commerce.order.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Product catalog containing available products for ordering.
 * This follows the catalog pattern for managing product inventory.
 */
public class ProductCatalog {
    
    private static final List<Product> PRODUCTS = new ArrayList<>();
    
    static {
        // Initialize product catalog
        PRODUCTS.add(new Product(1, "💻 Sülearvuti Lenovo ThinkPad", 
            new BigDecimal("899.99"), 
            "Professionaalne 14\" sülearvuti, Intel i5, 16GB RAM"));
        
        PRODUCTS.add(new Product(2, "📱 Nutitelefon Samsung Galaxy", 
            new BigDecimal("599.00"), 
            "6.1\" AMOLED ekraan, 128GB, 5G toega"));
        
        PRODUCTS.add(new Product(3, "🎧 Juhtmevabad kõrvaklapid Sony", 
            new BigDecimal("179.99"), 
            "Mürasummutusega, 30h aku, Bluetooth 5.0"));
        
        PRODUCTS.add(new Product(4, "📚 Raamat 'Clean Code'", 
            new BigDecimal("45.50"), 
            "Robert C. Martin, programmeerimise klassika"));
        
        PRODUCTS.add(new Product(5, "☕ Kohvimasin DeLonghi", 
            new BigDecimal("299.00"), 
            "Automaatne espressomasin, integreeritud kohviveski"));
    }
    
    /**
     * Gets all available products.
     * 
     * @return unmodifiable list of products
     */
    public static List<Product> getAllProducts() {
        return Collections.unmodifiableList(PRODUCTS);
    }
    
    /**
     * Gets a product by ID.
     * 
     * @param id product ID
     * @return product or null if not found
     */
    public static Product getProductById(int id) {
        return PRODUCTS.stream()
            .filter(p -> p.getId() == id)
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Checks if a product ID exists.
     * 
     * @param id product ID
     * @return true if product exists
     */
    public static boolean productExists(int id) {
        return getProductById(id) != null;
    }
}
