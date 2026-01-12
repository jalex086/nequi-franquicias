package co.com.bancolombia.api.handler;

import co.com.bancolombia.api.dto.BranchResponse;
import co.com.bancolombia.api.dto.CreateBranchRequest;
import co.com.bancolombia.api.dto.UpdateBranchNameRequest;
import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.usecase.branch.CreateBranchUseCase;
import co.com.bancolombia.usecase.branch.UpdateBranchNameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {
    
    private final CreateBranchUseCase createBranchUseCase;
    private final UpdateBranchNameUseCase updateBranchNameUseCase;
    
    public Mono<ServerResponse> createBranch(ServerRequest request) {
        String franchiseId = request.pathVariable("franchiseId");
        return request.bodyToMono(CreateBranchRequest.class)
                .doOnNext(req -> validateBranchRequest(req, franchiseId))
                .flatMap(req -> createBranchUseCase.execute(franchiseId, req.getName()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    public Mono<ServerResponse> updateBranchName(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(UpdateBranchNameRequest.class)
                .doOnNext(req -> validateUpdateNameRequest(req, id))
                .flatMap(req -> updateBranchNameUseCase.execute(id, req.getName()))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }
    
    private void validateBranchRequest(CreateBranchRequest request, String franchiseId) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la franquicia es requerido");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (request.getName().trim().length() < 2 || request.getName().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres");
        }
    }
    
    private void validateUpdateNameRequest(UpdateBranchNameRequest request, String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la sucursal es requerido");
        }
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es requerido");
        }
        if (request.getName().trim().length() < 2 || request.getName().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres");
        }
    }
    
    private BranchResponse toResponse(Branch branch) {
        return BranchResponse.builder()
                .id(branch.getId())
                .name(branch.getName())
                .franchiseId(branch.getFranchiseId())
                .build();
    }
}
