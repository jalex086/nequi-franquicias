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
import co.com.bancolombia.usecase.product.GetProductByIdUseCase;
import co.com.bancolombia.usecase.product.GetProductsByBranchUseCase;
import co.com.bancolombia.usecase.product.GetTopStockProductByBranchUseCase;
import co.com.bancolombia.usecase.product.GetTopStockProductByBranchWithBranchNameUseCase;
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
    public UpdateProductNameUseCase updateProductNameUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new UpdateProductNameUseCase(productRepository, branchRepository);
    }
    
    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new UpdateProductStockUseCase(productRepository, branchRepository);
    }
    
    @Bean
    public GetTopStockProductByBranchUseCase getTopStockProductByBranchUseCase(BranchRepository branchRepository, ProductRepository productRepository) {
        return new GetTopStockProductByBranchUseCase(branchRepository, productRepository);
    }
    
    @Bean
    public GetTopStockProductByBranchWithBranchNameUseCase getTopStockProductByBranchWithBranchNameUseCase(
            GetTopStockProductByBranchUseCase getTopStockProductByBranchUseCase,
            BranchRepository branchRepository) {
        return new GetTopStockProductByBranchWithBranchNameUseCase(getTopStockProductByBranchUseCase, branchRepository);
    }
    
    @Bean
    public GetTopStockProductsUseCase getTopStockProductsUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new GetTopStockProductsUseCase(productRepository, branchRepository);
    }
    
    @Bean
    public GetProductsByBranchUseCase getProductsByBranchUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new GetProductsByBranchUseCase(productRepository, branchRepository);
    }
    
    @Bean
    public GetProductByIdUseCase getProductByIdUseCase(ProductRepository repository) {
        return new GetProductByIdUseCase(repository);
    }
    
    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository, BranchRepository branchRepository) {
        return new DeleteProductUseCase(productRepository, branchRepository);
    }
}
