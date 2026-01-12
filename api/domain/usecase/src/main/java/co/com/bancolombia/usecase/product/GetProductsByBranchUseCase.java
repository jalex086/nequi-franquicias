package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetProductsByBranchUseCase {
    
    private final ProductRepository productRepository;
    
    public Flux<Product> execute(String branchId) {
        return productRepository.findByBranchId(branchId);
    }
}
