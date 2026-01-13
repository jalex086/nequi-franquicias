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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreateFranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    private CreateFranchiseUseCase createFranchiseUseCase;

    @BeforeEach
    void setUp() {
        createFranchiseUseCase = new CreateFranchiseUseCase(franchiseRepository);
    }

    @Test
    void shouldCreateFranchiseSuccessfully() {
        // Given
        String name = "Test Franchise";
        Franchise expectedFranchise = Franchise.builder()
                .id("test-id")
                .name(name)
                .build();

        when(franchiseRepository.save(any(Franchise.class)))
                .thenReturn(Mono.just(expectedFranchise));

        // When & Then
        StepVerifier.create(createFranchiseUseCase.execute(name))
                .expectNextMatches(franchise -> 
                    franchise.getName().equals(name) && 
                    franchise.getId() != null)
                .verifyComplete();
    }
}
