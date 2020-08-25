package com.rakib.repository;

import com.rakib.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemReactiveMongoRepository extends ReactiveMongoRepository<Item, String> {
    Mono<Item> findByProductDescription(String p);
}
