using { cuid, managed } from '@sap/cds/common';

entity JobsExecuted: cuid, managed {
  executedBy : String not null;
  status : JobStatus not null;
  jobName : String;
  additionalInfo : String null;
}

type JobStatus : String enum {
  Success;
  Failed;
}