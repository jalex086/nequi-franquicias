package co.com.bancolombia.dynamodb.branch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@DynamoDbBean
public class BranchEntity {
    
    private String id;
    private String franchiseId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String storageStrategy; // EMBEDDED | SEPARATED
    private List<EmbeddedProduct> products; // Solo si EMBEDDED
    
    @DynamoDbPartitionKey
    public String getPK() {
        return "BRANCH#" + id;
    }
    
    public void setPK(String pk) {
        if (pk != null && pk.startsWith("BRANCH#")) {
            this.id = pk.substring(7);
        }
    }
    
    @DynamoDbSortKey
    public String getSK() {
        return "METADATA";
    }
    
    public void setSK(String sk) {
    }
    
    public String getId() {
        return id;
    }
    
    public String getFranchiseId() {
        return franchiseId;
    }
    
    public String getName() {
        return name;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public String getStorageStrategy() {
        return storageStrategy;
    }
    
    public List<EmbeddedProduct> getProducts() {
        return products;
    }
    
    @DynamoDbBean
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    public static class EmbeddedProduct {
        private String id;
        private String name;
        private Integer stock;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public String getId() { return id; }
        public String getName() { return name; }
        public Integer getStock() { return stock; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
    }
}
