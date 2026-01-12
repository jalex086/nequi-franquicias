package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.CreateFranchiseRequest;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.GetAllFranchisesUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.reactive.function.server.MockServerRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseHandlerTest {

    @Mock
    private CreateFranchiseUseCase createFranchiseUseCase;
    
    @Mock
    private GetAllFranchisesUseCase getAllFranchisesUseCase;
    
    @Mock
    private UpdateFranchiseNameUseCase updateFranchiseNameUseCase;

    private FranchiseHandler franchiseHandler;

    @BeforeEach
    void setUp() {
        franchiseHandler = new FranchiseHandler(
            createFranchiseUseCase, 
            getAllFranchisesUseCase, 
            updateFranchiseNameUseCase
        );
    }

    @Test
    void shouldCreateFranchiseSuccessfully() {
        // Given
        CreateFranchiseRequest request = CreateFranchiseRequest.builder()
                .name("Test Franchise")
                .build();
                
        Franchise franchise = Franchise.builder()
                .id("franchise-1")
                .name("Test Franchise")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MockServerRequest serverRequest = MockServerRequest.builder()
                .body(Mono.just(request));

        when(createFranchiseUseCase.execute(anyString()))
                .thenReturn(Mono.just(franchise));

        // When & Then
        StepVerifier.create(franchiseHandler.createFranchise(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }

    @Test
    void shouldGetAllFranchisesSuccessfully() {
        // Given
        Franchise franchise1 = Franchise.builder()
                .id("franchise-1")
                .name("Franchise 1")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        MockServerRequest serverRequest = MockServerRequest.builder().build();

        when(getAllFranchisesUseCase.execute())
                .thenReturn(Flux.just(franchise1));

        // When & Then
        StepVerifier.create(franchiseHandler.getAllFranchises(serverRequest))
                .expectNextMatches(response -> response.statusCode().is2xxSuccessful())
                .verifyComplete();
    }
}
