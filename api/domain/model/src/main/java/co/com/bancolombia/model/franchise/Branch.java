package co.com.bancolombia.model.franchise;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class Branch {
    String id;
    String franchiseId;
    String name;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<Product> products;
    String storageStrategy; // EMBEDDED | SEPARATED
    
    public static Branch create(String franchiseId, String name) {
        return Branch.builder()
                .franchiseId(franchiseId)
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public Branch updateName(String newName) {
        return this.toBuilder()
                .name(newName)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
