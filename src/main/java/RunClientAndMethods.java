import javax.swing.*;
import java.sql.*;

public class RunClientAndMethods {

    /**
     * set_defaults(frame) adds consistency between UIs without effecting
     * existing UI design.
     * Replaces default Java logo on top left with a duck logo
     * Sets title to "Demo Company Client Management"
     * centers frame location to the middle of the computer
     * @param frame the JFrame that is created by that class
     */
    public static void setDefaults(JFrame frame) {
        ImageIcon image = new ImageIcon("SF_Logo.png");
        frame.setIconImage(image.getImage());
        frame.setTitle("Demo Company Client Management");
        frame.setLocationRelativeTo(null);
    }

    /**
     *
     * @param set results gotten from execute_queries
     * @return
     * @throws SQLException
     */
    public static StringBuilder formatResults(ResultSet set) throws SQLException {
        StringBuilder result = new StringBuilder();

        ResultSetMetaData meta = set.getMetaData();
        int columns = meta.getColumnCount();

        while (set.next()) {
            for (int i = 1; i <= columns; i++) {
                result.append(meta.getColumnName(i))
                        .append(": ")
                        .append(set.getString(i))
                        .append(" : ");
            }
        }
        return result;
    }

    static String delegateQueries(String request) throws Exception {
        String[] parts = request.split(":");

        if (parts.length < 0) {
            return "Invalid request: Too small";
        }

        String command = parts[0];

        switch (command) {
            case "SELECT":
                return "select";
            case "INSERT":
                return "insert";
            case "UPDATE":
                return "update";
            case "DELETE":
                return "delete";
            default:
                return "Unknown command";
        }
    }

    /**
     * executes stored procedure which handles INSERT, UPDATE, SELECT, DELETE
     * @author Julia Scott
     * @param message parsed statement that is put in stored procedure
     * @throws SQLException
     */
    public static void executeQuery(String message) throws SQLException {
        String statement = "";
        try {
            statement = delegateQueries(message);
        } catch (Exception e) {
            return;
        }

        try (CallableStatement stmt = DatabaseConnection.conn.prepareCall("CALL test_execute_command(?)")) {
            stmt.setString(1, message);
            boolean hasResults = stmt.execute();
            if (hasResults) {
                try (ResultSet set = stmt.getResultSet()) {
                    if (statement.equals("select")) {
                        StringBuilder result = formatResults(set);
                    }
                }
            }
        }
    }

    /**
     * The stored procedure splits commands on ':' (and strips quotes), so a ':' typed into
     * a text field would shift every value after it. This keeps typed text safe to send.
     * @param value text typed by the user
     * @return value with ':' replaced by ' -' and quotes removed
     */
    public static String clean(String value) {
        return value == null ? "" : value.replace(":", " -").replace("'", "").replace("\n", " ");
    }

    /**
     * generates a unique ID for adding table rows
     * @throws SQLException
     */
    public static int generate_unique_ID() throws SQLException {
        try {
            PreparedStatement stmt = DatabaseConnection.conn.prepareStatement("SELECT MAX(Client_ID) AS max_ID FROM CLIENT");
            ResultSet set = stmt.executeQuery();
            //"
            set.next();
            int new_id = set.getInt("max_ID") + 1;
            System.out.println(new_id);

            set.close();
            stmt.close();
            return new_id;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }

    /**
     * near duplicate of previous method. Hardcoded id column name and table
     * @return int id which is 1 more than the current highest id
     * @throws SQLException
     */
    public static int generate_contact_ID() throws SQLException {
        try {
            PreparedStatement stmt = DatabaseConnection.conn.prepareStatement("SELECT MAX(Contact_ID) AS max_ID FROM CONTACT");
            ResultSet set = stmt.executeQuery();
            set.next();
            int new_id = set.getInt("max_ID") + 1;
            System.out.println(new_id);

            set.close();
            stmt.close();
            return new_id;
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

    }
    static void add_client_screen() {
        AddClient frame = new AddClient();
        setDefaults(frame);
    }

    static void home_menu_screen() {
        HomeMenu frame = new HomeMenu();
        setDefaults(frame);
    }

    static void TempScreen() {
        TempScreen frame = new TempScreen();
        setDefaults(frame);
    }

    static void database_connection() {
        DatabaseConnection frame = new DatabaseConnection();
        setDefaults(frame);
    }

    public static void main(String[] args) {
        database_connection();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                DatabaseConnection.dropProcedure();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));
    }
}
