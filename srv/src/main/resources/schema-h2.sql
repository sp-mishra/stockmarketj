
DROP VIEW IF EXISTS GenericService_MarketHolidays;
DROP TABLE IF EXISTS Holidays;

CREATE TABLE Holidays (
  ID NVARCHAR(36) NOT NULL,
  product NVARCHAR(255),
  tradingDate DATE,
  weekDay NVARCHAR(255),
  description NVARCHAR(255),
  serialNumber INTEGER,
  PRIMARY KEY(ID)
); 

CREATE VIEW GenericService_MarketHolidays AS SELECT
  Holidays_0.ID,
  Holidays_0.product,
  Holidays_0.tradingDate,
  Holidays_0.weekDay,
  Holidays_0.description,
  Holidays_0.serialNumber
FROM Holidays AS Holidays_0; 

