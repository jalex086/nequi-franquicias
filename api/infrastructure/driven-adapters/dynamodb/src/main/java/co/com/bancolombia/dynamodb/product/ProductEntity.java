package co.com.bancolombia.dynamodb.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@DynamoDbBean
public class ProductEntity {
    
    private String id;
    private String franchiseId;
    private String branchId;
    private String name;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String GSI1PK; // Para búsqueda por branchId
    
    @DynamoDbPartitionKey
    public String getPK() {
        return "PRODUCT#" + id;
    }
    
    public void setPK(String pk) {
        if (pk != null && pk.startsWith("PRODUCT#")) {
            this.id = pk.substring(8);
        }
    }
    
    @DynamoDbSortKey
    public String getSK() {
        return "METADATA";
    }
    
    public void setSK(String sk) {
        // SK fijo
    }
    
    public String getId() {
        return id;
    }
    
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    public String getGSI1PK() {
        return GSI1PK;
    }
    
    public String getFranchiseId() {
        return franchiseId;
    }
    
    public String getBranchId() {
        return branchId;
    }
    
    public String getName() {
        return name;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
