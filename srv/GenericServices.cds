using {Holidays, JobsExecuted as JE} from '../db';

service GenericService @(path:'/generic') {
entity MarketHolidays as projection on Holidays excluding { createdBy, modifiedBy };
@readonly entity JobsExecuted as projection on JE excluding { createdBy, modifiedBy };
}