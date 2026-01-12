package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateFranchiseNameUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    @BeforeEach
    void setUp() {
        updateFranchiseNameUseCase = new UpdateFranchiseNameUseCase(franchiseRepository);
    }

    @Test
    void shouldUpdateFranchiseNameSuccessfully() {
        // Given
        String franchiseId = "franchise-1";
        String newName = "Updated Franchise";
        
        Franchise existingFranchise = Franchise.builder()
                .id(franchiseId)
                .name("Old Name")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Franchise updatedFranchise = existingFranchise.toBuilder()
                .name(newName)
                .build();

        when(franchiseRepository.findById(franchiseId))
                .thenReturn(Mono.just(existingFranchise));
        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(updatedFranchise));

        // When & Then
        StepVerifier.create(updateFranchiseNameUseCase.execute(franchiseId, newName))
                .expectNextMatches(franchise -> 
                    franchise.getName().equals(newName) && 
                    franchise.getId().equals(franchiseId))
                .verifyComplete();
    }

    @Test
    void shouldFailWhenFranchiseNotFound() {
        // Given
        when(franchiseRepository.findById(anyString()))
                .thenReturn(Mono.empty());

        // When & Then
        StepVerifier.create(updateFranchiseNameUseCase.execute("nonexistent", "New Name"))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().contains("not found"))
                .verify();
    }

    @Test
    void shouldFailWhenNameIsEmpty() {
        // When & Then
        StepVerifier.create(updateFranchiseNameUseCase.execute("franchise-1", ""))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("El nombre es requerido"))
                .verify();
    }
}
