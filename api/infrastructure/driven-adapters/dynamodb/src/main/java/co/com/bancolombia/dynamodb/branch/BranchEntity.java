package co.com.bancolombia.dynamodb.branch;

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
public class BranchEntity {
    
    private String id;
    private String franchiseId;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String GSI1PK;
    private String GSI2PK;
    
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
    
    public String getName() {
        return name;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
