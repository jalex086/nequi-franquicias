package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateFranchiseUseCase {
    
    private final FranchiseRepository franchiseRepository;
    
    public Mono<Franchise> execute(String name) {
        return Mono.just(Franchise.create(name))
                .map(franchise -> franchise.toBuilder()
                        .id(UUID.randomUUID().toString())
                        .build())
                .flatMap(franchiseRepository::save);
    }
}
