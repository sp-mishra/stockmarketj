package org.grklab.handlers;

import com.sap.cds.ql.Update;
import com.sap.cds.ql.cqn.CqnUpdate;
import com.sap.cds.services.persistence.PersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JobService {
    private final PersistenceService db;
    public JobService(PersistenceService db) {
        this.db = db;
    }

    @Scheduled(cron = "0 0 * * * ?")
    public void updateJobsExecutedEveryHour() {
        // Create a CqnUpdate object to update the JobsExecuted entity
//        final CqnUpdate update = Update.entity("backgroundExecutions.JobsExecuted")
//                .data("status", "Success") // Update the status to Success
//                .where("status", "Failed"); // Only update the jobs that have status Failed

        // Execute the update
//        db.run(update);
    }
}
