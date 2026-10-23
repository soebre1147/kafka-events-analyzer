package org.example.facade;

import org.example.analysis.KafkaAnalysisReport;
import org.example.analysis.KafkaAnalyzer;
import org.example.cli.CommandLineOptions;
import org.example.cli.CommandLineParser;
import org.example.model.ParsedResult;
import org.example.parser.EventProcessor;
import org.example.report.KafkaReportBuilder;
import org.example.storage.JavaReportSerializer;
import org.example.storage.JsonResultReader;
import org.example.storage.JsonResultWriter;

import java.io.IOException;

// Фасад вызывает остальные классы в нужном порядке.
public class KafkaAnalysisFacade {
    private final CommandLineParser commandLineParser = new CommandLineParser();
    private final EventProcessor eventProcessor = new EventProcessor();
    private final KafkaAnalyzer analyzer = new KafkaAnalyzer();
    private final KafkaReportBuilder reportBuilder = new KafkaReportBuilder();
    private final JsonResultWriter jsonWriter = new JsonResultWriter();
    private final JsonResultReader jsonReader = new JsonResultReader();
    private final JavaReportSerializer reportSerializer = new JavaReportSerializer();

    public int run(String[] args) {
        CommandLineOptions options;
        try {
            options = commandLineParser.parse(args);
        } catch (IllegalArgumentException exception) {
            System.err.println("Ошибка запуска: " + exception.getMessage());
            System.err.println(commandLineParser.usage());
            return 2;
        }

        if (options.getMode() == CommandLineOptions.Mode.HELP) {
            System.out.print(commandLineParser.usage());
            return 0;
        }

        try {
            KafkaAnalysisReport report;
            if (options.getMode() == CommandLineOptions.Mode.PARSE) {
                // Входные события читаем только через System.in.
                ParsedResult parsed = eventProcessor.read(System.in);
                report = analyzer.analyze(parsed);
                jsonWriter.write(options.getJsonPath(), parsed);
                reportSerializer.save(options.getReportPath(), report);
            } else if (options.getJsonPath() != null) {
                // Восстанавливаем события из JSON, не читая System.in.
                ParsedResult parsed = jsonReader.read(options.getJsonPath());
                report = analyzer.analyze(parsed);
            } else {
                report = reportSerializer.load(options.getReportPath());
            }
            String reportText = reportBuilder.build(report);
            System.out.print(reportText);
            return 0;
        } catch (IOException exception) {
            System.err.println("Ошибка ввода/вывода: " + exception.getMessage());
            return 1;
        }
    }
}
