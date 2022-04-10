package com.embeddediq.searchmonkey;

import java.util.Collection;
import java.util.stream.Collectors;

public class AggregateException extends RuntimeException {
    private final Collection<Exception> myExceptions;

    public AggregateException( Collection<Exception> exceptions) {
        super("Child exceptions:\n" + exceptions
            .stream()
            .map( e -> " * " + e.getClass().getSimpleName() + ": " + e.getMessage() )
            .collect( Collectors.joining("\n"))
        );
        myExceptions = exceptions;
    }
}
