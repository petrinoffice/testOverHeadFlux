package com.petr.overhead.fluxoverhead;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;

import java.util.Objects;

@SpringBootApplication
public class FluxOverheadApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(FluxOverheadApplication.class, args);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Flux.range(1, 10).log()
                .filter(s -> s % 2 == 0).log()
                .map(Objects::toString).log()
                .take(5).log()
                .subscribe(System.out::println);
    }
}
