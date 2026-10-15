# Demo Company Client Management

![Demo logo](SF_Logo.png)

A Java desktop application for managing a company's client records, backed by a MySQL database. Built as a portfolio project to practice GUI development, database design, and connecting an application to a relational database through stored procedures.

## Features

- **Home menu** as the entry point to the application
- **Add Client** screen that collects the required (NOT NULL) client information:
  - Legal name
  - Note
  - Source (Direct-Web, Direct-WalkIn, Travel-Agent, Social-Media, Referral, Trade-Show)
- Automatically generated unique client ID (one more than the current maximum)
- Automatic "date added" stamp using the current date
- User input cleaned before being sent to the database
- Confirmation and error dialogs so the user always knows whether an action succeeded

## Tech Stack

| Area | Technology |
|---|---|
| Language | Java 17+ |
| GUI | Java Swing (`JFrame`, `GridLayout`, `FlowLayout`) |
| Database | MySQL |
| Database driver | MySQL Connector/J 8.4.0 |
| Build tool | Maven |

## How It Works

The Swing screens never write SQL directly. Each screen builds a colon-delimited command string and hands it to a single stored procedure, `test_execute_command`, which parses the command and performs the operation. For example, adding a client sends:

```
INSERT:CLIENT:Client_ID:<id>:Date_Added:<date>:Legal_Name:<name>:Source:<source>:Note:<note>
```

```
Swing screen  ->  helper methods  ->  stored procedure  ->  MySQL
(AddClient)       (execute query,      (parses command
                   clean input)         and runs it)
```

## Getting Started

### Prerequisites

- JDK 17 or newer
- Maven
- A running MySQL server

### Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/Julia-Scott-161/CompanyClientManagement.git
   cd CompanyClientManagement
   ```
2. Create the database by running the SQL scripts in the `db/` folder against your MySQL server.
3. Update the connection settings (URL, username, password) in the database connection class to match your MySQL setup.
4. Build and run:
   ```bash
   mvn compile exec:java
   ```

You can also open the project in IntelliJ IDEA as a Maven project and run the main class directly.

## Project Structure

```
CompanyClientManagement/
├── src/main/java/            Swing screens and database helper classes
│   ├── RunClientAndMethods   Main class, shared helpers (ID generation, input cleaning, query execution)
│   ├── HomeMenu              Home screen
│   ├── AddClient             Add Client screen
│   └── DbConnection          Database connection
├── db/                       SQL scripts for the database
├── Additional documentation/ Supporting project documents, including the client's requirements.
├── SF_Logo.png
└── pom.xml
```

## Additional Documentation

The `Additional documentation/` folder holds supporting material for the project, including two Word documents describing what the client requested, as well as the SQL for table creation, an ER diagram, relational table, and UI diagram.

## Design Notes and Future Work

- **Single stored-procedure entry point.** Keeping all database operations behind one procedure keeps the Java side simple. A future improvement would be separate parameterized procedures or prepared statements instead of a delimited command string.
- **ID generation.** IDs are generated as the current maximum plus one. This works for a single user; for concurrent use, an `AUTO_INCREMENT` column would be safer.
- **Planned: required vs. all fields.** Use a `CardLayout` on the Add Client screen to toggle between required fields only and all fields, for when more client information is known up front.
- **Planned: pipelines.** Automatically create a blank pipeline whenever a client is created.
- **Planned: code organization.** Consolidate the most frequently called methods into one place instead of splitting them between the main class and the database connection class.

## Author

Julia Scott ([@Julia-Scott-161](https://github.com/Julia-Scott-161))

## AI Usage
Parts of this program were developed with AI assistance (Claude, ChatGPT). However, no real client data was ever used or seen with AI.

AI-assisted sections:
- Stored procedure (in databaseConnection.java)
- Elements in the db folder
- EditFields

Any use of AI was not to generate code for me, but to help me learn how to do it. Anything not listed was created without any AI assistance.