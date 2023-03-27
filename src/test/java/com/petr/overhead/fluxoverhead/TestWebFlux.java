package com.petr.overhead.fluxoverhead;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

public class TestWebFlux {

    private final int count = 100_000;

    @Test
    void test_simple() {
        long started = System.currentTimeMillis();
        ArrayList<Integer> i10List = new ArrayList<>();
        ArrayList<Integer> i100List = new ArrayList<>();

        StepVerifier.create(Flux.range(1, count)
                .handle((i, sink) -> {
                    int ix10 = i * 10;
                    if (ix10 % 10 != 0) {
                        return;
                    }

                    i10List.add(ix10);

                    int ix100 = ix10 * 10;
                    if (ix100 % 100 != 0) {
                        return;
                    }

                    i100List.add(ix100);

                    int ix200 = ix100 * 2;
                    if (ix200 > 1000_000_000) {
                        sink.next(ix200);
                    }

                })).verifyComplete();

        printResult("Functional way: ", (System.currentTimeMillis() - started));
        printResult("Functional way result /10", i10List.subList(0, 10), ANSI_YELLOW);
        printResult("Functional way result /100", i100List.subList(0, 10), ANSI_BLUE);
    }

    @Test
    void test_reactor() {
        long started = System.currentTimeMillis();
        ArrayList<Integer> i10List = new ArrayList<>();
        ArrayList<Integer> i100List = new ArrayList<>();

        StepVerifier.create(Flux.range(1, count)
                        .map(i -> i * 10)
                        .doOnNext(i10List::add)
                        .filter(ix10 -> ix10 % 10 == 0)
                        .map(ix10 -> ix10 * 10)
                        .filter(ix100 -> ix100 % 100 == 0)
                        .doOnNext(i100List::add)
                        .map(ix100 -> ix100 * 2)
                        .filter(ix200 -> ix200 > 1000_000_000))
                .verifyComplete();

        printResult("Reactive way:", (System.currentTimeMillis() - started));
        printResult("Reactive way result /10", i10List.subList(0, 10), ANSI_YELLOW);
        printResult("Reactive way result /100", i100List.subList(0, 10), ANSI_BLUE);
    }

    @Test
    void test_both() {
        test_reactor();
        test_simple();
    }

    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_RESET = "\u001B[0m";

    private void printResult(String name, long time) {
        String ANSI_GREEN = "\u001B[32m";
        String ANSI_RESET = "\u001B[0m";
        String message = String.format("%s%s%s mil sec %s", ANSI_GREEN,
                name,
                time,
                ANSI_RESET);
        System.out.println(message);
    }

    private void printResult(String name, List<Integer> result, String color) {
        String message = String.format("%s%s %s%s",
                color,
                name,
                result,
                ANSI_RESET);
        System.out.println(message);
    }
}
