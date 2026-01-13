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
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTopStockProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BranchRepository branchRepository;

    private GetTopStockProductsUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GetTopStockProductsUseCase(productRepository, branchRepository);
    }

    @Test
    void shouldGetTopStockProductsFromBothSchemas() {
        // Given
        String franchiseId = "franchise-id";
        
        // Productos SEPARATED
        Product separatedProduct = Product.builder()
                .id("separated-id")
                .name("Separated Product")
                .stock(500)
                .branchId("branch-1")
                .franchiseId(franchiseId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // Productos EMBEDDED
        Product embeddedProduct = Product.builder()
                .id("embedded-id")
                .name("Embedded Product")
                .stock(1000)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Branch branch = Branch.builder()
                .id("branch-2")
                .name("Test Branch")
                .products(List.of(embeddedProduct))
                .build();

        when(productRepository.findTopStockByFranchise(franchiseId)).thenReturn(Flux.just(separatedProduct));
        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.just(branch));

        // When & Then - Debería devolver primero el de mayor stock (embedded: 1000)
        StepVerifier.create(useCase.execute(franchiseId))
                .expectNextMatches(product -> 
                    product.getId().equals("embedded-id") && 
                    product.getStock().equals(1000) &&
                    product.getBranchId().equals("branch-2"))
                .expectNextMatches(product -> 
                    product.getId().equals("separated-id") && 
                    product.getStock().equals(500))
                .verifyComplete();
    }

    @Test
    void shouldReturnOnlySeparatedWhenNoEmbeddedProducts() {
        // Given
        String franchiseId = "franchise-id";
        
        Product separatedProduct = Product.builder()
                .id("separated-id")
                .name("Separated Product")
                .stock(100)
                .build();

        when(productRepository.findTopStockByFranchise(franchiseId)).thenReturn(Flux.just(separatedProduct));
        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(useCase.execute(franchiseId))
                .expectNext(separatedProduct)
                .verifyComplete();
    }

    @Test
    void shouldLimitToTop3Products() {
        // Given
        String franchiseId = "franchise-id";
        
        Product product1 = Product.builder().id("1").stock(100).build();
        Product product2 = Product.builder().id("2").stock(200).build();
        Product product3 = Product.builder().id("3").stock(300).build();
        Product product4 = Product.builder().id("4").stock(400).build();
        Product product5 = Product.builder().id("5").stock(500).build();

        when(productRepository.findTopStockByFranchise(franchiseId))
                .thenReturn(Flux.just(product1, product2, product3, product4, product5));
        when(branchRepository.findByFranchiseId(franchiseId)).thenReturn(Flux.empty());

        // When & Then - Solo debe devolver los top 3
        StepVerifier.create(useCase.execute(franchiseId))
                .expectNextCount(3)
                .verifyComplete();
    }
}
