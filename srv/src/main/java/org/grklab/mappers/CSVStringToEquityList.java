package org.grklab.mappers;

import jakarta.annotation.Nonnull;
import cds.gen.EquityList;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.Trim;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Getter
public class CSVStringToEquityList {
    private static final CellProcessor[] EQUITY_PROCESSORS = new CellProcessor[] {
            new Trim(new NotNull()),
            new Trim(new NotNull()),
            new Trim(new NotNull()),
            new Trim(new NotNull()),
            new ParseDouble(),
            new ParseDouble(),
            new NotNull(),
            new ParseDouble(),
    };
    private static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("dd-MMM-yyyy")
            .toFormatter(Locale.ENGLISH);

    private final String csvString;
    private final List<List<Object>> errorList;
    public CSVStringToEquityList(@Nonnull String csvString) {
        this.csvString = csvString;
        this.errorList = new ArrayList<>();
    }

    public List<EquityList> convert() {
        final List<EquityList> equityLists = new ArrayList<>();
        ICsvListReader listReader;
        int count = 0;
        try {
            listReader = new CsvListReader(new StringReader(csvString), CsvPreference.STANDARD_PREFERENCE);
            listReader.getHeader(true);
            List<Object> customerList;

            while( (customerList = listReader.read(EQUITY_PROCESSORS)) != null ) {
                final EquityList equityList = createSingleEquityList(customerList);
                if(equityList != null) {
                    equityLists.add(equityList);
                    count++;
                }
            }
        } catch (Exception e) {
            log.error("Error fetching and storing equity list", e);
        }
        log.info("Converted {} and error {}, total: {}", equityLists.size(), errorList.size(), count);
        return equityLists;
    }

    private EquityList createSingleEquityList(@Nonnull List<Object> customerList) {
        EquityList equityList = null;
        try {
            equityList = EquityList.create();
            equityList.setSymbol((String)customerList.get(0));
            equityList.setCompanyName((String)customerList.get(1));
            equityList.setSeries((String)customerList.get(2));
            final String listingDateStr = ((String)customerList.get(3)).strip();
            final LocalDate listingDate = LocalDate.parse(listingDateStr, formatter);
            equityList.setDateOfListing(listingDate);
            equityList.setPaidUpValue((Double)customerList.get(4));
            equityList.setMarketLot((Double)customerList.get(5));
            equityList.setIsinNumber((String)customerList.get(6));
            equityList.setFaceValue((Double) customerList.get(7));
        } catch (Exception e) {
            log.error("Error creating equity list", e);
            errorList.add(customerList);
        }
        return equityList;
    }
}
