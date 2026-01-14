package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetProductsByBranchUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BranchRepository branchRepository;

    private GetProductsByBranchUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetProductsByBranchUseCase(productRepository, branchRepository);
    }

    @Test
    void shouldGetProductsFromBothSeparatedAndEmbedded() {
        // Given
        String branchId = "test-branch-id";
        
        // Productos SEPARATED
        Product separatedProduct = Product.builder()
                .id("separated-id")
                .name("Separated Product")
                .stock(100)
                .branchId(branchId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Productos EMBEDDED
        Product embeddedProduct = Product.builder()
                .id("embedded-id")
                .name("Embedded Product")
                .stock(200)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .products(List.of(embeddedProduct))
                .build();

        when(productRepository.findByBranchId(branchId)).thenReturn(Flux.just(separatedProduct));
        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));

        // When & Then
        StepVerifier.create(useCase.execute(branchId))
                .expectNext(separatedProduct)
                .expectNextMatches(product -> 
                    product.getId().equals("embedded-id") && 
                    product.getBranchId().equals(branchId))
                .verifyComplete();
    }

    @Test
    void shouldReturnOnlySeparatedWhenNoEmbeddedProducts() {
        // Given
        String branchId = "test-branch-id";
        
        Product separatedProduct = Product.builder()
                .id("separated-id")
                .name("Separated Product")
                .stock(100)
                .branchId(branchId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .products(null)
                .build();

        when(productRepository.findByBranchId(branchId)).thenReturn(Flux.just(separatedProduct));
        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));

        // When & Then
        StepVerifier.create(useCase.execute(branchId))
                .expectNext(separatedProduct)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoBranchFound() {
        // Given
        String branchId = "non-existent-branch";
        
        when(productRepository.findByBranchId(branchId)).thenReturn(Flux.empty());
        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.execute(branchId))
                .verifyComplete();
    }
}
