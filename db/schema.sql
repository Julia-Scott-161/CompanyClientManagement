-- Safari Company Client Management: schema
-- Column names match the Java classes (Client, Contact, Touchpoint, Pipeline) and the SQL in
-- Home_Menu, ViewEditClientUI and the stored-procedure messages (INSERT:CLIENT:..., etc.).
-- Works on MySQL 8+ and MariaDB 10.5+. Safe to re-run: it drops and recreates the tables.

DROP TABLE IF EXISTS TOUCHPOINT;
DROP TABLE IF EXISTS CONTACT;
DROP TABLE IF EXISTS PIPELINE;
DROP TABLE IF EXISTS CLIENT;

-- Add_Client only supplies the NOT NULL columns (Client_ID, Date_Added, Legal_Name, Source, Note);
-- everything else is optional or has a default.
CREATE TABLE CLIENT (
    Client_ID    INT          NOT NULL,
    Legal_Name   VARCHAR(100) NOT NULL,
    Mailing_Addr VARCHAR(200),
    Rec_PassPrt  BOOLEAN      NOT NULL DEFAULT FALSE,
    Birth_Date   DATE,
    Date_Added   DATE         NOT NULL,
    Company      VARCHAR(100),
    Note         VARCHAR(500),
    Source       VARCHAR(30)  NOT NULL,
    PRIMARY KEY (Client_ID),
    CONSTRAINT chk_source CHECK (Source IN
        ('Direct-Web','Direct-WalkIn','Travel-Agent','Social-Media','Referral','Trade-Show'))
);

-- Pipeline stage for a client. PipeLoc values are the stages from the original pipeline screen.
CREATE TABLE PIPELINE (
    PLStg_ID   INT          NOT NULL,
    Client_ID  INT          NOT NULL,
    PipeLoc    VARCHAR(20)  NOT NULL DEFAULT 'Lead',
    LostCommt  VARCHAR(500),
    DaysInPipe INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (PLStg_ID),
    CONSTRAINT fk_pipeline_client FOREIGN KEY (Client_ID)
        REFERENCES CLIENT (Client_ID) ON DELETE CASCADE,
    CONSTRAINT chk_pipeloc CHECK (PipeLoc IN
        ('Proposal','Consultation','Lost','Lead','Booked','Post-Trip'))
);

CREATE TABLE CONTACT (
    Contact_ID   INT          NOT NULL,
    Client_ID    INT          NOT NULL,
    Cnt_Type     VARCHAR(20)  NOT NULL,   -- Email, Phone, LinkedIn, Other
    Pref_Cont    BOOLEAN      NOT NULL DEFAULT FALSE,
    Contact_Info VARCHAR(500) NOT NULL,
    PRIMARY KEY (Contact_ID),
    CONSTRAINT fk_contact_client FOREIGN KEY (Client_ID)
        REFERENCES CLIENT (Client_ID) ON DELETE CASCADE
);

CREATE TABLE TOUCHPOINT (
    TP_ID      INT          NOT NULL,
    Client_ID  INT          NOT NULL,
    TPdateocc  DATE         NOT NULL,
    TP_Type    VARCHAR(30)  NOT NULL,     -- Call, Email, Meeting, ...
    EngageScr  INT,                       -- engagement score
    TP_Note    VARCHAR(500),
    PRIMARY KEY (TP_ID),
    CONSTRAINT fk_touch_client FOREIGN KEY (Client_ID)
        REFERENCES CLIENT (Client_ID) ON DELETE CASCADE
);
