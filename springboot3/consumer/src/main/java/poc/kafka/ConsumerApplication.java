package poc.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.binder.kafka.ListenerContainerWithDlqAndRetryCustomizer;
import org.springframework.cloud.stream.config.ListenerContainerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.ErrorMessage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@SpringBootApplication
public class ConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    @Bean
    public Consumer<Long> consume2() {
        return (l) -> {
            log.info("Received counter [{}]", l);
            if (Math.random()>0.5) {
                log.info("Raise exception when processing counter [{}]", l);
                throw new RuntimeException("Consumer raised exception!!");
            }
        };
    }

    /*@Bean
    @GlobalChannelInterceptor(patterns = "*")
    public ChannelInterceptor channelListener() {
        return new ChannelInterceptor() {
            @Override
            public Message<?> postReceive(Message<?> message, MessageChannel channel) {
                log.info("Channel Interceptor post receive...");
                return message;
            }
        };
    }*/

    @Bean
    public Consumer<Message<Long>> consume() {
        return (msg) -> {
            MessageHeaders headers = msg.getHeaders();
            String topic = headers.get(KafkaHeaders.RECEIVED_TOPIC,String.class);
            AtomicInteger attempt = headers.get("deliveryAttempt", AtomicInteger.class);
            long l = (Long)msg.getPayload();
            log.info("Received counter [{}] from topic [{}] attempt [{}]", l, topic, attempt);
            if (Math.random()>0.5) {
                log.info("Raise exception when processing counter [{}]", l);
                throw new RuntimeException("Consumer raised exception!!");
            }
        };
    }

    @Bean
    public Consumer<ErrorMessage> myErrorHandler() {
        //RetryTemplate.builder().build();

        //new Backoff();

        return (errMsg) -> {
            log.info("ErrorMessage received {}",errMsg.getPayload());
            throw new RuntimeException(errMsg.getPayload());
        };
    }

    /*public ListenerContainerCustomizer<AbstractMessageListenerContainer> containerCustomizer() {
        return (container, dest, group) -> container.pause(); //.setAdviceChain(advice1, advice2);
    }*/

    /*public ListenerContainerWithDlqAndRetryCustomizer dlqCfg() {
        return new ListenerContainerWithDlqAndRetryCustomizer() {

        }
    }*/

}
