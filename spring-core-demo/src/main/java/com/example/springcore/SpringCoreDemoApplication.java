package com.example.springcore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ============================================================
 * CONCEPT 01: @SpringBootApplication — The Heart of Spring Boot
 * ============================================================
 *
 * @SpringBootApplication is a shortcut for 3 annotations:
 *
 *   1. @SpringBootConfiguration  → Marks this as a config class (like @Configuration)
 *   2. @EnableAutoConfiguration  → Auto-configures beans based on classpath JARs
 *                                   e.g., sees H2 JAR → auto-creates DataSource
 *                                        sees Web JAR → auto-creates DispatcherServlet
 *   3. @ComponentScan            → Scans this package & sub-packages for
 *                                   @Component, @Service, @Repository, @Controller
 *
 * @EnableScheduling → Activates @Scheduled tasks (see ReportScheduler.java)
 *
 * HOW IT WORKS:
 *   SpringApplication.run(...) does:
 *   1. Creates the Spring IoC Container (ApplicationContext)
 *   2. Registers all beans found by @ComponentScan
 *   3. Runs auto-configuration
 *   4. Starts embedded Tomcat on port 8080
 *   5. Deploys your REST endpoints
 *
 * RUN: mvn spring-boot:run   OR   java -jar target/spring-core-demo-1.0.jar
 * Then open: http://localhost:8080/api/products
 */
@SpringBootApplication
@EnableScheduling
public class SpringCoreDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCoreDemoApplication.class, args);
        System.out.println("\n✅ Spring Boot Online Shop is running!");
        System.out.println("📦 Products API  : http://localhost:8080/api/products");
        System.out.println("🛒 Orders API    : http://localhost:8080/api/orders");
        System.out.println("❤️  Health Check : http://localhost:8080/actuator/health");
        System.out.println("🗄️  H2 Console   : http://localhost:8080/h2-console\n");
    }
}
