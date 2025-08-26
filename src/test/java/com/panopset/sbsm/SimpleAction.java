package com.panopset.sbsm;

import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Component
public class SimpleAction implements Action<States, Events>, Consts {
    @Override
    public void execute(StateContext context) {
        context.getExtendedState().getVariables().put("foo", "bar");
        context.getExtendedState().getVariables().put(SSM_ONDECK, Events.FROM_PROCESS);
    }
}
