package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    
    public Mono<Product> execute(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del producto es requerido"));
        }
        
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre es requerido"));
        }
        
        if (name.trim().length() < 2 || name.trim().length() > 100) {
            return Mono.error(new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres"));
        }

        return productRepository.findById(id)
                .flatMap(product -> productRepository.save(product.updateName(name.trim())))
                .onErrorResume(error -> branchRepository.findBranchIdByProductId(id)
                            .flatMap(branchId -> updateEmbeddedInBranch(branchId, id, name.trim())););
    }
    
    private Mono<Product> updateEmbeddedInBranch(String branchId, String productId, String newName) {
        return branchRepository.findById(branchId)
                .flatMap(branch -> {
                    List<Product> products = branch.getProducts();
                    if (products == null || products.isEmpty()) {
                        return Mono.error(new RuntimeException("Product not found"));
                    }
                    
                    List<Product> updated = products.stream()
                            .map(p -> productId.equals(p.getId()) ? p.updateName(newName) : p)
                            .collect(Collectors.toList());
                    
                    return branchRepository.save(branch.toBuilder().products(updated).build())
                            .then(Mono.just(updated.stream()
                                    .filter(p -> productId.equals(p.getId()))
                                    .findFirst()
                                    .map(p -> p.toBuilder().branchId(branchId).build())
                                    .orElseThrow()));
                });
    }
}
