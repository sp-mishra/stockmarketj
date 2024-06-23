using Holidays from '../db/holiday';

service GenericService @(path:'/generic') {
entity MarketHolidays as projection on Holidays excluding { createdBy, modifiedBy };
}