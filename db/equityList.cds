using { cuid, managed } from '@sap/cds/common';

entity EquityList: cuid, managed {
    symbol: String;
    companyName: String;
    series: String;
    dateOfListing: Date;
    paidUpValue: Double;
    marketLot: Double;
    isinNumber: String;
    faceValue: Double;
}