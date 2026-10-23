package org.example.model;

import java.io.Serializable;
import java.util.Objects;

/** Номер, исходный текст и причина ошибки разбора. */
public final class InvalidLine implements Serializable {
    private static final long serialVersionUID = 1L;

    private final long lineNumber;
    private final String line;
    private final String reason;

    public InvalidLine(long lineNumber, String line, String reason) {
        this.lineNumber = lineNumber;
        this.line = Objects.requireNonNull(line, "line");
        this.reason = Objects.requireNonNull(reason, "reason");
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public String getLine() {
        return line;
    }

    public String getReason() {
        return reason;
    }
}
