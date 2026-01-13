package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetProductsByBranchUseCase {
    
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;
    
    public Flux<Product> execute(String branchId) {
        Flux<Product> separatedProducts = productRepository.findByBranchId(branchId);

        Flux<Product> embeddedProducts = branchRepository.findById(branchId)
                .flatMapMany(branch -> {
                    if (branch.getProducts() != null && !branch.getProducts().isEmpty()) {
                        return Flux.fromIterable(branch.getProducts())
                                .map(product -> product.toBuilder()
                                        .branchId(branchId)
                                        .build());
                    }
                    return Flux.empty();
                });
        return Flux.concat(separatedProducts, embeddedProducts);
    }
}
