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
    
    private String PK;
    private String SK;
    private String id;
    private String franchiseId;
    private String branchId;
    private String name;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Campos físicos para GSI
    private String GSI1PK; // Para GSI1 - búsqueda por branchId
    private String GSI2PK; // Para GSI2 - búsqueda por id
    
    @DynamoDbPartitionKey
    public String getPK() {
        return PK;
    }
    
    @DynamoDbSortKey
    public String getSK() {
        return SK;
    }
    private String GSI2PK; // Para GSI2 - búsqueda por id
    
    @DynamoDbPartitionKey
    public String getFranchiseId() {
        return franchiseId;
    }
    
    @DynamoDbSortKey
    public String getId() {
        return id;
    }
    
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI1")
    public String getGSI1PK() {
        return GSI1PK;
    }
    
    @DynamoDbSecondaryPartitionKey(indexNames = "GSI2")
    public String getGSI2PK() {
        return GSI2PK;
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
