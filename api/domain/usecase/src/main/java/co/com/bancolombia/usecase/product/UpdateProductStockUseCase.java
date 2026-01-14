package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    
    public Mono<Product> execute(String id, Integer stock) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del producto es requerido"));
        }
        
        if (stock == null || stock < 0) {
            return Mono.error(new IllegalArgumentException("El stock debe ser mayor o igual a 0"));
        }
        
        return productRepository.findById(id)
                .flatMap(product -> productRepository.save(product.updateStock(stock)))
                .onErrorResume(error -> branchRepository.findBranchIdByProductId(id)
                            .flatMap(branchId -> updateEmbeddedStockInBranch(branchId, id, stock)));
    }
    
    private Mono<Product> updateEmbeddedStockInBranch(String branchId, String productId, Integer newStock) {
        return branchRepository.findById(branchId)
                .flatMap(branch -> {
                    List<Product> products = branch.getProducts();
                    if (products == null || products.isEmpty()) {
                        return Mono.error(new RuntimeException("Product not found"));
                    }
                    
                    List<Product> updated = products.stream()
                            .map(p -> productId.equals(p.getId()) ? p.updateStock(newStock) : p)
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
