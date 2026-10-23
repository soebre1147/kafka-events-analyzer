# Практические задания 2 и 3: Kafka-like события

Программа принимает строки `topic partition offset eventType` через `System.in`.
Допустимые типы событий: `ORDER_CREATED`, `ORDER_PAID`, `ORDER_CANCELLED`,
`PAYMENT_STARTED`, `PAYMENT_FAILED`, `USER_REGISTERED`. Номер partition и offset
должны быть неотрицательными целыми числами.

## Что делает программа

В режиме `--parse` программа читает вход до конца потока, сохраняет корректные
события в `List<KafkaEvent>`, а проблемные строки с номером, текстом и причиной
ошибки — в `List<InvalidLine>`. Отдельный analyzer запускает стратегии для
подсчёта событий по topic, partition и eventType, максимальных offset по topic
и partition, а также количества платёжных событий. Платёжными считаются
`PAYMENT_STARTED` и `PAYMENT_FAILED`; `ORDER_PAID` остаётся событием заказа.
Группировка по partition использует числовой номер, как в первом задании.

Отчёт собирается через `StringBuilder`, выводится в консоль и сохраняется как
объект `KafkaAnalysisReport` через Java Serialization. Структурированный
результат парсинга отдельно записывается в UTF-8 JSON. В режиме
`--show-report` программа читает либо JSON (`--json`), либо бинарный файл
(`--report`), как уточнил преподаватель. В обоих случаях `System.in` не читается.
Из JSON восстанавливаются события и ошибки, по которым строится отчёт.
Из `.ser` загружается уже рассчитанный объект отчёта.

## Как разделён код

- `org.example.Main` — короткая точка входа.
- `cli/CommandLineParser` — проверка режимов и путей.
- `parser/EventParser` — разбор одной строки; `parser/EventProcessor` — чтение потока.
- `model/KafkaEvent`, `EventType`, `InvalidLine`, `ParsedResult` — модели данных.
- `analysis/KafkaAnalyzer` — запуск шести реализаций `AnalysisStrategy`.
- `analysis/AnalysisAccumulator` — карты со счётчиками и максимальными offset.
- `analysis/KafkaAnalysisReport` — неизменяемый объект итогового отчёта.
- `report/KafkaReportBuilder` — форматирование консольного отчёта.
- `storage/JsonResultWriter`, `JsonResultReader`, `JavaReportSerializer` —
  сохранение и восстановление.
- `facade/KafkaAnalysisFacade` — объединяет все этапы работы.

В подсчётах используются обычные циклы и условия. Для увеличения счётчика
программа берёт старое значение из `Map` и записывает новое через `put`.
Для максимального offset программа сравнивает новое значение с сохранённым.
Facade и Strategy оставлены, потому что они требуются во втором задании.
Пояснение классов и основных конструкций Java есть в [CODE_EXPLANATION.md](CODE_EXPLANATION.md).

## Запуск

Требуются Java 17+ и Maven. Для сборки выполните `mvn package` в корне проекта
или запустите задачу **Maven → Lifecycle → package** в IntelliJ IDEA. После
сборки из PowerShell в корне проекта:

```powershell
Get-Content -Encoding UTF8 sample-input.txt |
  java '-Dfile.encoding=UTF-8' -jar target/kafka-events-analyzer-1.0-SNAPSHOT.jar --parse --json parsed-result.json --report report.ser

java '-Dfile.encoding=UTF-8' -jar target/kafka-events-analyzer-1.0-SNAPSHOT.jar --show-report --json parsed-result.json
java '-Dfile.encoding=UTF-8' -jar target/kafka-events-analyzer-1.0-SNAPSHOT.jar --show-report --report report.ser
java '-Dfile.encoding=UTF-8' -jar target/kafka-events-analyzer-1.0-SNAPSHOT.jar --help
```

Для чтения JSON используется Jackson. Maven включает эту библиотеку в JAR,
поэтому готовый JAR запускается без отдельного указания зависимостей.
В IntelliJ после изменения `pom.xml` нужно обновить Maven, если IDE ещё
не загрузила зависимости автоматически.

В IntelliJ можно создать конфигурацию **Application** с классом
`org.example.Main`. Для анализа добавьте в поле **Program arguments**:
`--parse --json parsed-result.json --report report.ser` и укажите
`sample-input.txt` в поле **Redirect input from**. Для просмотра используйте
`--show-report --json parsed-result.json` или
`--show-report --report report.ser`. Выберите один источник отчёта.
Запуск без режима выводит ошибку и подсказку, как требуется в задании 3.

Пример содержит 8 строк: 6 корректных и 2 ошибочные. JSON должен содержать
`totalLines: 8`, `validLines: 6`, `invalidLines: 2`; отчёт после восстановления
из JSON и `.ser` должен совпадать с исходным выводом. В режиме просмотра
нужно выбрать один источник: `--json` или `--report`. Пустые строки тоже считаются
ошибочными. Папка для выходных файлов должна существовать заранее.
