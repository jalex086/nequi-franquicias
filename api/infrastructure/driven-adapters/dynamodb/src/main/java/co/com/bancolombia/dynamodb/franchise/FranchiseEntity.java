package co.com.bancolombia.dynamodb.franchise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDbBean
public class FranchiseEntity {
    
    private String id;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @DynamoDbPartitionKey
    public String getId() {
        return id;
    }
}
