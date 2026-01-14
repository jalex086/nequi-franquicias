package co.com.bancolombia.dynamodb.franchise;

import co.com.bancolombia.dynamodb.config.DynamoDBProperties;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FranchiseRepositoryAdapter implements FranchiseRepository {
    
    private final DynamoDbEnhancedAsyncClient dynamoClient;
    private final DynamoDBProperties properties;
    private DynamoDbAsyncTable<FranchiseEntity> table;
    
    private DynamoDbAsyncTable<FranchiseEntity> getTable() {
        if (table == null) {
            table = dynamoClient.table(properties.getTables().getFranchises(), 
                    TableSchema.fromBean(FranchiseEntity.class));
        }
        return table;
    }
    
    @Override
    public Mono<Franchise> save(Franchise franchise) {
        FranchiseEntity entity = toEntity(franchise);
        return Mono.fromFuture(getTable().putItem(entity))
                .doOnSuccess(result -> log.debug("Franchise saved: {}", franchise.getId()))
                .doOnError(error -> log.error("Error saving franchise: {}", franchise.getId(), error))
                .onErrorMap(throwable -> new RuntimeException("Error saving franchise", throwable))
                .thenReturn(franchise);
    }
    
    @Override
    public Mono<Franchise> findById(String id) {
        return Mono.fromFuture(getTable().getItem(r -> r.key(k -> k.partitionValue("FRANCHISE#" + id).sortValue("METADATA"))))
                .doOnSuccess(result -> log.debug("Franchise found: {}", id))
                .doOnError(error -> log.error("Error finding franchise: {}", id, error))
                .onErrorMap(throwable -> new RuntimeException("Error finding franchise", throwable))
                .map(this::toDomain);
    }
    
    @Override
    public Flux<Franchise> findAll() {
        return Flux.from(getTable().scan().items())
                .doOnComplete(() -> log.debug("All franchises retrieved"))
                .doOnError(error -> log.error("Error retrieving all franchises", error))
                .onErrorMap(throwable -> new RuntimeException("Error retrieving franchises", throwable))
                .map(this::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromFuture(getTable().deleteItem(r -> r.key(k -> k.partitionValue(id))))
                .doOnSuccess(result -> log.debug("Franchise deleted: {}", id))
                .doOnError(error -> log.error("Error deleting franchise: {}", id, error))
                .onErrorMap(throwable -> new RuntimeException("Error deleting franchise", throwable))
                .then();
    }
    
    private FranchiseEntity toEntity(Franchise franchise) {
        return FranchiseEntity.builder()
                .PK("FRANCHISE#" + franchise.getId())
                .SK("METADATA")
                .id(franchise.getId())
                .name(franchise.getName())
                .createdAt(franchise.getCreatedAt())
                .updatedAt(franchise.getUpdatedAt())
                .build();
    }
    
    private Franchise toDomain(FranchiseEntity entity) {
        if (entity == null) {
            return null;
        }
        return Franchise.builder()
                .id(entity.getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
