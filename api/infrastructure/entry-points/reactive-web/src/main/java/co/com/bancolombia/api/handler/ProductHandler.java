package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.CreateProductRequest;
import co.com.bancolombia.api.dto.ProductResponse;
import co.com.bancolombia.api.dto.ProductWithBranchResponse;
import co.com.bancolombia.api.dto.UpdateProductNameRequest;
import co.com.bancolombia.api.dto.UpdateProductStockRequest;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import co.com.bancolombia.usecase.product.CreateProductUseCase;
import co.com.bancolombia.usecase.product.DeleteProductUseCase;
import co.com.bancolombia.usecase.product.GetProductsByBranchUseCase;
import co.com.bancolombia.usecase.product.GetTopStockProductByBranchUseCase;
import co.com.bancolombia.usecase.product.GetTopStockProductsUseCase;
import co.com.bancolombia.usecase.product.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.product.UpdateProductStockUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductHandler {
    
    private static final String ID_PATH_VARIABLE = "id";
    private static final String FRANCHISE_ID_PATH_VARIABLE = "franchiseId";
    private static final String BRANCH_ID_PATH_VARIABLE = "branchId";
    private static final String BRANCH_NOT_FOUND_MESSAGE = "Sucursal no encontrada";
    
    private final CreateProductUseCase createProductUseCase;
    private final GetProductsByBranchUseCase getProductsByBranchUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final GetTopStockProductsUseCase getTopStockProductsUseCase;
    private final GetTopStockProductByBranchUseCase getTopStockProductByBranchUseCase;
    private final UpdateProductNameUseCase updateProductNameUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;
    
    public Mono<ServerResponse> createProduct(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID_PATH_VARIABLE);
        String branchId = request.pathVariable(BRANCH_ID_PATH_VARIABLE);
        return request.bodyToMono(CreateProductRequest.class)
                .flatMap(req -> createProductUseCase.execute(franchiseId, branchId, req.getName(), req.getStock()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    public Mono<ServerResponse> getProductsByBranch(ServerRequest request) {
        String branchId = request.pathVariable(BRANCH_ID_PATH_VARIABLE);
        return getProductsByBranchUseCase.execute(branchId)
                .map(this::toResponse)
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }
    
    public Mono<ServerResponse> getProduct(ServerRequest request) {
        String productId = request.pathVariable(ID_PATH_VARIABLE);
        return productRepository.findById(productId)
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .onErrorResume(error -> ServerResponse.notFound().build());
    }
    
    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        String id = request.pathVariable(ID_PATH_VARIABLE);
        return deleteProductUseCase.execute(id)
                .then(ServerResponse.noContent().build());
    }
    
    public Mono<ServerResponse> getTopStockProducts(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID_PATH_VARIABLE);
        return getTopStockProductsUseCase.execute(franchiseId)
                .map(this::toResponse)
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }
    
    public Mono<ServerResponse> getTopStockProductByBranch(ServerRequest request) {
        String franchiseId = request.pathVariable(FRANCHISE_ID_PATH_VARIABLE);
        return getTopStockProductByBranchUseCase.execute(franchiseId)
                .flatMap(product -> 
                    branchRepository.findById(product.getBranchId())
                        .map(branch -> toResponseWithBranch(product, branch.getName()))
                        .defaultIfEmpty(toResponseWithBranch(product, BRANCH_NOT_FOUND_MESSAGE))
                )
                .collectList()
                .flatMap(products -> ServerResponse.ok().bodyValue(products));
    }
    
    public Mono<ServerResponse> updateProductName(ServerRequest request) {
        String id = request.pathVariable(ID_PATH_VARIABLE);
        return request.bodyToMono(UpdateProductNameRequest.class)
                .flatMap(req -> updateProductNameUseCase.execute(id, req.getName()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    public Mono<ServerResponse> updateProductStock(ServerRequest request) {
        String id = request.pathVariable(ID_PATH_VARIABLE);
        return request.bodyToMono(UpdateProductStockRequest.class)
                .flatMap(req -> updateProductStockUseCase.execute(id, req.getStock()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    private ProductResponse toResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .build();
    }
    
    private ProductWithBranchResponse toResponseWithBranch(Product product, String branchName) {
        return ProductWithBranchResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .branchId(product.getBranchId())
                .branchName(branchName)
                .build();
    }
}
