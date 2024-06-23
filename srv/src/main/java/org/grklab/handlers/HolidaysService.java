package org.grklab.handlers;

import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsReadEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@ServiceName("GenericService")
public class HolidaysService implements EventHandler {
    private Map<Object, Map<String, Object>> products = new HashMap<>();

    @On(event = CqnService.EVENT_CREATE, entity = "GenericService.MarketHolidays")
    public void onCreate(CdsCreateEventContext context) {
        context.getCqn().entries().forEach(e -> products.put(e.get("ID"), e));
        context.setResult(context.getCqn().entries());
    }

    @On(event = CqnService.EVENT_READ, entity = "org.grklab.domains.GenericService.Holidays")
    public void onRead(CdsReadEventContext context) {
        context.setResult(products.values());
    }

    @On(event = CqnService.EVENT_UPSERT, entity = "GenericService.MarketHolidays")
    public void onInsert(CdsUpsertEventContext context) {
        context.getCqn().entries().forEach(e -> products.put(e.get("ID"), e));
        context.setResult(context.getCqn().entries());
    }
}
