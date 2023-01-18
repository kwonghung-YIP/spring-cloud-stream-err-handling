package poc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
@SpringBootApplication
public class KafkaTranPocApplication {

    public static void main(String[] args) {
        SpringApplication.run(KafkaTranPocApplication.class, args);
    }

    private Random random = new Random();

    @Bean
    public Function<Flux<Long>,Flux<Long>> simple() {
        return (flux) -> {
            return flux.map(l -> {
                log.info("Received counter [{}]",l);
                return l * 2;
            });
        };
    }

    @Bean
    public Supplier<Tuple2<Long,Long>> boardcastSupp() {
        return () -> {
            long v1 = random.nextLong(1,100);
            long v2 = 100 - v1;
            return Tuples.of(v1,v2);
        };
    }

    @Bean
    public Function<Flux<Long>,Tuple2<Flux<Long>, Flux<Long>>> boardcastFn() {
        return (flux) -> {
            flux.doOnNext(l -> {
                log.info("Counter received {}", l);
            });
            long v1 = random.nextLong(1,100);
            long v2 = 100 - v1;
            return Tuples.of(Flux.just(v1),Flux.just(v2));
        };
    }
}
