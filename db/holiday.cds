using { cuid } from '@sap/cds/common';
using Product from './Product';

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
product: Association to Product;
tradingDate: Date;
weekDay: DayOfWeek;
description: String default 'Trade Closed for Holiday';
serialNumber: Integer;
}