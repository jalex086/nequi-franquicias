package co.com.bancolombia.model.franchise;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder(toBuilder = true)
public class Product {
    String id;
    String branchId;
    String franchiseId;
    String name;
    Integer stock;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    
    public static Product create(String franchiseId, String branchId, String name, Integer stock) {
        return Product.builder()
                .franchiseId(franchiseId)
                .branchId(branchId)
                .name(name)
                .stock(stock)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public Product updateName(String newName) {
        return this.toBuilder()
                .name(newName)
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public Product updateStock(Integer newStock) {
        return this.toBuilder()
                .stock(newStock)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
