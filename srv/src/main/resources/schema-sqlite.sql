
DROP VIEW IF EXISTS localized_sap_capire_incidents_Incidents_conversation;
DROP VIEW IF EXISTS localized_sap_capire_incidents_Customers;
DROP VIEW IF EXISTS localized_sap_capire_incidents_Incidents;
DROP VIEW IF EXISTS localized_sap_capire_incidents_Urgency;
DROP VIEW IF EXISTS localized_sap_capire_incidents_Status;
DROP TABLE IF EXISTS sap_capire_incidents_Urgency_texts;
DROP TABLE IF EXISTS sap_capire_incidents_Status_texts;
DROP TABLE IF EXISTS sap_capire_incidents_Incidents_conversation;
DROP TABLE IF EXISTS sap_capire_incidents_Urgency;
DROP TABLE IF EXISTS sap_capire_incidents_Status;
DROP TABLE IF EXISTS sap_capire_incidents_Customers;
DROP TABLE IF EXISTS sap_capire_incidents_Incidents;

CREATE TABLE sap_capire_incidents_Incidents (
  ID NVARCHAR(36) NOT NULL,
  createdAt TIMESTAMP_TEXT,
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP_TEXT,
  modifiedBy NVARCHAR(255),
  customer_ID NVARCHAR(255),
  title NVARCHAR(255),
  urgency_code NVARCHAR(255) DEFAULT 'M',
  status_code NVARCHAR(255) DEFAULT 'N',
  PRIMARY KEY(ID)
); 

CREATE TABLE sap_capire_incidents_Customers (
  createdAt TIMESTAMP_TEXT,
  createdBy NVARCHAR(255),
  modifiedAt TIMESTAMP_TEXT,
  modifiedBy NVARCHAR(255),
  ID NVARCHAR(255) NOT NULL,
  firstName NVARCHAR(255),
  lastName NVARCHAR(255),
  email NVARCHAR(255),
  phone NVARCHAR(255),
  PRIMARY KEY(ID)
); 

CREATE TABLE sap_capire_incidents_Status (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(255) NOT NULL,
  criticality INTEGER,
  PRIMARY KEY(code)
); 

CREATE TABLE sap_capire_incidents_Urgency (
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(255) NOT NULL,
  PRIMARY KEY(code)
); 

CREATE TABLE sap_capire_incidents_Incidents_conversation (
  up__ID NVARCHAR(36) NOT NULL,
  ID NVARCHAR(36) NOT NULL,
  timestamp TIMESTAMP_TEXT,
  author NVARCHAR(255),
  message NVARCHAR(255),
  PRIMARY KEY(up__ID, ID)
); 

CREATE TABLE sap_capire_incidents_Status_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(255) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE TABLE sap_capire_incidents_Urgency_texts (
  locale NVARCHAR(14) NOT NULL,
  name NVARCHAR(255),
  descr NVARCHAR(1000),
  code NVARCHAR(255) NOT NULL,
  PRIMARY KEY(locale, code)
); 

CREATE VIEW localized_sap_capire_incidents_Status AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code,
  L_0.criticality
FROM (sap_capire_incidents_Status AS L_0 LEFT JOIN sap_capire_incidents_Status_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = session_context( '$user.locale' )); 

CREATE VIEW localized_sap_capire_incidents_Urgency AS SELECT
  coalesce(localized_1.name, L_0.name) AS name,
  coalesce(localized_1.descr, L_0.descr) AS descr,
  L_0.code
FROM (sap_capire_incidents_Urgency AS L_0 LEFT JOIN sap_capire_incidents_Urgency_texts AS localized_1 ON localized_1.code = L_0.code AND localized_1.locale = session_context( '$user.locale' )); 

CREATE VIEW localized_sap_capire_incidents_Incidents AS SELECT
  L.ID,
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.customer_ID,
  L.title,
  L.urgency_code,
  L.status_code
FROM sap_capire_incidents_Incidents AS L; 

CREATE VIEW localized_sap_capire_incidents_Customers AS SELECT
  L.createdAt,
  L.createdBy,
  L.modifiedAt,
  L.modifiedBy,
  L.ID,
  L.firstName,
  L.lastName,
  L.email,
  L.phone
FROM sap_capire_incidents_Customers AS L; 

CREATE VIEW localized_sap_capire_incidents_Incidents_conversation AS SELECT
  L.up__ID,
  L.ID,
  L.timestamp,
  L.author,
  L.message
FROM sap_capire_incidents_Incidents_conversation AS L; 

