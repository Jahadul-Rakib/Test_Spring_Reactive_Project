package com.rakib.repository;

import com.rakib.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveMongoRepositoryTest {

    @Autowired
    ItemReactiveMongoRepository repository;

    List<Item> itemList = Arrays.asList(
            new Item("1", "Radio", 12.0),
            new Item("2", "TV", 12.0),
            new Item("3", "Laptop", 12.0),
            new Item("4", "Fan", 12.0)
    );

    @Before
    public void setUp() {
        repository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(repository::save)
                .doOnNext(System.out::println)
                .blockLast();
    }

    @Test
    public void getAllItemsTest() {
        StepVerifier.create(repository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItemTest() {
        StepVerifier.create(repository.findById("1"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(12.0))
                .verifyComplete();
    }

    @Test
    public void getItemByProductDescriptionTest() {
        StepVerifier.create(repository.findByProductDescription("Radio"))
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(12.0))
                .verifyComplete();
    }

    @Test
    public void saveItemTest() {
        Item item = new Item("6", "Pen", 15.0);
        Mono<Item> save = repository.save(item);
        StepVerifier.create(save)
                .expectSubscription()
                .expectNextMatches(items -> items.getPrice().equals(15.0))
                .verifyComplete();
    }

    @Test
    public void updateItemTest() {
        Mono<Item> itemMono = repository.findByProductDescription("Radio")
                .map((item) -> {
                    item.setPrice(30.0);
                    return item;
                })
                .flatMap((i) -> {
                    return repository.save(i);
                });

        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextMatches(items -> items.getPrice().equals(30.0))
                .verifyComplete();
    }

}