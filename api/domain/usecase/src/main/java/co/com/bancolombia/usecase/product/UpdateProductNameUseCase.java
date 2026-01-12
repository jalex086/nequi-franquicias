package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateProductNameUseCase {
    
    private final ProductRepository productRepository;
    
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
                .switchIfEmpty(Mono.error(new RuntimeException("Producto con ID " + id + " not found")))
                .map(product -> product.toBuilder().name(name.trim()).build())
                .flatMap(productRepository::save);
    }
}
