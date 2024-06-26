package org.grklab.components;

import cds.gen.JobStatus;
import cds.gen.Product;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sap.cds.Result;
import com.sap.cds.Row;
import com.sap.cds.ql.Delete;
import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Select;
import com.sap.cds.ql.cqn.CqnDelete;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnSelect;
import com.sap.cds.services.persistence.PersistenceService;
import org.grklab.mappers.CSVStringToEquityList;
import org.grklab.mappers.HolidaysDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.reactive.function.client.WebClient;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;
import org.grklab.handlers.JobsExecutedService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import cds.gen.Holidays;
import cds.gen.Holidays_;
import cds.gen.Product_;
import cds.gen.EquityList_;
import cds.gen.EquityList;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
@Slf4j
public class GenericInfoCollector {
    private static final String HOLIDAYS_API = "https://www.nseindia.com/api/holiday-master?type=trading";
    private final JobsExecutedService jobsExecutedService;
    private final PersistenceService db;
    private boolean nseHolidaysFetched;
    private boolean equityListFetched;

    @Value("${grklab.nse-scheduler-holiday-enable}")
    private boolean nseSchedulerHolidayEnable;

    @Value("${grklab.nse-scheduler-equitylist-enable}")
    private boolean nseSchedulerEquityListEnable;

    public GenericInfoCollector(JobsExecutedService jobsExecutedService,
                                PersistenceService db) {
        this.jobsExecutedService = jobsExecutedService;
        this.db = db;
        this.nseHolidaysFetched = false;
        this.equityListFetched = false;
    }

    @Scheduled(cron = "${grklab.nse-scheduler-holiday}")
    public void fetchHolidays() {
        if(!nseSchedulerHolidayEnable) {
            log.info("NSE holiday scheduler is disabled");
            return;
        }

        if (nseHolidaysFetched) {
            log.info("NSE holidays already fetched");
            return;
        }

        log.info("Fetching holidays from NSE - Start");
        // Fetch all shortForm values from the Product table
        final CqnSelect select = Select.from(Product_.class).columns(Product_::shortForm);
        final List<Row> result = db.run(select).list();
        final List<String> nodes = result.stream()
                .map(row -> (String) row.get("shortForm")).toList();
        log.info("Short forms fetched from Product table: {}", nodes);
        // Check if the Holidays table is empty
        final CqnSelect count = Select.from(Holidays_.class).inlineCount();
        final long countResult = db.run(count).rowCount();

        // If the table is not empty, delete all records
        if (countResult > 0) {
            final CqnDelete delete = Delete.from(Holidays_.class);
            final Result deleteResult = db.run(delete);
            log.info("Deleted all {} records from Holidays table", deleteResult.rowCount());
        }
        final String errorMessage = "Error fetching holidays from NSE";
        final String functionName = "fetchHolidays";
        WebClient.create()
                .get()
                .uri(HOLIDAYS_API)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    try {
                        final ObjectMapper objectMapper = new ObjectMapper();
                        final SimpleModule module = new SimpleModule();
                        module.addDeserializer(Holidays.class, new HolidaysDeserializer());
                        objectMapper.registerModule(module);

                        final JsonNode jsonNode = objectMapper.readTree(responseBody);

                        for (final String node : nodes) {
                            if (jsonNode.has(node)) {
                                final JsonNode nodeData = jsonNode.get(node);

                                // Parse the nodeData array into a list of cds.gen.Holidays objects
                                final List<Holidays> holidays = objectMapper.readValue(nodeData.toString(), new TypeReference<>() {});

                                // Process each holiday
                                for (final Holidays holiday : holidays) {
                                    final Map<String, Object> productMap = Map.of(Product.SHORT_FORM, node);
                                    holiday.setProduct(productMap);
                                    // Create a CqnInsert object for each holiday and execute it
                                    final CqnInsert insert = Insert.into(Holidays_.class).entry(holiday);
                                    db.run(insert);
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.error(errorMessage, e);
                        jobsExecutedService.updateJobExecutionStatus(GenericInfoCollector.class, functionName,
                                JobStatus.FAILED, errorMessage);
                    }
                    return Mono.empty();
                })
                .doOnNext(response -> jobsExecutedService.updateJobExecutionStatus(GenericInfoCollector.class, functionName,
                        JobStatus.SUCCESS, "Updated the holiday list"))
                .doOnError(e -> {
                    log.error(errorMessage, e);
                    jobsExecutedService.updateJobExecutionStatus(GenericInfoCollector.class, functionName,
                            JobStatus.FAILED, errorMessage);
                })
                .subscribe();
        nseHolidaysFetched = true;
        log.info("Fetching holidays from NSE - Finished");
    }

    @Scheduled(cron = "${grklab.nse-scheduler-equitylist}")
    public void fetchEquityList() {
        if(!nseSchedulerEquityListEnable) {
            log.info("NSE equity list scheduler is disabled");
            return;
        }

        if (equityListFetched) {
            log.info("Equity list already fetched");
            return;
        }
        // Check if the table is empty
        final CqnSelect count = Select.from(EquityList_.class).inlineCount();
        final long countResult = db.run(count).rowCount();

        // If the table is not empty, delete all records
        if (countResult > 0) {
            final CqnDelete delete = Delete.from(EquityList_.class);
            final Result deleteResult = db.run(delete);
            log.info("Deleted all {} records from Equity table", deleteResult.rowCount());
        }

        final String equityListUrl = "https://archives.nseindia.com/content/equities/EQUITY_L.csv";
        log.info("Fetching equity list - Start");
        final String functionName = "fetchEquityList";
        WebClient.create()
                .get()
                .uri(equityListUrl)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(responseBody -> {
                    try {
                        final CSVStringToEquityList csvStringToEquityList = new CSVStringToEquityList(responseBody);
                        final List<EquityList> equityLists = csvStringToEquityList.convert();
                        equityLists.forEach(equityList -> {
                            final CqnInsert insert = Insert.into(EquityList_.class).entry(equityList);
                            db.run(insert);
                        });
                    } catch (Exception e) {
                        log.error("Error fetching and storing equity list", e);
                    }
                    return Mono.empty();
                })
                .doOnNext(response -> {
                    jobsExecutedService.updateJobExecutionStatus(GenericInfoCollector.class, functionName,
                        JobStatus.SUCCESS, "Updated the equity list");
                    equityListFetched = true;
                })
                .doOnError(e -> {
                    log.error("Error fetching holidays from NSE", e);
                    jobsExecutedService.updateJobExecutionStatus(GenericInfoCollector.class, functionName,
                            JobStatus.FAILED, "Equity list update failed");
                })
                .subscribe();
        equityListFetched = true;
        log.info("Fetching equity list - End");
    }
}
