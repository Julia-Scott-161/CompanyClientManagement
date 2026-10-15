import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
/**
 * DB_connection sets up the database connection so that any testers
 * in our group can connect to their individual databases.
 * @author Julia Scott
 */
public class DatabaseConnection extends JFrame implements ActionListener {
    protected static Connection conn = null;

    JButton DB_connect = new JButton("Connect to Database");
    JPasswordField password = new JPasswordField();
    JTextField username = new JTextField();


    DatabaseConnection(){
        this.setLayout(new FlowLayout(FlowLayout.CENTER,15,0));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //application cannot be closed by X
        this.setResizable(false);
        ImageIcon image = new ImageIcon("SF_Logo.png");
        this.setIconImage(image.getImage());
        this.setTitle("Demo Company Client Management");
        this.setSize(300,300);
        this.setLocationRelativeTo(null);


        JPanel user = new JPanel();
        user.setPreferredSize(new Dimension(300,75));
        JLabel username_label = new JLabel("Database: ");
        user.add(username_label);


        username.setPreferredSize(new Dimension(100,25));
        user.add(username);

        JPanel pass = new JPanel();
        pass.setPreferredSize(new Dimension(300,75));
        JLabel password_label = new JLabel("Password: ");
        pass.add(password_label);


        password.setPreferredSize(new Dimension(100,25));
        pass.add(password);

        JPanel login = new JPanel();
        login.setPreferredSize(new Dimension(300,75));
        login.add(DB_connect);
        DB_connect.addActionListener(this);
        this.getRootPane().setDefaultButton(DB_connect); //pressing Enter also clicks Connect

        this.add(user);
        this.add(pass);
        this.add(login);

        this.setVisible(true);
    }

    /**
     * drops the procedure if it exists at the start of compiling, and deletes it at the end.
     * The intention of deleting it was to add it to other people's databases at the beginning,
     * edit the database, and then delete it, so that any real changes were made solely to
     * the data in the tables of their database.
     * @throws SQLException
     */
    public static void dropProcedure() throws SQLException {
        if (conn != null) {
            String sql = "DROP PROCEDURE IF EXISTS test_execute_command";
            // DDL is not a callable "{call ...}", so use a plain Statement (works with every JDBC driver)
            try (Statement dropSP = DatabaseConnection.conn.createStatement()) {
                dropSP.execute(sql);
            }
        }
    }

