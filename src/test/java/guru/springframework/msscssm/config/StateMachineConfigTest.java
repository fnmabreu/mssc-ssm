package guru.springframework.msscssm.config;

import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Created by fabreu on 27/11/2021
 */
@SpringBootTest
class StateMachineConfigTest {

    @Autowired
    StateMachineFactory<PaymentState, PaymentEvent> factory;

    @Test
    void testNewStateMachine() {

        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(UUID.randomUUID());

        sm.startReactively().block();

        System.out.println(sm.getState().toString());

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTHORIZE).build()))
                .doOnComplete(() -> {
                    System.out.println("Event handling complete");
                })
                .subscribe();

        System.out.println(sm.getState().toString());

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED).build())).subscribe();

        System.out.println(sm.getState().toString());

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED).build())).subscribe();

        System.out.println(sm.getState().toString());
    }
}