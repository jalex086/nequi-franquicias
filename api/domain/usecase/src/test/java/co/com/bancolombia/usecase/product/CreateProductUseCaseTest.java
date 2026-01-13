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

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;
    
    @Mock
    private BranchRepository branchRepository;

    private CreateProductUseCase createProductUseCase;

    @BeforeEach
    void setUp() {
        createProductUseCase = new CreateProductUseCase(productRepository, branchRepository);
    }

    @Test
    void shouldCreateProductSuccessfully() {
        // Given
        String franchiseId = "franchise-1";
        String branchId = "branch-1";
        String name = "Test Product";
        Integer stock = 100;

        Branch existingBranch = Branch.builder()
                .id(branchId)
                .franchiseId(franchiseId)
                .name("Test Branch")
                .products(List.of()) // Lista vacía, menos de 100 productos
                .build();

        Product expectedProduct = Product.builder()
                .id("product-1")
                .franchiseId(franchiseId)
                .branchId(branchId)
                .name(name)
                .stock(stock)
                .build();

        when(branchRepository.findById(branchId))
                .thenReturn(Mono.just(existingBranch));
        when(branchRepository.addProduct(anyString(), any(Product.class)))
                .thenReturn(Mono.just(existingBranch));

        // When & Then
        StepVerifier.create(createProductUseCase.execute(franchiseId, branchId, name, stock))
                .expectNextMatches(product -> 
                    product.getName().equals(name) && 
                    product.getStock().equals(stock) &&
                    product.getBranchId().equals(branchId))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenBranchNotFound() {
        // Given
        when(branchRepository.findById(anyString()))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(createProductUseCase.execute("franchise-1", "nonexistent", "Product", 100))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().contains("not found"))
                .verify();
    }

    @Test
    void shouldFailWhenStockIsNegative() {
        // When & Then
        StepVerifier.create(createProductUseCase.execute("franchise-1", "branch-1", "Product", -1))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("mayor o igual a 0"))
                .verify();
    }

    @Test
    void shouldUseSeparatedStrategyWhenBranchHas100Products() {
        // Given
        String franchiseId = "franchise-1";
        String branchId = "branch-1";
        String name = "Test Product";
        Integer stock = 50;

        // Crear lista con 99 productos (al agregar uno más serán 100 = SEPARATED)
        List<Product> existingProducts = new ArrayList<>();
        for (int i = 0; i < 99; i++) {
            existingProducts.add(Product.builder().id("product-" + i).build());
        }

        Branch branchWithManyProducts = Branch.builder()
                .id(branchId)
                .franchiseId(franchiseId)
                .name("Test Branch")
                .products(existingProducts)
                .storageStrategy("EMBEDDED")
                .build();

        Product expectedProduct = Product.builder()
                .id("product-100")
                .franchiseId(franchiseId)
                .branchId(branchId)
                .name(name)
                .stock(stock)
                .build();

        Branch updatedBranch = branchWithManyProducts.toBuilder()
                .storageStrategy("SEPARATED")
                .products(List.of())
                .build();

        when(branchRepository.findById(branchId))
                .thenReturn(Mono.just(branchWithManyProducts));
        when(productRepository.save(any(Product.class)))
                .thenReturn(Mono.just(expectedProduct));
        when(branchRepository.save(any(Branch.class)))
                .thenReturn(Mono.just(updatedBranch));

        // When & Then
        StepVerifier.create(createProductUseCase.execute(franchiseId, branchId, name, stock))
                .expectNextMatches(product -> 
                    product.getName().equals(name) && 
                    product.getStock().equals(stock) &&
                    product.getBranchId().equals(branchId))
                .verifyComplete();

        // Verify that branch was updated to SEPARATED strategy
        verify(branchRepository).save(argThat(branch -> 
            "SEPARATED".equals(branch.getStorageStrategy()) && 
            branch.getProducts().isEmpty()));
    }
}
