package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class GetAllFranchisesUseCase {
    
    private final FranchiseRepository franchiseRepository;
    
    public Flux<Franchise> execute() {
        return franchiseRepository.findAll();
    }
}
