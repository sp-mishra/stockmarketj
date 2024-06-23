using { cuid } from '@sap/cds/common';

type DayOfWeek : String enum {
  MONDAY;
  TUESDAY;
  WEDNESDAY;
  THURSDAY;
  FRIDAY;
  SATURDAY;
  SUNDAY;
}

entity Holidays: cuid {
product: String;
tradingDate: Date;
weekDay: DayOfWeek;
description: String;
serialNumber: Integer;
}