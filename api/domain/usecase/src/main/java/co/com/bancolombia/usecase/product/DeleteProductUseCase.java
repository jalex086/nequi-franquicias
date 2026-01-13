package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DeleteProductUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    
    public Mono<Void> execute(String productId) {
        return productRepository.findById(productId)
                .flatMap(product -> productRepository.deleteById(productId))
                .switchIfEmpty(
                    branchRepository.findBranchIdByProductId(productId)
                            .flatMap(branchId -> deleteEmbeddedInBranch(branchId, productId))
                );
    }
    
    private Mono<Void> deleteEmbeddedInBranch(String branchId, String productId) {
        return branchRepository.findById(branchId)
                .flatMap(branch -> {
                    List<Product> updated = branch.getProducts().stream()
                            .filter(p -> !productId.equals(p.getId()))
                            .collect(Collectors.toList());
                    
                    return branchRepository.save(branch.toBuilder().products(updated).build());
                })
                .then();
    }
}
