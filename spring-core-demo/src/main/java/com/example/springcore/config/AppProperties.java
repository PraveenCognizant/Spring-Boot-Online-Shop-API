package com.example.springcore.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * ============================================================
 * CONCEPT 03: @ConfigurationProperties — Type-Safe Configuration
 * ============================================================
 *
 * Instead of using @Value("${app.name}") everywhere (scattered, error-prone),
 * we bind ALL related properties to a single POJO.
 *
 * @ConfigurationProperties(prefix = "app")
 *   Binds all properties starting with "app." to fields in this class.
 *
 * In application.properties:
 *   app.name=Online Shop
 *   app.version=1.0.0
 *   app.max-order-quantity=100
 *   app.support-email=support@shop.com
 *   app.currency=USD
 *
 * Spring maps: app.max-order-quantity → maxOrderQuantity (camelCase)
 *
 * @Component makes Spring create and manage this bean.
 * Any class can then @Autowired AppProperties to read config.
 *
 * BENEFITS over @Value:
 *   - Type-safe (int, boolean, List<String> — not just Strings)
 *   - IDE autocomplete for properties
 *   - Grouped and organized
 *   - Can be validated with @Validated + @NotBlank etc.
 */
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private String name;
    private String version;
    private int maxOrderQuantity;
    private String supportEmail;
    private String currency;

    // --- Getters & Setters (required for binding) ---

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public int getMaxOrderQuantity() { return maxOrderQuantity; }
    public void setMaxOrderQuantity(int maxOrderQuantity) { this.maxOrderQuantity = maxOrderQuantity; }

    public String getSupportEmail() { return supportEmail; }
    public void setSupportEmail(String supportEmail) { this.supportEmail = supportEmail; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public String toString() {
        return "AppProperties{name='" + name + "', version='" + version
                + "', maxOrderQuantity=" + maxOrderQuantity + ", currency='" + currency + "'}";
    }
}
