package com.muzima.search.api.aspect;

/**
 * TODO: Write brief description about the class here.
 */
public aspect JoinPointTraceAspect {

    private int callDepth;

    pointcut traced(): !within(JoinPointTraceAspect);

    before(): traced() {
        print("Before", thisJoinPoint);
        callDepth++;
    }

    after(): traced() {
        callDepth--;
        print("After", thisJoinPoint);
    }

    private void print(String prefix, Object message) {
        for (int i = 0; i < callDepth; i++) {
            System.out.print("  ");
        }
        System.out.println(prefix + ": " + message);
    }
}
