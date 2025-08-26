package com.panopset.sbsm;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.config.configurers.ExternalTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import reactor.core.publisher.Mono;

import java.util.HashSet;

import static com.panopset.sbsm.Events.*;
import static com.panopset.sbsm.States.*;

@Configuration
@EnableStateMachineFactory
public class SimpleStateMachineConfig extends EnumStateMachineConfigurerAdapter<States, Events> {
    private final SimpleAction simpleAction;
    public SimpleStateMachineConfig(
            SimpleAction simpleAction
    ) {
        this.simpleAction = simpleAction;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<States, Events> config) throws Exception {
        config.withConfiguration().autoStartup(false).listener(listener());
    }

    @Override
    public void configure(StateMachineStateConfigurer<States, Events> states) throws Exception {
        HashSet<States> set = new HashSet<>();
        set.add(JACKSON);
        set.add(MIDDLE);
        set.add(TOTALS);
        states
                .withStates()
                .initial(INIT)
                .states(set)
                .end(END);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<States, Events> transitions) throws Exception {
        transitions.withExternal()
                .source(INIT).target(MIDDLE)
                .event(START)
                .and().withExternal()

                .source(MIDDLE).target(JACKSON)
                .event(FROM_EXTERNAL)
                .action(simpleAction)
                .and().withExternal()

                .source(JACKSON).target(TOTALS)
                .event(FROM_PROCESS)
                .and().withExternal()

                .source(TOTALS).target(END)
                .event(WRAP)
                ;
    }

    private StateMachineListener<States, Events> listener() {
        return new StateMachineListenerAdapter<>() {
            @Override
            public void stateChanged(State<States, Events> from, State<States, Events> to) {
                super.stateChanged(from, to);
                String fromName = "nul";
                if (from != null) {
                    fromName = from.getId().name();
                }
                System.out.println("State changed "
                        + fromName + " to " + to.getId().name());
            }
        };
    }

    public static void sendEvent(StateMachine<States, Events> stateMachine, Events event) {
        stateMachine.sendEvent(
                Mono.just(
                        MessageBuilder.withPayload(event).build()
                )
        ).then(Mono.defer(() -> Mono.just(stateMachine.getState().getId()))).subscribe();
    }
}
