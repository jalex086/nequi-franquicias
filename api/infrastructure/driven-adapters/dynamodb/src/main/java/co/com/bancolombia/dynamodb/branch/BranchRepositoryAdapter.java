package co.com.bancolombia.dynamodb.branch;

import co.com.bancolombia.dynamodb.config.DynamoDBProperties;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.Product;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BranchRepositoryAdapter implements BranchRepository {
    
    private static final int EMBEDDED_PRODUCT_LIMIT = 100;
    private static final String EMBEDDED_STRATEGY = "EMBEDDED";
    private static final String SEPARATED_STRATEGY = "SEPARATED";
    private static final String BRANCH_PREFIX = "BRANCH#";
    private static final String METADATA_SK = "METADATA";
    private static final String PRODUCTS = "products";
    
    private final DynamoDbEnhancedAsyncClient dynamoClient;
    private final DynamoDBProperties properties;

    private final software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient basicDynamoClient;
    
    private DynamoDbAsyncTable<BranchEntity> getTable() {
        return dynamoClient.table(properties.getTables().getBranches(), 
                TableSchema.fromBean(BranchEntity.class));
    }
    
    @Override
    public Mono<Branch> save(Branch branch) {
        String strategy = branch.getStorageStrategy() != null ? branch.getStorageStrategy() : determineStorageStrategy(branch.getProducts());
        BranchEntity entity = toEntity(branch, strategy);
        
        return Mono.fromFuture(getTable().putItem(entity))
                .thenReturn(branch);
    }
    
    @Override
    public Mono<Branch> findById(String id) {
        return Mono.fromFuture(getTable().getItem(r -> r.key(k -> k.partitionValue(BRANCH_PREFIX + id).sortValue(METADATA_SK))))
                .map(this::toDomain);
    }
    
    @Override
    public Flux<Branch> findByFranchiseId(String franchiseId) {
        return Flux.from(getTable().scan().items())
                .filter(entity -> franchiseId.equals(entity.getFranchiseId()))
                .map(this::toDomain);
    }
    
    @Override
    public Mono<Void> deleteById(String id) {
        return Mono.fromFuture(getTable().deleteItem(r -> r.key(k -> k.partitionValue(BRANCH_PREFIX + id).sortValue(METADATA_SK))))
                .then();
    }
    
    @Override
    public Mono<Branch> addProduct(String branchId, Product product) {
        String pk = BRANCH_PREFIX + branchId;
        String sk = METADATA_SK;

        BranchEntity.EmbeddedProduct embeddedProduct = BranchEntity.EmbeddedProduct.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
        
        UpdateItemRequest updateRequest = UpdateItemRequest.builder()
                .tableName(properties.getTables().getBranches())
                .key(Map.of(
                    "PK", AttributeValue.builder().s(pk).build(),
                    "SK", AttributeValue.builder().s(sk).build()
                ))
                .updateExpression("SET products = list_append(if_not_exists(products, :empty_list), :new_product)")
                .expressionAttributeValues(Map.of(
                    ":empty_list", AttributeValue.builder().l(List.of()).build(),
                    ":new_product", AttributeValue.builder().l(
                        AttributeValue.builder().m(Map.of(
                            "id", AttributeValue.builder().s(embeddedProduct.getId()).build(),
                            "name", AttributeValue.builder().s(embeddedProduct.getName()).build(),
                            "stock", AttributeValue.builder().n(embeddedProduct.getStock().toString()).build(),
                            "createdAt", AttributeValue.builder().s(embeddedProduct.getCreatedAt().toString()).build(),
                            "updatedAt", AttributeValue.builder().s(embeddedProduct.getUpdatedAt().toString()).build()
                        )).build()
                    ).build()
                ))
                .build();
        
        return Mono.fromFuture(basicDynamoClient.updateItem(updateRequest))
                .then(findById(branchId));
    }
    
    private String determineStorageStrategy(List<Product> products) {
        return (products != null && products.size() >= EMBEDDED_PRODUCT_LIMIT) 
                ? SEPARATED_STRATEGY 
                : EMBEDDED_STRATEGY;
    }
    
    private BranchEntity toEntity(Branch branch, String strategy) {
        BranchEntity.BranchEntityBuilder builder = BranchEntity.builder()
                .id(branch.getId())
                .franchiseId(branch.getFranchiseId())
                .name(branch.getName())
                .createdAt(branch.getCreatedAt())
                .updatedAt(branch.getUpdatedAt())
                .storageStrategy(strategy);

        if (EMBEDDED_STRATEGY.equals(strategy) && branch.getProducts() != null) {
            List<BranchEntity.EmbeddedProduct> embeddedProducts = branch.getProducts().stream()
                    .map(this::toEmbeddedProduct)
                    .collect(Collectors.toList());
            builder.products(embeddedProducts);
        }
        
        return builder.build();
    }
    
    private BranchEntity.EmbeddedProduct toEmbeddedProduct(Product product) {
        return BranchEntity.EmbeddedProduct.builder()
                .id(product.getId())
                .name(product.getName())
                .stock(product.getStock())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
    
    private Branch toDomain(BranchEntity entity) {
        if (entity == null) {
            return null;
        }
        
        Branch.BranchBuilder builder = Branch.builder()
                .id(entity.getId())
                .franchiseId(entity.getFranchiseId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .storageStrategy(entity.getStorageStrategy());

        if (entity.getProducts() != null) {
            List<Product> products = entity.getProducts().stream()
                    .map(this::fromEmbeddedProduct)
                    .collect(Collectors.toList());
            builder.products(products);
        }
        
        return builder.build();
    }
    
    private Product fromEmbeddedProduct(BranchEntity.EmbeddedProduct embedded) {
        return Product.builder()
                .id(embedded.getId())
                .name(embedded.getName())
                .stock(embedded.getStock())
                .createdAt(embedded.getCreatedAt())
                .updatedAt(embedded.getUpdatedAt())
                .build();
    }

    @Override
    public Mono<String> findBranchIdByProductId(String productId) {
        return Mono.fromFuture(basicDynamoClient.getItem(GetItemRequest.builder()
                .tableName(properties.getTables().getProducts())
                .key(Map.of(
                    "PK", AttributeValue.builder().s("PRODUCT#" + productId).build(),
                    "SK", AttributeValue.builder().s(METADATA_SK).build()
                ))
                .build()))
                .filter(response -> response.item() != null && !response.item().isEmpty())
                .map(response -> response.item().get("branchId").s())
                .switchIfEmpty(
                    Mono.fromFuture(basicDynamoClient.scan(ScanRequest.builder()
                            .tableName(properties.getTables().getBranches())
                            .build()))
                            .flatMapMany(response -> Flux.fromIterable(response.items()))
                            .filter(item -> {
                                if (item.get(PRODUCTS) != null && item.get(PRODUCTS).l() != null) {
                                    return item.get(PRODUCTS).l().stream()
                                            .anyMatch(p -> productId.equals(p.m().get("id").s()));
                                }
                                return false;
                            })
                            .map(item -> item.get("PK").s().replace(BRANCH_PREFIX, ""))
                            .next()
                );
    }
}
