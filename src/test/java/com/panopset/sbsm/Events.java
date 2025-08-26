package com.panopset.sbsm;

import static com.panopset.sbsm.States.*;

public enum Events {
    START, //(INIT, MIDDLE),
    FROM_EXTERNAL, // ( MIDDLE, JACKSON),
    FROM_PROCESS, //( JACKSON, TOTALS),
    WRAP; //(TOTALS, END);

    @Override
    public String toString() {
        return this.name();
    }
}
