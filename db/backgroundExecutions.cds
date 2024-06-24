using { cuid, managed } from '@sap/cds/common';

entity JobsExecuted: cuid, managed {
  executedBy : String;
  status : JobStatus;
  jobName : String;
  additionalInfo : String;
}

type JobStatus : String enum {
  Success;
  Failed;
}