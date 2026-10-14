-- Demo data (entirely made up). Run after schema.sql. Safe to re-run after schema.sql.
-- Two clients share the source 'Referral' on purpose, to show edits only change one client.

INSERT INTO CLIENT (Client_ID, Legal_Name, Mailing_Addr, Rec_PassPrt, Birth_Date, Date_Added, Company, Note, Source) VALUES
 (1, 'Amara Okafor',   '12 Sample Street, Anytown, VA', TRUE,  '1984-03-12', '2026-08-01', 'Okafor Family Travel', 'Wants a 10-day Serengeti trip', 'Referral'),
 (2, 'Liam Brennan',   NULL,                            TRUE,  '1990-07-25', '2026-08-15', 'Brennan & Co',         'Honeymoon, flexible dates',     'Direct-Web'),
 (3, 'Sofia Marquez',  NULL,                            FALSE, '1995-11-02', '2026-09-03', NULL,                   'Needs passport renewal',        'Social-Media'),
 (4, 'Hiroshi Tanaka', '48 Example Road, Sampleville',  TRUE,  '1978-01-30', '2026-09-20', 'Tanaka Photography',   'Photo-safari focus',            'Trade-Show'),
 (5, 'Priya Nair',     NULL,                            TRUE,  NULL,         '2026-09-28', NULL,                   'Family of five',                'Referral');

INSERT INTO PIPELINE (PLStg_ID, Client_ID, PipeLoc, LostCommt, DaysInPipe) VALUES
 (1, 1, 'Proposal',     '',                32),
 (2, 2, 'Consultation', '',                18),
 (3, 3, 'Lead',         '',                29),
 (4, 4, 'Lost',         'Budget too high', 12),
 (5, 5, 'Lead',         '',                 5);

INSERT INTO CONTACT (Contact_ID, Client_ID, Cnt_Type, Pref_Cont, Contact_Info) VALUES
 (1, 1, 'Email', TRUE,  'amara@example.com'),
 (2, 1, 'Phone', FALSE, '555-0101'),
 (3, 2, 'Email', TRUE,  'liam@example.com'),
 (4, 4, 'Phone', TRUE,  '555-0144');

INSERT INTO TOUCHPOINT (TP_ID, Client_ID, TPdateocc, TP_Type, EngageScr, TP_Note) VALUES
 (1, 1, '2026-08-05', 'Call',    4, 'Intro call, discussed itinerary options'),
 (2, 1, '2026-08-20', 'Email',   3, 'Sent proposal draft'),
 (3, 2, '2026-08-18', 'Meeting', 5, 'Consultation about honeymoon packages'),
 (4, 4, '2026-09-25', 'Call',    2, 'Client declined due to budget');
