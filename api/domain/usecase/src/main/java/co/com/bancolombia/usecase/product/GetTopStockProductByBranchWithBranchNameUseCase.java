package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetTopStockProductByBranchWithBranchNameUseCase {
    
    private final GetTopStockProductByBranchUseCase getTopStockProductByBranchUseCase;
    private final BranchRepository branchRepository;
    
    public Flux<ProductWithBranchName> execute(String franchiseId) {
        return getTopStockProductByBranchUseCase.execute(franchiseId)
                .flatMap(product -> 
                    branchRepository.findById(product.getBranchId())
                        .map(branch -> new ProductWithBranchName(product, branch.getName()))
                        .defaultIfEmpty(new ProductWithBranchName(product, "Sucursal no encontrada"))
                );
    }
    
    public static class ProductWithBranchName {
        private final Product product;
        private final String branchName;
        
        public ProductWithBranchName(Product product, String branchName) {
            this.product = product;
            this.branchName = branchName;
        }
        
        public Product getProduct() {
            return product;
        }
        
        public String getBranchName() {
            return branchName;
        }
    }
}
