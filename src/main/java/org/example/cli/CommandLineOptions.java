package org.example.cli;

import java.nio.file.Path;

/** Проверенные параметры одного режима запуска. */
public final class CommandLineOptions {
    public enum Mode {
        PARSE,
        SHOW_REPORT,
        HELP
    }

    private final Mode mode;
    private final Path jsonPath;
    private final Path reportPath;

    CommandLineOptions(Mode mode, Path jsonPath, Path reportPath) {
        this.mode = mode;
        this.jsonPath = jsonPath;
        this.reportPath = reportPath;
    }

    public Mode getMode() {
        return mode;
    }

    public Path getJsonPath() {
        return jsonPath;
    }

    public Path getReportPath() {
        return reportPath;
    }
}
