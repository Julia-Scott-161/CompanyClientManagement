import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * AddContact is similar to add client, but takes all possible values, not just 'required'
 * Should implement checks to make sure null values are not added, but that was not done
 * //TODO: why is my class naming so inconsistent :(
 * @author Julia Scott
 */
//TODO:
public class AddContact extends JFrame implements ActionListener {

    private JComboBox<String> contactTypeBox;
    private JCheckBox preferredBox;
    private JTextArea contactDetailsArea;

    private JButton add_button = new JButton("Add Contact");
    private JButton back_button = new JButton("Back to Client Info");

    private Client myClient;

    public AddContact(Client client) {
        this.myClient = client;

        this.setTitle("Add Contact");
        ImageIcon image = new ImageIcon("SF_Logo.png");
        this.setIconImage(image.getImage());
        setSize(400, 350);
        setLocationRelativeTo(null);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

//---------------CONTACT TYPE-----------------//
        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setBorder(BorderFactory.createTitledBorder("Contact Type"));

        contactTypeBox = new JComboBox<>(new String[]{
                "Email", "Phone", "LinkedIn", "Other"
        });

        typePanel.add(contactTypeBox, BorderLayout.CENTER);
        main.add(typePanel);

//---------------PREFERRED CONTACT-----------------//
        JPanel prefPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prefPanel.setBorder(BorderFactory.createTitledBorder("Preferred Contact"));

        preferredBox = new JCheckBox("Preferred");
        prefPanel.add(preferredBox);

        main.add(prefPanel);

//---------------CONTACT INFORMATION-----------------//
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Contact Info"));

        contactDetailsArea = new JTextArea(4, 20);
        contactDetailsArea.setLineWrap(true);
        contactDetailsArea.setWrapStyleWord(true);

        infoPanel.add(new JScrollPane(contactDetailsArea), BorderLayout.CENTER);
        main.add(infoPanel);

//---------------BUTTONS-----------------//
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));

        //add buttons to panel
        bottom.add(add_button);
        bottom.add(back_button);

        //declare button actions
        add_button.addActionListener(this);
        back_button.addActionListener(this);

        //add panels to frame, declare/clarify layout placement
        this.add(main,BorderLayout.CENTER);
        this.add(bottom, BorderLayout.SOUTH);

        setVisible(true);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == add_button) {
            String type = (String) contactTypeBox.getSelectedItem();
            boolean preferred = preferredBox.isSelected();
            String info = contactDetailsArea.getText();
            //for testing purposes
//            System.out.println("Adding Contact:");
//            System.out.println(type);
//            System.out.println(preferred);
//            System.out.println(info);

            String id;
            try {
                //id calls to a method in RunClient_and_Methods which generates a unique ID
                //that is one more than the current max
                //currently a different method than to generate client_ID, since coding it
                // to accept table/Column Name didn't get implemented in time
                id = String.valueOf(RunClientAndMethods.generate_contact_ID());
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            // Preferred is currently a string for UI reading, so it's
            // set to 1 or 0 so database can read it.
            int preferredInt = preferred ? 1 : 0;
            /*
             * message parses the given values into a statement which
             * the stored procedure test_execute_command uses to add
             * the client to the database.
             */
            //calls the method execute_query from RunClientAndMethods.java
            //which executes the stored procedure
            StringBuilder message = new StringBuilder("INSERT:CONTACT:");
            message.append("Contact_ID:").append(id)
                    .append(":Cnt_Type:").append(type)
                    .append(":Pref_Cont:").append(preferredInt)
                    .append(":Contact_Info:").append(RunClientAndMethods.clean(info))
                    .append(":Client_ID:").append(myClient.getId());

            try {
                RunClientAndMethods.executeQuery(message.toString());
                //message to show that contact was added successfully
                JOptionPane.showMessageDialog(null, "Contact Added");
                EventQueue.invokeLater(() -> new ViewEditClientUI(myClient)); //go back to the client which contact was added for
                this.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Contact couldn't be added: " + ex.getMessage());
            }
        }
        if (e.getSource() == back_button) {
            EventQueue.invokeLater(() -> new ViewEditClientUI(myClient)); //go back to the client which add contact was selected
            this.dispose();
        }
    }
}
