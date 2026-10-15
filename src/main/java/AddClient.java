import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.sql.Date;

/**
 * Add_Client takes only the required information needed to create a client (NOT NULL)
 * it then calls the stored procedure test_execute_command to insert it and takes you
 * back to the home menu.
 * Would have been nice to implement the cardLayout to toggle between only required
 * fields, and all fields, in case more information about the client was known when
 * adding them.
 * Eventually, a blank pipeline should also be created when client gets created.
 * @author Julia Scott
 */
public class AddClient extends JFrame implements ActionListener {
    JButton add_button =  new JButton("   Add  ");
    JButton back_button = new JButton(" Cancel ");
    JTextField name = new JTextField();
    JTextField note = new JTextField();
    JComboBox box;

    AddClient() {
        this.setLayout(new FlowLayout(FlowLayout.CENTER));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //application cannot be closed by X

        //other details handled by setDefaults in RunClientAndMethods.java
        this.setSize(500,600);
        this.setLocationRelativeTo(null);

        this.add(new JLabel("Add New Client"), CENTER_ALIGNMENT);

        JPanel fields = new JPanel();
        fields.setPreferredSize(new Dimension(450,300));
        fields.setLayout(new GridLayout(4,2,0,25));

        fields.add(new JLabel("Legal Name: "), LEFT_ALIGNMENT);
        fields.add(name);


        fields.add(new JLabel("Note: "));
        fields.add(note);

        this.add(fields);

        JPanel source = new JPanel();
        JLabel typeLab = new JLabel("Source:");
        source.add(typeLab);
        String[] options = {"Direct-Web", "Direct-WalkIn", "Travel-Agent", "Social-Media", "Referral", "Trade-Show"};
        box = new JComboBox(options);
        source.add(box);

        this.add(source);

//---------------BUTTONS-----------------//
        JPanel B_panel = new JPanel();
        B_panel.setPreferredSize(new Dimension(500,150));
        //By default, panels already use FlowLayout.
        //But don't break what's not broken before its due.
        B_panel.setLayout(new FlowLayout(FlowLayout.CENTER,125,50));

        //add actions
        add_button.addActionListener(this);
        back_button.addActionListener(this);

        //add buttons to panel
        B_panel.add(add_button);
        B_panel.add(back_button);

        //add panel to frame
        this.add(B_panel);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add_button) {
            //code to get today's date for the Date_Added section
            LocalDate today = LocalDate.now();
            Date sqlDate = Date.valueOf(today);
            //TODO: organize where most called methods are: RunClientAndMethods.java vs DatabaseConnection.java
            String id;
            try {
                //id calls to a method in RunClient_and_Methods which generates a unique ID
                //that is one more than the current max
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
            //calls the method execute_query from RunClientAndMethods.java
            //which executes the stored procedure
            StringBuilder message = new StringBuilder("INSERT:CLIENT:");
            message.append("Client_ID:").append(id)
                    .append(":Date_Added:").append(sqlDate)
                    .append(":Legal_Name:").append(RunClientAndMethods.clean(name.getText()))
                    .append(":Source:").append(box.getSelectedItem().toString())
                    .append(":Note:").append(RunClientAndMethods.clean(note.getText()));
            System.out.println(message.toString());
            try {
                RunClientAndMethods.executeQuery(message.toString());
                //message popup to confirm client added successfully
                JOptionPane.showMessageDialog(null, "Client Added");
                EventQueue.invokeLater(HomeMenu::new);
                this.dispose();
            } catch (Exception e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(null, "Client couldn't be added: " + e1.getMessage());
            }
        }
        if (e.getSource() == back_button) {
            EventQueue.invokeLater(RunClientAndMethods::home_menu_screen);
            AddClient.this.dispose();
        }
    }
}
