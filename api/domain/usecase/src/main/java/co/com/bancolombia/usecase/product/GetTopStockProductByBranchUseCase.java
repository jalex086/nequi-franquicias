package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetTopStockProductByBranchUseCase {
    
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    
    public Flux<Product> execute(String franchiseId) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("El ID de la franquicia es requerido"));
        }

        return branchRepository.findByFranchiseId(franchiseId)
                .flatMap(branch -> getTopStockProductForBranch(branch));
    }
    
    private Mono<Product> getTopStockProductForBranch(Branch branch) {
        if ("EMBEDDED".equals(branch.getStorageStrategy()) && branch.getProducts() != null && !branch.getProducts().isEmpty()) {
            return Flux.fromIterable(branch.getProducts())
                    .reduce((p1, p2) -> p1.getStock() > p2.getStock() ? p1 : p2)
                    .map(product -> product.toBuilder()
                            .branchId(branch.getId())
                            .build());
        } else {
            return productRepository.findByBranchId(branch.getId())
                    .reduce((p1, p2) -> p1.getStock() > p2.getStock() ? p1 : p2);
        }
    }
}
