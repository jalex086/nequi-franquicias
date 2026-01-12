package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.CreateFranchiseRequest;
import co.com.bancolombia.api.dto.FranchiseResponse;
import co.com.bancolombia.api.dto.UpdateFranchiseNameRequest;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.usecase.franchise.CreateFranchiseUseCase;
import co.com.bancolombia.usecase.franchise.GetAllFranchisesUseCase;
import co.com.bancolombia.usecase.franchise.UpdateFranchiseNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {
    
    private final CreateFranchiseUseCase createFranchiseUseCase;
    private final GetAllFranchisesUseCase getAllFranchisesUseCase;
    private final UpdateFranchiseNameUseCase updateFranchiseNameUseCase;
    
    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequest.class)
                .doOnNext(this::validateRequest)
                .flatMap(req -> createFranchiseUseCase.execute(req.getName()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    private void validateRequest(CreateFranchiseRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (request.getName().trim().length() < 2 || request.getName().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres");
        }
    }
    
    public Mono<ServerResponse> getAllFranchises(ServerRequest request) {
        return getAllFranchisesUseCase.execute()
                .map(this::toResponse)
                .collectList()
                .flatMap(franchises -> ServerResponse.ok().bodyValue(franchises));
    }
    
    public Mono<ServerResponse> updateFranchiseName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(UpdateFranchiseNameRequest.class)
                .flatMap(req -> updateFranchiseNameUseCase.execute(id, req.getName()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    private FranchiseResponse toResponse(Franchise franchise) {
        return FranchiseResponse.builder()
                .id(franchise.getId())
                .name(franchise.getName())
                .build();
    }
}
