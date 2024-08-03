package com.udacity.webcrawler.profiler;

// import java.lang.reflect.InvocationHandler;
// import java.lang.reflect.InvocationTargetException;
// import java.lang.reflect.Method;
// import java.lang.reflect.Modifier;
// import java.time.Clock;
// import java.time.Duration;
// import java.time.Instant;
// import java.util.Objects;

// /**
//  * A method interceptor that checks whether Methods are annotated with the Profiled annotation.
//  * If they are, the method interceptor records how long the method invocation took.
//  */
// final class ProfilingMethodInterceptor<T> implements InvocationHandler {

//     private Clock clock;
//     private T delegate;
//     private ProfilingState state;

//     public ProfilingMethodInterceptor(Clock clock, T delegate, ProfilingState state) {
//         this.clock = clock;
//         this.delegate = delegate;
//         this.state = state;
//     }

//     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
//         Instant start = clock.instant();
//         Object result = null;
        
//         // Check method accessibility
//         if (!Modifier.isPublic(method.getModifiers())) {
//             throw new IllegalAccessException("Method is not accessible");
//         }

//         try {
//             result = method.invoke(delegate, args);
//         } catch (InvocationTargetException e) {
//             // Record method execution time even if an exception is thrown
//             state.record(delegate.getClass(), method, Duration.between(start, clock.instant()));
//             throw e.getTargetException();
//         }

//         // Record method execution time if annotated with Profiled
//         Profiled profiled = method.getAnnotation(Profiled.class);
//         if (profiled != null) {
//             state.record(delegate.getClass(), method, Duration.between(start, clock.instant()));
//         }

//         return result;
//     }
// }



import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

final class ProfilingMethodInterceptor<T> implements InvocationHandler {

    private Clock clock;
    private T delegate;
    private ProfilingState state;

    public ProfilingMethodInterceptor(Clock clock, T delegate, ProfilingState state) {
        this.clock = clock;
        this.delegate = delegate;
        this.state = state;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Instant start = clock.instant();
        Object result = null;

        try {
            // Check method accessibility
            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalAccessException("Method is not accessible");
            }

            // Supplier to execute method invocation
            ResultSupplier resultSupplier = () -> method.invoke(delegate, args);

            // Record method execution time
            result = executeWithProfiling(resultSupplier, method, start);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Error invoking method: " + e.getMessage(), e);
        }

        return result;
    }

    // Functional interface for method invocation
    @FunctionalInterface
    interface ResultSupplier {
        Object get() throws Throwable;
    }

    // Execute method invocation with profiling
    private Object executeWithProfiling(ResultSupplier supplier, Method method, Instant start) throws Throwable {
        try {
            Object result = supplier.get();
            recordMethodExecutionTime(method, start);
            return result;
        } catch (Throwable throwable) {
            recordMethodExecutionTime(method, start);
            throw throwable;
        }
    }

    // Record method execution time
    private void recordMethodExecutionTime(Method method, Instant start) {
        Profiled profiled = method.getAnnotation(Profiled.class);
        if (profiled != null) {
            state.record(delegate.getClass(), method, Duration.between(start, clock.instant()));
        }
    }
}

