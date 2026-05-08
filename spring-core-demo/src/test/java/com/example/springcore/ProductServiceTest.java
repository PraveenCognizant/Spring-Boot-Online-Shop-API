package com.example.springcore;

import com.example.springcore.dto.ProductRequest;
import com.example.springcore.exception.ResourceNotFoundException;
import com.example.springcore.model.Product;
import com.example.springcore.repository.ProductRepository;
import com.example.springcore.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ============================================================
 * CONCEPT: Unit Testing with JUnit 5 + Mockito
 * ============================================================
 *
 * Unit Test = tests ONE class in isolation, with dependencies MOCKED.
 *
 * @ExtendWith(MockitoExtension.class)
 *   → Activates Mockito without needing a full Spring context.
 *     Much faster than @SpringBootTest.
 *
 * @Mock → Creates a fake (mock) of ProductRepository.
 *         We control what it returns — no real database needed.
 *
 * @InjectMocks → Creates a real ProductServiceImpl and injects
 *                all @Mock fields into it (via constructor or field).
 *
 * MOCKITO KEY METHODS:
 *   when(mock.method()).thenReturn(value)  → stub a return value
 *   when(mock.method()).thenThrow(ex)      → stub an exception
 *   verify(mock).method()                  → assert method was called
 *   verify(mock, times(2)).method()        → assert called exactly twice
 *   verify(mock, never()).method()         → assert never called
 *
 * ASSERTJ (used for assertions — more readable than JUnit assertions):
 *   assertThat(value).isEqualTo(expected)
 *   assertThat(list).hasSize(3)
 *   assertThatThrownBy(() -> ...).isInstanceOf(MyException.class)
 *
 * NAMING CONVENTION: methodName_stateUnderTest_expectedBehavior
 */
@SuppressWarnings("null")  // Mockito's any()/notNull() matchers safely return null for @NonNull params
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;   // fake — no real DB

    @InjectMocks
    private ProductServiceImpl productService;     // real class being tested

    private Product sampleProduct;
    private ProductRequest sampleRequest;

    @BeforeEach  // runs before EACH test method
    void setUp() {
        sampleProduct = new Product("iPhone 15", "Apple phone", 999.0, 50, "Electronics");
        sampleProduct.setId(1L);

        sampleRequest = new ProductRequest("iPhone 15", "Apple phone", 999.0, 50, "Electronics");
    }

    // ---- Test: getAllProducts ----

    @Test
    @DisplayName("getAllProducts returns all products from repository")
    void getAllProducts_always_returnsAllProducts() {
        // ARRANGE — stub the repository to return our sample list
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        // ACT
        List<Product> result = productService.getAllProducts();

        // ASSERT
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("iPhone 15");
        verify(productRepository, times(1)).findAll();  // confirm it was called once
    }

    // ---- Test: getProductById — success ----

    @Test
    @DisplayName("getProductById returns product when found")
    void getProductById_existingId_returnsProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getPrice()).isEqualTo(999.0);
    }

    // ---- Test: getProductById — not found ----

    @Test
    @DisplayName("getProductById throws ResourceNotFoundException when not found")
    void getProductById_nonExistingId_throwsResourceNotFoundException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // assertThatThrownBy is cleaner than try-catch in tests
        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Product")
                .hasMessageContaining("99");
    }

    // ---- Test: createProduct ----

    @Test
    @DisplayName("createProduct saves and returns the new product")
    void createProduct_validRequest_savesAndReturnsProduct() {
        // When save() is called with any Product, return our sampleProduct
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.createProduct(sampleRequest);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("iPhone 15");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // ---- Test: deleteProduct — success ----

    @Test
    @DisplayName("deleteProduct calls delete when product exists")
    void deleteProduct_existingId_deletesProduct() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        doNothing().when(productRepository).delete(sampleProduct);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(sampleProduct);
    }

    // ---- Test: deleteProduct — not found ----

    @Test
    @DisplayName("deleteProduct throws exception when product does not exist")
    void deleteProduct_nonExistingId_throwsResourceNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(999L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(productRepository, never()).delete(any()); // delete should NOT be called
    }
}
