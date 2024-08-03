package com.udacity.webcrawler.profiler;

import javax.inject.Inject;
import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME;

/**
 * Concrete implementation of the {@link Profiler}.
 */
public final class ProfilerImpl implements Profiler {

    private final Clock clock;
    private final ProfilingState state = new ProfilingState();
    private final ZonedDateTime startTime;

    @Inject
    public ProfilerImpl(Clock clock) {
        this.clock = Objects.requireNonNull(clock);
        this.startTime = ZonedDateTime.now(clock);
    }

    @Override
    public <T> T wrap(Class<T> klass, T delegate) {
        Objects.requireNonNull(klass);
        List<Annotation> annotationList= Arrays.stream(klass.getMethods())
                .map(Method::getDeclaredAnnotations)
                .flatMap(Stream::of)
                .collect(Collectors.toList());
        if (annotationList.stream().filter(a -> a.annotationType().equals(Profiled.class))
                .findFirst().isEmpty()) {
            throw new IllegalArgumentException();
        }
        Object proxy = Proxy.newProxyInstance(klass.getClassLoader(), new Class[]{klass}, new ProfilingMethodInterceptor(clock, delegate, state));
        return (T) proxy;
    }

    @Override
    public void writeData(Path path) {
        try (Writer writer = Files.newBufferedWriter(path, StandardOpenOption.APPEND)) {
            state.write(writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

@Override
public void writeData(Writer writer) throws IOException {
    writer.write("Run at " + RFC_1123_DATE_TIME.format(startTime));
    writer.write(System.lineSeparator());
    state.write(writer);
    writer.write(System.lineSeparator());
}
}
