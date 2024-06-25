package org.grklab.handlers;

import com.sap.cds.ql.Insert;
import com.sap.cds.ql.cqn.CqnInsert;
import com.sap.cds.services.persistence.PersistenceService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import cds.gen.JobsExecuted_;
import cds.gen.JobsExecuted;

import java.time.Instant;

@Component
@Slf4j
public class JobsExecutedService {
    private final PersistenceService db;
    public JobsExecutedService(PersistenceService db) {
        this.db = db;
    }

    public void updateJobExecutionStatus(@Nonnull Class<?> executedByClazz, @Nonnull String jobName,
                                         @Nonnull String status, String additionalInfo) {
        // Create a CqnUpdate object to update the JobsExecuted entity
        final JobsExecuted jobsExecuted = JobsExecuted.create();
        jobsExecuted.setExecutedBy(executedByClazz.getName());
        jobsExecuted.setStatus(status);
        jobsExecuted.setJobName(jobName);
        jobsExecuted.setAdditionalInfo(additionalInfo);
        jobsExecuted.setCreatedAt(Instant.now());
        final CqnInsert insert = Insert.into(JobsExecuted_.CDS_NAME).entry(jobsExecuted);
        db.run(insert);
    }
}
