package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetTopStockProductsUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    
    public Flux<Product> execute(String franchiseId) {
        Flux<Product> separatedProducts = productRepository.findTopStockByFranchise(franchiseId);

        Flux<Product> embeddedProducts = branchRepository.findByFranchiseId(franchiseId)
                .flatMap(branch -> {
                    if (branch.getProducts() != null && !branch.getProducts().isEmpty()) {
                        return Flux.fromIterable(branch.getProducts())
                                .map(product -> product.toBuilder()
                                        .branchId(branch.getId())
                                        .franchiseId(franchiseId)
                                        .build());
                    }
                    return Flux.empty();
                });

        return Flux.concat(separatedProducts, embeddedProducts)
                .sort((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
                .take(3);
    }
}
