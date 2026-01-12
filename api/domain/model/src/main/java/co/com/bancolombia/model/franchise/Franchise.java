package co.com.bancolombia.model.franchise;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
@Builder(toBuilder = true)
public class Franchise {
    String id;
    String name;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    List<Branch> branches;
    
    public static Franchise create(String name) {
        return Franchise.builder()
                .name(name)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
    
    public Franchise updateName(String newName) {
        return this.toBuilder()
                .name(newName)
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
