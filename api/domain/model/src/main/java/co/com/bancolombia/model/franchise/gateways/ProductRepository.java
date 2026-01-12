package co.com.bancolombia.model.franchise.gateways;

import co.com.bancolombia.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {
    Mono<Product> save(Product product);
    Mono<Product> findById(String id);
    Flux<Product> findByBranchId(String branchId);
    Flux<Product> findByFranchiseId(String franchiseId);
    Flux<Product> findTopStockByFranchise(String franchiseId);
    Mono<Void> deleteById(String id);
}
