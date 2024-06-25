package org.grklab.mappers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import cds.gen.Holidays;

import static java.util.UUID.randomUUID;

public class HolidaysDeserializer extends JsonDeserializer<cds.gen.Holidays> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy", Locale.ENGLISH);

    @Override
    public Holidays deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        final Holidays holidays = Holidays.create();
        final JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        holidays.setId(randomUUID().toString());

        if (node.has("tradingDate")) {
            // Replace with the format of your date string
            final String tradingDateStr = node.get("tradingDate").asText();
            final LocalDate tradingDate = LocalDate.parse(tradingDateStr, formatter);
            holidays.setTradingDate(tradingDate);
        }

        if (node.has("weekDay")) {
            holidays.setWeekDay(node.get("weekDay").asText());
        }
        if (node.has("description")) {
            holidays.setDescription(node.get("description").asText());
        }
        if (node.has("Sr_no")) {
            holidays.setSerialNumber(node.get("Sr_no").asInt());
        }
        return holidays;
    }
}