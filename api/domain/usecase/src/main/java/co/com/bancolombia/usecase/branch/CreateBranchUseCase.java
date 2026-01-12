package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateBranchUseCase {
    
    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;
    
    public Mono<Branch> execute(String franchiseId, String name) {
        if (franchiseId == null || franchiseId.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la franquicia es requerido"));
        }
        
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre de la sucursal es requerido"));
        }
        
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franquicia con ID " + franchiseId + " not found")))
                .then(Mono.just(Branch.create(franchiseId, name.trim())))
                .map(branch -> branch.toBuilder()
                        .id(UUID.randomUUID().toString())
                        .build())
                .flatMap(branchRepository::save);
    }
}
