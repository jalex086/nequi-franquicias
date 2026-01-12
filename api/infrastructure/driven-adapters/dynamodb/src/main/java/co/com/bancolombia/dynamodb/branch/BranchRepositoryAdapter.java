package co.com.bancolombia.dynamodb.branch;

import co.com.bancolombia.dynamodb.config.DynamoDBProperties;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;

@Repository
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepository {
    
    private final DynamoDbEnhancedAsyncClient dynamoClient;
    private final DynamoDBProperties properties;
    
    private DynamoDbAsyncTable<BranchEntity> getTable() {
        return dynamoClient.table(properties.getTables().getBranches(), 
                TableSchema.fromBean(BranchEntity.class));
    }
    
    @Override
    public Mono<Branch> save(Branch branch) {
        BranchEntity entity = toEntity(branch);
        return Mono.fromFuture(getTable().putItem(entity))
                .thenReturn(branch);
    }
    
    @Override
    public Mono<Branch> findById(String id) {
        return Flux.from(getTable().index("GSI1")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(id)))
                .flatMapIterable(page -> page.items()))
                .next()
                .map(this::toDomain);
    }
    
    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return Flux.from(getTable().query(QueryConditional.keyEqualTo(k -> k.partitionValue(franchiseId)))
                .flatMapIterable(page -> page.items()))
                .map(this::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        // Implementación simplificada
        return Mono.empty();
    }
    
    private BranchEntity toEntity(Branch branch) {
        return BranchEntity.builder()
                .id(branch.getId())
                .franchiseId(branch.getFranchiseId())
                .name(branch.getName())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .GSI1PK(branch.getId()) // Para buscar por ID de sucursal
                .GSI2PK("BRANCH#" + branch.getFranchiseId()) // Para buscar sucursales por franquicia
                .build();
    }
    
    private Branch toDomain(BranchEntity entity) {
        return Branch.builder()
                .id(entity.getId())
                .franchiseId(entity.getFranchiseId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
