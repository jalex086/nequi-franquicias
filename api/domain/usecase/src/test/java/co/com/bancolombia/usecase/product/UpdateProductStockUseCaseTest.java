package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateProductStockUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BranchRepository branchRepository;

    private UpdateProductStockUseCase updateProductStockUseCase;

    @BeforeEach
    void setUp() {
        updateProductStockUseCase = new UpdateProductStockUseCase(productRepository, branchRepository);
    }

    @Test
    void shouldUpdateProductStockSuccessfully() {
        // Given
        String productId = "product-1";
        Integer newStock = 500;
        
        Product existingProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .stock(100)
                .branchId("branch-1")
                .franchiseId("franchise-1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Product updatedProduct = existingProduct.toBuilder()
                .stock(newStock)
                .build();

        when(productRepository.findById(productId))
                .thenReturn(Mono.just(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenReturn(Mono.just(updatedProduct));

        // When & Then
        StepVerifier.create(updateProductStockUseCase.execute(productId, newStock))
                .expectNextMatches(product -> 
                    product.getStock().equals(newStock) && 
                    product.getId().equals(productId))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenProductNotFound() {
        // Given
        when(productRepository.findById(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Product not found")));
        when(branchRepository.findBranchIdByProductId(anyString()))
                .thenReturn(Mono.error(new RuntimeException("Product not found")));

        // When & Then
        StepVerifier.create(updateProductStockUseCase.execute("nonexistent", 100))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().contains("not found"))
                .verify();
    }

    @Test
    void shouldFailWhenStockIsNegative() {
        // When & Then
        StepVerifier.create(updateProductStockUseCase.execute("product-1", -1))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("mayor o igual a 0"))
                .verify();
    }
}
