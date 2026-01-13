package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllFranchisesUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private GetAllFranchisesUseCase getAllFranchisesUseCase;

    @BeforeEach
    void setUp() {
        getAllFranchisesUseCase = new GetAllFranchisesUseCase(franchiseRepository);
    }

    @Test
    void shouldReturnAllFranchises() {
        // Given
        Franchise franchise1 = Franchise.builder()
                .id("franchise-1")
                .name("Franchise 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        Franchise franchise2 = Franchise.builder()
                .id("franchise-2")
                .name("Franchise 2")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(franchiseRepository.findAll())
                .thenReturn(Flux.just(franchise1, franchise2));

        // When & Then
        StepVerifier.create(getAllFranchisesUseCase.execute())
                .expectNext(franchise1)
                .expectNext(franchise2)
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenNoFranchises() {
        // Given
        when(franchiseRepository.findAll())
                .thenReturn(Flux.empty());

        // When & Then
        StepVerifier.create(getAllFranchisesUseCase.execute())
                .verifyComplete();
    }
}
