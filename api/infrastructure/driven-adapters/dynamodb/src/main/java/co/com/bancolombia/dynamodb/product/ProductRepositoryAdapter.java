package co.com.bancolombia.dynamodb.product;

import co.com.bancolombia.dynamodb.config.DynamoDBProperties;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.ProductRepository;
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
public class ProductRepositoryAdapter implements ProductRepository {
    
    private final DynamoDbEnhancedAsyncClient dynamoClient;
    private final DynamoDBProperties properties;
    
    private DynamoDbAsyncTable<ProductEntity> getTable() {
        return dynamoClient.table(properties.getTables().getProducts(), 
                TableSchema.fromBean(ProductEntity.class));
    }
    
    @Override
    public Mono<Product> save(Product product) {
        ProductEntity entity = toEntity(product);
        return Mono.fromFuture(getTable().putItem(entity))
                .thenReturn(product);
    }
    
    @Override
    public Mono<Product> findById(String id) {
        return Flux.from(getTable().index("GSI2")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(id)))
                .flatMapIterable(page -> page.items()))
                .next()
                .map(this::toDomain);
    }
    
    @Override
    public Flux<Product> findByBranchId(String branchId) {
        return Flux.from(getTable().index("GSI1")
                .query(QueryConditional.keyEqualTo(k -> k.partitionValue(branchId)))
                .flatMapIterable(page -> page.items()))
                .map(this::toDomain);
    }
    
    @Override
    public Flux<Product> findByFranchiseId(String franchiseId) {
        return Flux.from(getTable().query(QueryConditional.keyEqualTo(k -> k.partitionValue(franchiseId)))
                .flatMapIterable(page -> page.items()))
                .map(this::toDomain);
    }
    
    @Override
    public Flux<Product> findTopStockByFranchise(String franchiseId) {
        return findByFranchiseId(franchiseId)
                .collectList()
                .flatMapMany(products -> Flux.fromIterable(products)
                        .sort((p1, p2) -> Integer.compare(p2.getStock(), p1.getStock()))
                        .take(10));
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return findById(id)
                .flatMap(product -> Mono.fromFuture(getTable().deleteItem(r -> r.key(k -> k
                        .partitionValue(product.getFranchiseId())
                        .sortValue(product.getId())))))
                .then();
    }
    
    private ProductEntity toEntity(Product product) {
        return ProductEntity.builder()
                .id(product.getId())
                .franchiseId(product.getFranchiseId())
                .branchId(product.getBranchId())
                .name(product.getName())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .GSI1PK(product.getBranchId()) // Para búsqueda por sucursal
                .GSI2PK(product.getId())       // Para búsqueda por ID
                .build();
    }
    
    private Product toDomain(ProductEntity entity) {
        return Product.builder()
                .id(entity.getId())
                .franchiseId(entity.getFranchiseId())
                .branchId(entity.getBranchId())
                .name(entity.getName())
                .stock(entity.getStock())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
