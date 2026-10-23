package org.example.parser;

/** Ошибка формата одной входной строки. */
public final class ParseException extends IllegalArgumentException {
    public ParseException(String message) {
        super(message);
    }
}