    /**
     * addProcedure standardizes any database by adding the stored procedure
     * that my written code calls, so any previous work before connecting the
     * UIs will not have to be rewritten based on whose database is used
     * @throws SQLException
     */
    public static void addProcedure() throws SQLException {
        String create_sql = ("    CREATE PROCEDURE test_execute_command(IN p_command VARCHAR(2000))\n" +
                "    BEGIN\n" +
                "    DECLARE v_operation   VARCHAR(20);\n" +
                "    DECLARE v_table       VARCHAR(100);\n" +
                "    DECLARE v_field1      VARCHAR(100);\n" +
                "    DECLARE v_value1      VARCHAR(500);\n" +
                "    DECLARE v_field2      VARCHAR(100);\n" +
                "    DECLARE v_value2      VARCHAR(500);\n" +
                "    DECLARE v_token_count INT;\n" +
                "    DECLARE v_pos         INT;\n" +
                "    DECLARE v_field       VARCHAR(100);\n" +
                "    DECLARE v_value       VARCHAR(500);\n" +
                "    DECLARE v_col_list    VARCHAR(2000) DEFAULT '';\n" +
                "    DECLARE v_val_list    VARCHAR(2000) DEFAULT '';\n" +
                "    DECLARE v_executed    BOOLEAN DEFAULT FALSE;\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- Count total colon-delimited tokens\n" +
                "          -- -------------------------------------------------------\n" +
                "    SET v_token_count = LENGTH(p_command)\n" +
                "            - LENGTH(REPLACE(p_command, ':', '')) + 1;\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- Extract operation and table (always tokens 1 and 2)\n" +
                "          -- -------------------------------------------------------\n" +
                "    SET v_operation = SUBSTRING_INDEX(p_command, ':', 1);\n" +
                "    SET v_table     = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 2), ':', -1);\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- Validate table identifier\n" +
                "          -- -------------------------------------------------------\n" +
                "    IF v_table NOT REGEXP '^[a-zA-Z_][a-zA-Z0-9_]*$' THEN\n" +
                "    SIGNAL SQLSTATE '45000'\n" +
                "    SET MESSAGE_TEXT = 'Invalid table identifier';\n" +
                "    END IF;\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- SELECT  →  SELECT:TABLE:FIELD:'VALUE'\n" +
                "            -- -------------------------------------------------------\n" +
                "    IF v_operation = 'SELECT' AND v_executed = FALSE THEN\n" +
                "    SET v_field1 = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 3), ':', -1);\n" +
                "    SET v_value1 = REPLACE(SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 4), ':', -1), \"'\", '');\n" +
                "\n" +
                "    IF v_field1 NOT REGEXP '^[a-zA-Z_][a-zA-Z0-9_]*$' THEN\n" +
                "    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid field identifier';\n" +
                "    END IF;\n" +
                "\n" +
                "    SET @sql = CONCAT('SELECT * FROM ', v_table, ' WHERE ', v_field1, ' = ?');\n" +
                "    SET @p1  = v_value1;\n" +
                "    PREPARE stmt FROM @sql;\n" +
                "    EXECUTE stmt USING @p1;\n" +
                "    DEALLOCATE PREPARE stmt;\n" +
                "    SET v_executed = TRUE;\n" +
                "    END IF;\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- INSERT  →  INSERT:TABLE:FIELD1:'VALUE1':FIELD2:'VALUE2':FIELD3:'VALUE3'...\n" +
                "            --            Supports any number of field/value pairs\n" +
                "          -- -------------------------------------------------------\n" +
                "    IF v_operation = 'INSERT' AND v_executed = FALSE THEN\n" +
                "\n" +
                "              -- Token count must be even after operation+table (pairs of field:value)\n" +
                "              -- Minimum: INSERT:TABLE:FIELD:'VALUE' = 4 tokens\n" +
                "    IF (v_token_count < 4) OR ((v_token_count % 2) != 0) THEN\n" +
                "    SIGNAL SQLSTATE '45000'\n" +
                "    SET MESSAGE_TEXT = 'INSERT requires at least one FIELD:''VALUE'' pair';\n" +
                "    END IF;\n" +
                "\n" +
                "              -- Loop through field:value pairs starting at token 3\n" +
                "    SET v_pos = 3;\n" +
                "\n" +
                "    WHILE v_pos < v_token_count DO\n" +
                "\n" +
                "    SET v_field = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', v_pos),     ':', -1);\n" +
                "    SET v_value = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', v_pos + 1), ':', -1);\n" +
                "\n" +
                "                  -- Validate column identifier\n" +
                "    IF v_field NOT REGEXP '^[a-zA-Z_][a-zA-Z0-9_]*$' THEN\n" +
                "    SIGNAL SQLSTATE '45000'\n" +
                "    SET MESSAGE_TEXT = 'Invalid field identifier in INSERT';\n" +
                "    END IF;\n" +
                "\n" +
                "                  -- Build column and value lists\n" +
                "    IF v_col_list != '' THEN\n" +
                "    SET v_col_list = CONCAT(v_col_list, ', ');\n" +
                "    SET v_val_list = CONCAT(v_val_list, ', ');\n" +
                "    END IF;\n" +
                "\n" +
                "    SET v_col_list = CONCAT(v_col_list, v_field);\n" +
                "\n" +
                "                  -- QUOTE() safely escapes the value and wraps it in single quotes\n" +
                "                  -- Strips surrounding quotes from protocol format first: 'Bob' -> Bob\n" +
                "    SET v_val_list = CONCAT(v_val_list,\n" +
                "            QUOTE(REPLACE(v_value, \"'\", '')));\n" +
                "\n" +
                "    SET v_pos = v_pos + 2;\n" +
                "    END WHILE;\n" +
                "\n" +
                "    SET @sql = CONCAT(\n" +
                "                  'INSERT INTO ', v_table,\n" +
                "                  ' (', v_col_list, ')',\n" +
                "                          ' VALUES (', v_val_list, ')'\n" +
                "    );\n" +
                "\n" +
                "    PREPARE stmt FROM @sql;\n" +
                "    EXECUTE stmt;\n" +
                "    DEALLOCATE PREPARE stmt;\n" +
                "    SET v_executed = TRUE;\n" +
                "    END IF;\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- UPDATE  →  UPDATE:TABLE:WHERE_FIELD:'WHERE_VALUE':SET_FIELD:'SET_VALUE'\n" +
                "            -- -------------------------------------------------------\n" +
                "    IF v_operation = 'UPDATE' AND v_executed = FALSE THEN\n" +
                "    SET v_field1 = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 3), ':', -1);\n" +
                "    SET v_value1 = REPLACE(SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 4), ':', -1), \"'\", '');\n" +
                "    SET v_field2 = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 5), ':', -1);\n" +
                "    SET v_value2 = REPLACE(SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 6), ':', -1), \"'\", '');\n" +
                "\n" +
                "    IF v_field1 NOT REGEXP '^[a-zA-Z_][a-zA-Z0-9_]*$' OR\n" +
                "    v_field2 NOT REGEXP '^[a-zA-Z_][a-zA-Z0-9_]*$' THEN\n" +
                "    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid field identifier';\n" +
                "    END IF;\n" +
                "\n" +
                "    SET @sql = CONCAT(\n" +
                "                  'UPDATE ', v_table,\n" +
                "                  ' SET ',   v_field2, ' = ?',\n" +
                "                          ' WHERE ', v_field1, ' = ?'\n" +
                "    );\n" +
                "    SET @p1 = v_value2;\n" +
                "    SET @p2 = v_value1;\n" +
                "    PREPARE stmt FROM @sql;\n" +
                "    EXECUTE stmt USING @p1, @p2;\n" +
                "    DEALLOCATE PREPARE stmt;\n" +
                "    SET v_executed = TRUE;\n" +
                "    END IF;\n" +
                "\n" +
                "          -- -------------------------------------------------------\n" +
                "                  -- DELETE  →  DELETE:TABLE:FIELD:'VALUE'\n" +
                "            -- -------------------------------------------------------\n" +
                "    IF v_operation = 'DELETE' AND v_executed = FALSE THEN\n" +
                "    SET v_field1 = SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 3), ':', -1);\n" +
                "    SET v_value1 = REPLACE(SUBSTRING_INDEX(SUBSTRING_INDEX(p_command, ':', 4), ':', -1), \"'\", '');\n" +
                "\n" +
                "    IF v_field1 NOT REGEXP '^[a-zA-Z_][a-zA-Z0-9_]*$' THEN\n" +
                "    SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Invalid field identifier';\n" +
                "    END IF;\n" +
                "\n" +
                "    SET @sql = CONCAT('DELETE FROM ', v_table, ' WHERE ', v_field1, ' = ?');\n" +
                "    SET @p1  = v_value1;\n" +
                "    PREPARE stmt FROM @sql;\n" +
                "    EXECUTE stmt USING @p1;\n" +
                "    DEALLOCATE PREPARE stmt;\n" +
                "    SET v_executed = TRUE;\n" +
                "    END IF;\n" +
                "\n" +
                "    IF v_executed = FALSE THEN\n" +
                "    SIGNAL SQLSTATE '45000'\n" +
                "    SET MESSAGE_TEXT = 'Unknown operation. Expected SELECT, INSERT, UPDATE, or DELETE';\n" +
                "    END IF;\n" +
                "\n" +
                "    END");
        try (Statement stored_procedure = conn.createStatement()) {
            stored_procedure.execute(create_sql);
        }
    }

