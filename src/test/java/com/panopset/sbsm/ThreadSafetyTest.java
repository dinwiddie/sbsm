package com.panopset.sbsm;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import static com.panopset.sbsm.SimpleStateMachineConfig.sendEvent;
import static com.panopset.sbsm.States.*;

@SpringBootTest
public class ThreadSafetyTest implements Consts {
    StateMachineFactory<States, Events> stateMachineFactory;

    public ThreadSafetyTest(
            @Autowired StateMachineFactory<States, Events> stateMachineFactory
    ) {
        this.stateMachineFactory = stateMachineFactory;
    }

    @Test
    public void test() {
        testCycle();
    }

    @Test
    public void test2() {
        testCycle();
    }

    private int TOTAL = 1000;

    @Test
    public void testSeveral() {
        for (int i=0; i<TOTAL; i++) {
            System.out.println("testSeveral " + i + " of " + TOTAL );
            testCycle();
        }
    }

    private void testCycle() {
        StateMachine simpleSSM = stateMachineFactory.getStateMachine();
        simpleSSM.startReactively().subscribe();
        Assertions.assertEquals(INIT, simpleSSM.getState().getId());
        sendEvent(simpleSSM, Events.START);
        Assertions.assertEquals(MIDDLE, simpleSSM.getState().getId());
        sendEvent(simpleSSM, Events.FROM_EXTERNAL);
        Assertions.assertEquals(JACKSON, simpleSSM.getState().getId());
        Assertions.assertEquals("bar", simpleSSM.getExtendedState().getVariables().get("foo"));
        Events onDeck = simpleSSM.getExtendedState().get(SSM_ONDECK, Events.class);
        sendEvent(simpleSSM, onDeck);
        Assertions.assertEquals(TOTALS, simpleSSM.getState().getId());
        sendEvent(simpleSSM, Events.WRAP);
        Assertions.assertEquals(END, simpleSSM.getState().getId());
    }
}
