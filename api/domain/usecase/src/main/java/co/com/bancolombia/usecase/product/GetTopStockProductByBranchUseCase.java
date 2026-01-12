package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Comparator;

@RequiredArgsConstructor
public class GetTopStockProductByBranchUseCase {
    
    private final ProductRepository productRepository;
    
    public Flux<Product> execute(String franchiseId) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Flux.error(new IllegalArgumentException("El ID de la franquicia es requerido"));
        }
        
        return productRepository.findByFranchiseId(franchiseId)
                .groupBy(Product::getBranchId)
                .flatMap(group -> 
                    group.reduce((p1, p2) -> p1.getStock() > p2.getStock() ? p1 : p2)
                )
                .sort(Comparator.comparing(Product::getStock).reversed());
    }
}
