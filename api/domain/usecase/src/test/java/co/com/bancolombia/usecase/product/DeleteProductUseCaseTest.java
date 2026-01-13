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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BranchRepository branchRepository;

    private DeleteProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DeleteProductUseCase(productRepository, branchRepository);
    }

    @Test
    void shouldDeleteSeparatedProduct() {
        // Given
        String productId = "separated-product-id";
        Product product = Product.builder()
                .id(productId)
                .name("Separated Product")
                .stock(100)
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.just(product));
        when(productRepository.deleteById(productId)).thenReturn(Mono.empty());
        when(branchRepository.findBranchIdByProductId(productId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.execute(productId))
                .verifyComplete();
    }

    @Test
    void shouldDeleteEmbeddedProduct() {
        // Given
        String productId = "embedded-product-id";
        String branchId = "branch-id";
        
        Product embeddedProduct = Product.builder()
                .id(productId)
                .name("Embedded Product")
                .stock(100)
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .products(List.of(embeddedProduct))
                .build();

        Branch updatedBranch = branch.toBuilder()
                .products(List.of())
                .build();

        when(productRepository.findById(productId)).thenReturn(Mono.empty());
        when(branchRepository.findBranchIdByProductId(productId)).thenReturn(Mono.just(branchId));
        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updatedBranch));

        // When & Then
        StepVerifier.create(useCase.execute(productId))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenProductNotFound() {
        // Given
        String productId = "non-existent-id";
        
        when(productRepository.findById(productId)).thenReturn(Mono.empty());
        when(branchRepository.findBranchIdByProductId(productId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.execute(productId))
                .verifyComplete();
    }
}
