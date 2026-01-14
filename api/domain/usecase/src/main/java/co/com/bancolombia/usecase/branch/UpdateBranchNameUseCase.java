package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.franchise.Branch;
import co.com.bancolombia.model.franchise.gateways.BranchRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateBranchNameUseCase {
    
    private final BranchRepository branchRepository;
    
    public Mono<Branch> execute(String id, String name) {
        if (id == null || id.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El ID de la sucursal es requerido"));
        }
        
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre es requerido"));
        }
        
        if (name.trim().length() < 2 || name.trim().length() > 100) {
            return Mono.error(new IllegalArgumentException("El nombre debe tener entre 2 y 100 caracteres"));
        }
        
        return branchRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Sucursal con ID " + id + " not found")))
                .map(branch -> branch.toBuilder().name(name.trim()).build())
                .flatMap(branchRepository::save);
    }
}
