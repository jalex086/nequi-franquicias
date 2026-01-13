package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTopStockProductByBranchWithBranchNameUseCaseTest {

    @Mock
    private GetTopStockProductByBranchUseCase getTopStockProductByBranchUseCase;
    
    @Mock
    private BranchRepository branchRepository;

    private GetTopStockProductByBranchWithBranchNameUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTopStockProductByBranchWithBranchNameUseCase(
                getTopStockProductByBranchUseCase, branchRepository);
    }

    @Test
    void shouldGetTopStockProductsWithBranchNames() {
        // Given
        String franchiseId = "franchise-id";
        String branchId = "branch-id";
        
        Product product = Product.builder()
                .id("product-id")
                .name("Top Stock Product")
                .stock(1000)
                .branchId(branchId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Branch branch = Branch.builder()
                .id(branchId)
                .name("Test Branch")
                .build();

        when(getTopStockProductByBranchUseCase.execute(franchiseId)).thenReturn(Flux.just(product));
        when(branchRepository.findById(branchId)).thenReturn(Mono.just(branch));

        // When & Then
        StepVerifier.create(useCase.execute(franchiseId))
                .expectNextMatches(productWithBranch -> 
                    productWithBranch.getProduct().equals(product) &&
                    productWithBranch.getBranchName().equals("Test Branch"))
                .verifyComplete();
    }

    @Test
    void shouldReturnDefaultMessageWhenBranchNotFound() {
        // Given
        String franchiseId = "franchise-id";
        String branchId = "non-existent-branch";
        
        Product product = Product.builder()
                .id("product-id")
                .name("Top Stock Product")
                .stock(1000)
                .branchId(branchId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(getTopStockProductByBranchUseCase.execute(franchiseId)).thenReturn(Flux.just(product));
        when(branchRepository.findById(branchId)).thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(useCase.execute(franchiseId))
                .expectNextMatches(productWithBranch -> 
                    productWithBranch.getProduct().equals(product) &&
                    productWithBranch.getBranchName().equals("Sucursal no encontrada"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoProducts() {
        // Given
        String franchiseId = "franchise-id";
        
        when(getTopStockProductByBranchUseCase.execute(franchiseId)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(franchiseId))
                .verifyComplete();
    }
}
