# Safari Company Client Management

A Java Swing desktop app for a travel company to manage clients, their contact info,
touchpoints and sales-pipeline stage, backed by a MySQL database.

## Features
- Client list, loaded from the database
- Add client (required fields only), view/edit client details with per-field Edit/Save
- Add contact info (type, preferred flag, details) for a client
- Delete client (cascades to pipeline, contacts and touchpoints)
- Pipeline editing, touchpoint add/edit and the Trips screen are planned (placeholder screen for now)

## How it works (talking points)
- **Schema** (`db/schema.sql`): `CLIENT` is the parent of `PIPELINE`, `CONTACT` and `TOUCHPOINT`
  (foreign keys with `ON DELETE CASCADE`). `CHECK` constraints limit `Source` and pipeline stage.
- **Stored procedure** `test_execute_command` runs `INSERT / UPDATE / SELECT / DELETE` from a
  colon-delimited command such as `UPDATE:CLIENT:Client_ID:1:Source:Referral`. Table and column names
  are validated with a regex and values are bound as parameters. The app creates the procedure on
  connect and drops it on exit, so the database only ever holds the real data tables.
- **Model classes** (`Client`, `Contact`, `Touchpoint`, `Pipeline`) hold a row each, so a screen
  needs one query and then uses getters.
- **`EditField`** is a reusable panel (text, checkbox or dropdown) with Edit / Save buttons that
  builds the update command for one column of one client.

## Run it
1. Java 17+ and Maven, plus a MySQL 8 (or MariaDB) server listening on **port 3336**.
2. As an admin, create the database and user, then load the tables and demo data:
   ```
   mysql -u root -p --port=3336 -h 127.0.0.1 < db/create_database.sql
   mysql -u safari -psafari --port=3336 -h 127.0.0.1 safari < db/schema.sql
   mysql -u safari -psafari --port=3336 -h 127.0.0.1 safari < db/seed.sql
   ```
   (In the `mysql>` prompt: `source path/to/schema.sql` also works.)
3. Start the app: `mvn compile exec:java`, or run `RunClient_and_Methods` in your IDE.
4. On the login screen enter database `safari` and password `safari` (a local demo account).

The demo data is entirely made up. Put `SF_Logo.png` in the project root for the window icon.
