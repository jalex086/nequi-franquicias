package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UpdateFranchiseNameUseCase {
    
    private final FranchiseRepository franchiseRepository;
    
    public Mono<Franchise> execute(String franchiseId, String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El nombre es requerido"));
        }
        
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new RuntimeException("Franchise not found")))
                .map(franchise -> franchise.updateName(newName.trim()))
                .flatMap(franchiseRepository::save);
    }
}
