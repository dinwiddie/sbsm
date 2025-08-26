package com.panopset.sbsm;

import static com.panopset.sbsm.States.*;

public enum Events {
    START(INIT, MIDDLE),
    FROM_EXTERNAL( MIDDLE, JACKSON),
    FROM_PROCESS( JACKSON, TOTALS),
    WRAP(TOTALS, END);

    public final States fromState;
    public final States toState;

    Events(States fromState, States toState) {
        this.fromState = fromState;
        this.toState = toState;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
