package org.grklab.handlers;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.Upsert;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.ql.cqn.CqnUpsert;
import com.sap.cds.services.cds.CdsCreateEventContext;
import com.sap.cds.services.cds.CdsUpsertEventContext;
import com.sap.cds.services.cds.CqnService;
import com.sap.cds.services.handler.EventHandler;
import com.sap.cds.services.handler.annotations.On;
import com.sap.cds.services.handler.annotations.ServiceName;
import com.sap.cds.services.persistence.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import cds.gen.genericservice.GenericService_;
import cds.gen.Holidays_;

@Component
@ServiceName(GenericService_.CDS_NAME)
@Slf4j
public class HolidaysService implements EventHandler {
    private final PersistenceService db;
    public HolidaysService(PersistenceService db) {
        this.db = db;
    }

    @On(event = CqnService.EVENT_CREATE, entity = Holidays_.CDS_NAME)
    public void onCreate(CdsCreateEventContext context) {
        final var entries = context.getCqn().entries();
        log.info("{} onCreate. {}", Holidays_.CDS_NAME, entries);

        // Create a CqnInsert object for each entry and execute it
        entries.forEach(e -> {
            final CqnInsert insert = Insert.into(Holidays_.CDS_NAME).entry(e);
            db.run(insert);
        });

        context.setResult(entries);
    }

    @On(event = CqnService.EVENT_UPSERT, entity = Holidays_.CDS_NAME)
    public void upInsert(CdsUpsertEventContext context) {
        log.info("Upserting {}", Holidays_.CDS_NAME);

        // Create a CqnUpsert object for each entry and execute it
        context.getCqn().entries().forEach(e -> {
            final CqnUpsert upsert = Upsert.into(Holidays_.CDS_NAME).entry(e);
            db.run(upsert);
        });

        context.setResult(context.getCqn().entries());
    }
}
