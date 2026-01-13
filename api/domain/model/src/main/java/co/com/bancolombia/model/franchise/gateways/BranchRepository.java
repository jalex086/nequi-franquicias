package co.com.bancolombia.model.franchise.gateways;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findById(String id);
    Flux<Branch> findByFranchiseId(String franchiseId);
    Mono<Void> deleteById(String id);
    Mono<Branch> addProduct(String branchId, Product product);
    Mono<String> findBranchIdByProductId(String productId);
}
