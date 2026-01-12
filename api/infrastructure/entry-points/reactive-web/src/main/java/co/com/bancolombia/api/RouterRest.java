package co.com.bancolombia.api;

import co.com.bancolombia.api.handler.FranchiseHandler;
import co.com.bancolombia.api.handler.BranchHandler;
import co.com.bancolombia.api.handler.ProductHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class RouterRest {
    
    @Bean
    public RouterFunction<ServerResponse> routerFunction(
            FranchiseHandler franchiseHandler,
            BranchHandler branchHandler,
            ProductHandler productHandler) {
        
        return route()
                // Franchise routes
                .POST("/api/franchises", franchiseHandler::createFranchise)
                .GET("/api/franchises", franchiseHandler::getAllFranchises)
                .PUT("/api/franchises/{id}/name", franchiseHandler::updateFranchiseName)
                
                // Branch routes
                .POST("/api/franchises/{franchiseId}/branches", branchHandler::createBranch)
                .PUT("/api/branches/{id}/name", branchHandler::updateBranchName)
                
                // Product routes
                .POST("/api/franchises/{franchiseId}/branches/{branchId}/products", productHandler::createProduct)
                .GET("/api/branches/{branchId}/products", productHandler::getProductsByBranch)
                .DELETE("/api/products/{id}", productHandler::deleteProduct)
                .PUT("/api/products/{id}/name", productHandler::updateProductName)
                .PUT("/api/products/{id}/stock", productHandler::updateProductStock)
                .GET("/api/franchises/{franchiseId}/products/top-stock", productHandler::getTopStockProducts)
                .GET("/api/franchises/{franchiseId}/branches/top-stock-product", productHandler::getTopStockProductByBranch)
                
                .build();
    }
}
