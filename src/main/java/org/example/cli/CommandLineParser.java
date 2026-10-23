package org.example.cli;

import java.nio.file.Path;

/** Проверяет режим, обязательные пути и неизвестные параметры. */
public final class CommandLineParser {
    public CommandLineOptions parse(String[] args) {
        if (args.length == 1 && "--help".equals(args[0])) {
            return new CommandLineOptions(CommandLineOptions.Mode.HELP, null, null);
        }

        CommandLineOptions.Mode mode = null;
        Path jsonPath = null;
        Path reportPath = null;

        for (int index = 0; index < args.length; index++) {
            String argument = args[index];
            switch (argument) {
                case "--parse":
                    if (mode != null) {
                        throw new IllegalArgumentException("укажите ровно один режим запуска");
                    }
                    mode = CommandLineOptions.Mode.PARSE;
                    break;
                case "--show-report":
                    if (mode != null) {
                        throw new IllegalArgumentException("укажите ровно один режим запуска");
                    }
                    mode = CommandLineOptions.Mode.SHOW_REPORT;
                    break;
                case "--json":
                    if (jsonPath != null) {
                        throw new IllegalArgumentException("параметр --json указан повторно");
                    }
                    jsonPath = readPath(args, index + 1, "--json");
                    index++; // Следующий аргумент — имя файла, оно уже прочитано.
                    break;
                case "--report":
                    if (reportPath != null) {
                        throw new IllegalArgumentException("параметр --report указан повторно");
                    }
                    reportPath = readPath(args, index + 1, "--report");
                    index++;
                    break;
                default:
                    throw new IllegalArgumentException("неизвестный параметр: " + argument);
            }
        }

        if (mode == null) {
            throw new IllegalArgumentException("не указан режим --parse или --show-report");
        }
        if (mode == CommandLineOptions.Mode.PARSE) {
            if (jsonPath == null) {
                throw new IllegalArgumentException("не указан JSON-файл: --json <file>");
            }
            if (reportPath == null) {
                throw new IllegalArgumentException("не указан файл отчёта: --report <file>");
            }
            if (jsonPath.toAbsolutePath().normalize().equals(reportPath.toAbsolutePath().normalize())) {
                throw new IllegalArgumentException("JSON и сериализованный отчёт должны быть разными файлами");
            }
        } else {
            if (jsonPath == null && reportPath == null) {
                throw new IllegalArgumentException("для просмотра укажите --json <file> или --report <file>");
            }
            if (jsonPath != null && reportPath != null) {
                throw new IllegalArgumentException("для просмотра выберите один файл: --json или --report");
            }
        }

        return new CommandLineOptions(mode, jsonPath, reportPath);
    }

    private Path readPath(String[] args, int index, String option) {
        if (index >= args.length || args[index].isBlank() || args[index].startsWith("--")) {
            throw new IllegalArgumentException("после " + option + " укажите путь к файлу");
        }
        return Path.of(args[index]);
    }

    public String usage() {
        return "Использование:\n"
                + "  --parse --json <file> --report <file>   читать System.in, сохранить JSON и отчёт\n"
                + "  --show-report --json <file>            загрузить JSON и показать отчёт\n"
                + "  --show-report --report <file>          загрузить бинарный отчёт\n"
                + "  --help                                 показать эту подсказку\n";
    }
}
