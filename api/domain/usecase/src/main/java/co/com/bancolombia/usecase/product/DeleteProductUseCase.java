package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class DeleteProductUseCase {
    
    private final ProductRepository productRepository;
    
    public Mono<Void> execute(String productId) {
        return productRepository.deleteById(productId);
    }
}
