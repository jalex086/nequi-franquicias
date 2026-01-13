package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductByIdUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    private GetProductByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductByIdUseCase(productRepository);
    }

    @Test
    void shouldGetProductById() {
        // Given
        String productId = "test-product-id";
        Product expectedProduct = Product.builder()
                .id(productId)
                .name("Test Product")
                .stock(100)
                .branchId("branch-id")
                .franchiseId("franchise-id")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(expectedProduct));

        // When & Then
        StepVerifier.create(useCase.execute(productId))
                .expectNext(expectedProduct)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenProductNotFound() {
        // Given
        String productId = "non-existent-id";
        when(productRepository.findById(productId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.execute(productId))
                .verifyComplete();
    }
}
