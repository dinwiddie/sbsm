# sbsm

Spring boot state machine action thread safety issue.

To reproduce the issue, we run our testCycle 10,000 times, and usually by iteration 200 it will fail.

The reason for the failure, is that a State action does not complete before the next transition.
