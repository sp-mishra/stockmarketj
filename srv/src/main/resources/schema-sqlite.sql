
DROP VIEW IF EXISTS GenericService_JobsExecuted;
DROP VIEW IF EXISTS GenericService_MarketHolidays;
DROP TABLE IF EXISTS EquityList;
DROP TABLE IF EXISTS Product;
DROP TABLE IF EXISTS JobsExecuted;
DROP TABLE IF EXISTS Holidays;

CREATE TABLE Holidays (
  ID NVARCHAR(36) NOT NULL,
  product_shortForm NVARCHAR(255),
  tradingDate DATE_TEXT,
  weekDay NVARCHAR(255),
  description NVARCHAR(255) DEFAULT 'Trade Closed for Holiday',
  serialNumber INTEGER,
  PRIMARY KEY(ID)
); 

CREATE TABLE JobsExecuted (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP_TEXT,
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP_TEXT,
  modifiedBy NVARCHAR(255),
  executedBy NVARCHAR(255) NOT NULL,
  status NVARCHAR(255) NOT NULL,
  jobName NVARCHAR(255),
  additionalInfo NVARCHAR(255) NULL,
  PRIMARY KEY(ID)
); 

CREATE TABLE Product (
  shortForm NVARCHAR(255) NOT NULL,
  longForm NVARCHAR(255),
  PRIMARY KEY(shortForm)
); 

CREATE TABLE EquityList (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP_TEXT,
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP_TEXT,
  modifiedBy NVARCHAR(255),
  symbol NVARCHAR(255),
  companyName NVARCHAR(255),
  series NVARCHAR(255),
  dateOfListing DATE_TEXT,
  paidUpValue DOUBLE,
  marketLot DOUBLE,
  isinNumber NVARCHAR(255),
  faceValue DOUBLE,
  PRIMARY KEY(ID)
); 

CREATE VIEW GenericService_MarketHolidays AS SELECT
  Holidays_0.ID,
  Holidays_0.product_shortForm,
  Holidays_0.tradingDate,
  Holidays_0.weekDay,
  Holidays_0.description,
  Holidays_0.serialNumber
FROM Holidays AS Holidays_0; 

CREATE VIEW GenericService_JobsExecuted AS SELECT
  JE_0.ID,
  JE_0.createdAt,
  JE_0.modifiedAt,
  JE_0.executedBy,
  JE_0.status,
  JE_0.jobName,
  JE_0.additionalInfo
FROM JobsExecuted AS JE_0; 

