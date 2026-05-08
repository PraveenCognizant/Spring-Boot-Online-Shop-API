package com.example.springcore.config;

import com.example.springcore.model.Product;
import com.example.springcore.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * ============================================================
 * CONCEPT 02: @Configuration + @Bean — Manual Bean Creation
 * ============================================================
 *
 * @Configuration → Marks this class as a source of Spring beans.
 *                  It's like an XML applicationContext.xml, but in Java.
 *
 * @Bean → The method returns an object that Spring manages.
 *         Spring calls this method ONCE and stores the result in the context.
 *         Other beans can then @Autowired it.
 *
 * WHEN DO YOU USE @Bean instead of @Component/@Service?
 *   - Third-party classes you can't annotate (e.g., Jackson ObjectMapper)
 *   - When creation logic is complex
 *   - When you want multiple instances with different configurations
 *
 * CommandLineRunner → Runs once immediately after the app starts.
 *                     Great for seeding test/demo data.
 *
 * @Profile("dev") → This bean only exists in the "dev" profile.
 *                   Won't run in "prod" profile (no data seeding in production!).
 *
 * PROFILES are set in application.properties:
 *   spring.profiles.active=dev
 * Or via command line: java -jar app.jar --spring.profiles.active=prod
 */
@Configuration
public class AppConfig {

    /**
     * Data seeder — runs at startup in "dev" profile only.
     * CommandLineRunner is a functional interface: run(String... args)
     * Spring calls run() after the ApplicationContext is fully loaded.
     */
    @Bean
    @Profile("dev")    // Only active when spring.profiles.active=dev
    public CommandLineRunner seedDemoData(ProductRepository productRepository) {
        return args -> {
            // Only seed if DB is empty
            if (productRepository.count() == 0) {
                System.out.println("\n[CONFIG] Seeding demo data for DEV profile...");

                productRepository.save(new Product("iPhone 15 Pro", "Apple flagship smartphone", 999.99, 50, "Electronics"));
                productRepository.save(new Product("Samsung Galaxy S24", "Android flagship phone", 899.99, 40, "Electronics"));
                productRepository.save(new Product("MacBook Pro M3", "Apple laptop with M3 chip", 1999.99, 20, "Electronics"));
                productRepository.save(new Product("Sony WH-1000XM5", "Noise-cancelling headphones", 349.99, 75, "Electronics"));
                productRepository.save(new Product("Nike Air Max 270", "Running sneakers", 129.99, 150, "Footwear"));
                productRepository.save(new Product("Adidas Ultraboost", "Premium running shoes", 179.99, 100, "Footwear"));
                productRepository.save(new Product("Levi's 501 Jeans", "Classic straight-fit denim", 69.99, 200, "Clothing"));
                productRepository.save(new Product("The Alchemist (Book)", "Paulo Coelho bestseller", 14.99, 500, "Books"));
                productRepository.save(new Product("Yoga Mat Pro", "Non-slip exercise mat", 39.99, 3, "Sports")); // low stock
                productRepository.save(new Product("Coffee Maker Deluxe", "12-cup programmable machine", 79.99, 0, "Kitchen")); // out of stock

                System.out.println("[CONFIG] Seeded " + productRepository.count() + " products.\n");
            }
        };
    }
}
