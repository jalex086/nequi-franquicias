package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateProductUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    
    public Mono<Product> execute(String franchiseId, String branchId, String name, Integer stock) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la franquicia es requerido"));
        }
        
        if (branchId == null || branchId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la sucursal es requerido"));
        }
        
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre del producto es requerido"));
        }
        
        if (stock == null || stock < 0) {
            return Mono.error(new IllegalArgumentException("El stock debe ser mayor o igual a 0"));
        }
        
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new RuntimeException("Sucursal con ID " + branchId + " not found")))
                .flatMap(branch -> {
                    Product newProduct = Product.create(franchiseId, branchId, name.trim(), stock)
                            .toBuilder()
                            .id(UUID.randomUUID().toString())
                            .build();

                    int currentProductCount = branch.getProducts() != null ? branch.getProducts().size() : 0;
                    
                    if (currentProductCount >= 100) {
                        return productRepository.save(newProduct);
                    } else {
                        return branchRepository.addProduct(branchId, newProduct)
                                .thenReturn(newProduct);
                    }
                });
    }
}