    /**
     * Opens the JDBC connection (database name doubles as the MySQL user, as on the login screen)
     * and (re)creates the stored procedure. Kept separate from the Swing code so it can be reused/tested.
     * @param database name of the database (and user) to log in as
     * @param password that user's password
     * @throws Exception if the driver, connection, or stored procedure setup fails
     */
    public static void connect(String database, String password) throws Exception {
        String URL = ("jdbc:mysql://127.0.0.1:3336/" + database + "?useTimezone=true&serverTimezone=UTC");
        System.out.println("URL: " + URL);
        Class.forName("com.mysql.cj.jdbc.Driver");
        conn = DriverManager.getConnection(URL, database, password);
        System.out.println("Server and database connected");
        try {
            dropProcedure();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        addProcedure();   // if this fails the app can't work
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == DB_connect) {
            if (username.getText().equals("") || password.getText().equals("")) {
                //handle empty values
                JOptionPane.showMessageDialog(null, "Please fill out all the fields");
            }
            else {
                try {
                    connect(username.getText(), password.getText());
                    //set up stored procedure
                    this.setVisible(false); //get rid of/hide start menu
                    EventQueue.invokeLater(RunClientAndMethods::home_menu_screen);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Couldn't connect, try again!\n" + ex.getMessage());
                }
            }
        }
    }
}
