package co.com.bancolombia.config;

import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
import co.com.bancolombia.usecase.branch.CreateBranchUseCase;
import co.com.bancolombia.usecase.branch.UpdateBranchNameUseCase;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.GetAllFranchisesUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import co.com.bancolombia.usecase.product.CreateProductUseCase;
import co.com.bancolombia.usecase.product.DeleteProductUseCase;
import co.com.bancolombia.usecase.product.GetProductsByBranchUseCase;
import co.com.bancolombia.usecase.product.GetTopStockProductByBranchUseCase;
import co.com.bancolombia.usecase.product.GetTopStockProductsUseCase;
import co.com.bancolombia.usecase.product.UpdateProductNameUseCase;
import co.com.bancolombia.usecase.product.UpdateProductStockUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCasesConfig {
    
    @Bean
    public CreateFranchiseUseCase createFranchiseUseCase(FranchiseRepository repository) {
        return new CreateFranchiseUseCase(repository);
    }
    
    @Bean
    public GetAllFranchisesUseCase getAllFranchisesUseCase(FranchiseRepository repository) {
        return new GetAllFranchisesUseCase(repository);
    }
    
    @Bean
    public UpdateFranchiseNameUseCase updateFranchiseNameUseCase(FranchiseRepository repository) {
        return new UpdateFranchiseNameUseCase(repository);
    }
    
    @Bean
    public CreateBranchUseCase createBranchUseCase(BranchRepository branchRepository, FranchiseRepository franchiseRepository) {
        return new CreateBranchUseCase(branchRepository, franchiseRepository);
    }
    
    @Bean
    public UpdateBranchNameUseCase updateBranchNameUseCase(BranchRepository repository) {
        return new UpdateBranchNameUseCase(repository);
    }
    
    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new CreateProductUseCase(productRepository, branchRepository);
    }
    
    @Bean
    public UpdateProductNameUseCase updateProductNameUseCase(ProductRepository repository) {
        return new UpdateProductNameUseCase(repository);
    }
    
    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository repository) {
        return new UpdateProductStockUseCase(repository);
    }
    
    @Bean
    public GetTopStockProductByBranchUseCase getTopStockProductByBranchUseCase(BranchRepository branchRepository, ProductRepository productRepository) {
        return new GetTopStockProductByBranchUseCase(branchRepository, productRepository);
    }
    
    @Bean
    public GetTopStockProductsUseCase getTopStockProductsUseCase(ProductRepository repository) {
        return new GetTopStockProductsUseCase(repository);
    }
    
    @Bean
    public GetProductsByBranchUseCase getProductsByBranchUseCase(ProductRepository repository) {
        return new GetProductsByBranchUseCase(repository);
    }
    
    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository repository) {
        return new DeleteProductUseCase(repository);
    }
}
