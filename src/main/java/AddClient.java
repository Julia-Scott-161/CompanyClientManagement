import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

/**
 * Add_Client takes only the required information needed to create a client (the NOT NULL values)
 * it then calls the stored procedure test_execute_command to insert it and takes you
 * back to the home menu.
 * Eventually, a blank pipeline should also be created when client gets created.
 *
 * Main features:
    * AddClient uses FlowLayout, and cannot be closed by the top-right X.
    * it's size is 500 x 600, and centered to the screen.
    * The panels in AddClient are: fieldsPanel (name + note), sourcePanel, and buttonPanel.
 * @author Julia Scott
 */
public class AddClient extends JFrame implements ActionListener {
    JButton addButton =  new JButton("   Add  ");
    JButton backButton = new JButton(" Cancel ");
    JTextField name = new JTextField();
    JTextField note = new JTextField();
    JComboBox<String> box;

    AddClient() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //application cannot be closed by X

        // other details handled by setDefaults in RunClientAndMethods.java
        this.setSize(500,600);
        this.setLocationRelativeTo(null);

        this.add(new JLabel("Add New Client"), CENTER_ALIGNMENT);

        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setPreferredSize(new Dimension(450,300));
        fieldsPanel.setLayout(new GridLayout(4,2,0,25));

        fieldsPanel.add(new JLabel("Legal Name: "), LEFT_ALIGNMENT);
        fieldsPanel.add(name);


        fieldsPanel.add(new JLabel("Note: "));
        fieldsPanel.add(note);

        this.add(fieldsPanel);

        JPanel sourcePanel = new JPanel();
        JLabel typeLab = new JLabel("Source:");
        sourcePanel.add(typeLab);
        String[] options = {"Direct-Web", "Direct-WalkIn", "Travel-Agent", "Social-Media", "Referral", "Trade-Show"};
        box = new JComboBox(options);
        sourcePanel.add(box);

        this.add(sourcePanel);

//---------------BUTTONS-----------------//
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(500,150));

        // By default, panels already use FlowLayout, so it's just restated for clarity
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,125,50));

        // add actions
        addButton.addActionListener(this);
        backButton.addActionListener(this);

        // add buttons to panel
        buttonPanel.add(addButton);
        buttonPanel.add(backButton);

        // add panel to frame
        this.add(buttonPanel);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addButton) {
            // code to get today's date for the Date_Added section
            LocalDate today = LocalDate.now();
            Date sqlDate = Date.valueOf(today);
            String id;
            try {
                // id calls to a method in RunClient_and_Methods which generates a unique ID
                // that is one more than the current max
                id = String.valueOf(RunClientAndMethods.generate_unique_ID());
                System.out.println("ID = "+ id);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

            /*
             * message parses the given values into a statement which
             * the stored procedure test_execute_command uses to add
             * the client to the database.
             */
            // calls the method execute_query from RunClientAndMethods.java
            // which executes the stored procedure
            StringBuilder message = new StringBuilder("INSERT:CLIENT:");
            message.append("Client_ID:").append(id)
                    .append(":Date_Added:").append(sqlDate)
                    .append(":Legal_Name:").append(RunClientAndMethods.clean(name.getText()))
                    .append(":Source:").append(box.getSelectedItem().toString())
                    .append(":Note:").append(RunClientAndMethods.clean(note.getText()));
            System.out.println(message.toString());
            try {
                RunClientAndMethods.executeQuery(message.toString());
                // message popup to confirm client added successfully
                JOptionPane.showMessageDialog(null, "Client Added");
                EventQueue.invokeLater(HomeMenu::new);
                this.dispose();
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Client couldn't be added: " + e1.getMessage());
            }
        }
        if (e.getSource() == backButton) {
            EventQueue.invokeLater(RunClientAndMethods::home_menu_screen);
            AddClient.this.dispose();
        }
    }
}
