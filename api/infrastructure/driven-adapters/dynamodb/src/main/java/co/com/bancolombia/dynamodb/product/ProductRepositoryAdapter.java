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
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;

import java.time.LocalDateTime;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryAdapter implements ProductRepository {

    private static final String STOCK = "stock";
    private static final String CREATED_AT = "createdAt";
    private static final String UPDATED_AT = "updatedAt";
    private static final String PRODUCTS = "products";
    private static final String METADATA_SK = "METADATA";
    private static final String PRODUCT = "PRODUCT#";

    private final DynamoDbEnhancedAsyncClient dynamoClient;
    private final DynamoDbAsyncClient basicDynamoClient;
    private final DynamoDBProperties properties;
    
    private DynamoDbAsyncTable<ProductEntity> getTable() {
        return dynamoClient.table(properties.getTables().getProducts(), 
                TableSchema.fromBean(ProductEntity.class));
    }
    
    @Override
    public Mono<Product> save(Product product) {
        Map<String, AttributeValue> item = Map.of(
            "PK", AttributeValue.builder().s(PRODUCT + product.getId()).build(),
            "SK", AttributeValue.builder().s(METADATA_SK).build(),
            "id", AttributeValue.builder().s(product.getId()).build(),
            "franchiseId", AttributeValue.builder().s(product.getFranchiseId()).build(),
            "branchId", AttributeValue.builder().s(product.getBranchId()).build(),
            "name", AttributeValue.builder().s(product.getName()).build(),
            STOCK, AttributeValue.builder().n(String.valueOf(product.getStock())).build(),
            CREATED_AT, AttributeValue.builder().s(product.getCreatedAt().toString()).build(),
            UPDATED_AT, AttributeValue.builder().s(product.getUpdatedAt().toString()).build(),
            "GSI1PK", AttributeValue.builder().s(product.getBranchId()).build()
        );
        
        return Mono.fromFuture(basicDynamoClient.putItem(PutItemRequest.builder()
                .tableName(properties.getTables().getProducts())
                .item(item)
                .build()))
                .thenReturn(product);
    }
    
    @Override
    public Mono<Product> findById(String id) {
        return Mono.fromFuture(basicDynamoClient.getItem(GetItemRequest.builder()
                .tableName(properties.getTables().getProducts())
                .key(Map.of(
                    "PK", AttributeValue.builder().s(PRODUCT + id).build(),
                    "SK", AttributeValue.builder().s(METADATA_SK).build()
                ))
                .build()))
                .filter(response -> response.item() != null && !response.item().isEmpty())
                .map(response -> {
                    Map<String, AttributeValue> item = response.item();
                    return Product.builder()
                            .id(item.get("id").s())
                            .franchiseId(item.get("franchiseId").s())
                            .branchId(item.get("branchId").s())
                            .name(item.get("name").s())
                            .stock(Integer.parseInt(item.get(STOCK).n()))
                            .createdAt(LocalDateTime.parse(item.get(CREATED_AT).s()))
                            .updatedAt(LocalDateTime.parse(item.get(UPDATED_AT).s()))
                            .build();
                })
                .switchIfEmpty(
                    findEmbeddedProductById(id)
                );
    }
    
    private Mono<Product> findEmbeddedProductById(String productId) {
        return Mono.fromFuture(basicDynamoClient.scan(ScanRequest.builder()
                .tableName(properties.getTables().getBranches())
                .build()))
                .flatMapMany(response -> Flux.fromIterable(response.items()))
                .filter(item -> item.containsKey(PRODUCTS) && item.get(PRODUCTS).l() != null)
                .flatMap(branchItem -> {
                    String branchId = branchItem.get("id").s();
                    return Flux.fromIterable(branchItem.get(PRODUCTS).l())
                            .filter(productAttr -> productAttr.m().get("id").s().equals(productId))
                            .map(productAttr -> {
                                Map<String, AttributeValue> productMap = productAttr.m();
                                return Product.builder()
                                        .id(productMap.get("id").s())
                                        .branchId(branchId)
                                        .name(productMap.get("name").s())
                                        .stock(Integer.parseInt(productMap.get(STOCK).n()))
                                        .createdAt(LocalDateTime.parse(productMap.get(CREATED_AT).s()))
                                        .updatedAt(LocalDateTime.parse(productMap.get(UPDATED_AT).s()))
                                        .build();
                            });
                })
                .next();
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
        return Flux.from(getTable().scan().items())
                .filter(entity -> franchiseId.equals(entity.getFranchiseId()))
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
        return Mono.fromFuture(basicDynamoClient.deleteItem(DeleteItemRequest.builder()
                .tableName(properties.getTables().getProducts())
                .key(Map.of(
                    "PK", AttributeValue.builder().s(PRODUCT + id).build(),
                    "SK", AttributeValue.builder().s(METADATA_SK).build()
                ))
                .build()))
                .then();
    }
    
    private Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
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
