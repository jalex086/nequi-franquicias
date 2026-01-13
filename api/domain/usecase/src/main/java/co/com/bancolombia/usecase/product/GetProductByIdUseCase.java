package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class GetProductByIdUseCase {
    
    private final ProductRepository productRepository;
    
    public Mono<Product> execute(String productId) {
        return productRepository.findById(productId);
    }
}
