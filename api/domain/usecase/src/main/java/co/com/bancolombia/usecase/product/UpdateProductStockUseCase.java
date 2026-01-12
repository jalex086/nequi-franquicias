package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductStockUseCase {
    
    private final ProductRepository productRepository;
    
    public Mono<Product> execute(String id, Integer stock) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID del producto es requerido"));
        }
        
        if (stock == null || stock < 0) {
            return Mono.error(new IllegalArgumentException("El stock debe ser mayor o igual a 0"));
        }
        
        return productRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Producto con ID " + id + " not found")))
                .map(product -> product.toBuilder().stock(stock).build())
                .flatMap(productRepository::save);
    }
}
