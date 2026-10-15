import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ViewEditClientUI extends JFrame implements ActionListener {

        JButton backButton = new JButton("Back to Home");
        JButton tripButton = new JButton("Go To Trips");
        JButton addContactButton = new JButton("Add Contact Info");
        JButton addTouchpointButton = new JButton("Add Touchpoint");
        JButton editContactButton = new JButton("View/Edit Contact Info");
        JButton editTouchpointButton = new JButton("View/Edit Touchpoint");
        JButton deleteContactButton = new JButton("Delete Contact");
        JButton deleteTouchpointButton = new JButton("Delete Touchpoint");
        JButton pipelineButton = new JButton("Edit Pipeline Info");
        String location;
        DefaultListModel<Touchpoint> tpModel = new DefaultListModel<>();
        JList<Touchpoint> tpList = new JList<>(tpModel);
        DefaultListModel<Contact> cModel = new DefaultListModel<>();
        JList<Contact> cList = new JList<>(cModel);
        private Client myClient;

    /**
     * Editiable fields are done by the EditField.java class
     * @param client a model out of in a client list
     * @author Julia Scott
     */
    public ViewEditClientUI(Client client) {
            this.myClient = client;
//            setTitle("Demo Company Client Management");
//            ImageIcon image = new ImageIcon("SF_Logo.png");
//            this.setIconImage(image.getImage());
            setSize(600, 750);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

            JPanel main = new JPanel();
            main.setLayout(new BorderLayout(10, 10));
            main.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));


//---------------TOP SECTION-----------------//
            JPanel topPanel = new JPanel(new GridBagLayout());
            GridBagConstraints g = new GridBagConstraints();
            g.insets = new Insets(5, 5, 5, 5);
            g.anchor = GridBagConstraints.WEST;

            // Left column
            g.gridx = 0; g.gridy = 0;
            //topPanel.add(new JLabel("Client: " + client.getName()), g); //hardcoded view
            String colName = "Legal_Name";
            EditField name =
                    new EditField(
                            client.getId(),
                            colName,
                            "Client",
                            EditField.FieldType.TEXT,
                            client.getName()
                    );
            topPanel.add(name);

            g.gridy++;
            //topPanel.add(new JLabel("Company: " + client.getCompany()), g); //hardcoded view
            String colComp = "Company";
            EditField company =
                    new EditField(
                            client.getId(),
                            colComp,
                            "Company",
                            EditField.FieldType.TEXT,
                            client.getCompany()
                    );
            topPanel.add(company);

            g.gridy++;
            //topPanel.add(new JLabel("Source: " + client.getSource()), g); //hardcoded view
            String colSource = "Source";
            EditField source =
                    new EditField(
                            client.getId(),
                            colSource,
                            "Source",
                            EditField.FieldType.DROPDOWN,
                            client.getSource(),
                            "Direct-Web", "Direct-WalkIn", "Travel-Agent", "Social-Media", "Referral", "Trade-Show"
                    );
            topPanel.add(source);

//---------------NON-EDITABLE VALUES-----------------//
            g.gridx = 1; g.gridy = 0;
            //topPanel.add(new JLabel("Date Added: " + client.getDateA()), g); //hardcoded view

            g.gridy++;
            topPanel.add(new JLabel("Client ID: " + client.getId()), g); //Unchanging

            g.gridy++;
            topPanel.add(new JLabel("Date Added: " + client.getDateA()), g); //Unchanging


//---------------PIPELINE-----------------//
            g.gridx = 0; g.gridy++;
            topPanel.add(new JLabel("Location in Pipeline:"), g);

            g.gridx = 1;
            try {
                PreparedStatement stmt = DatabaseConnection.conn.prepareStatement("SELECT PipeLoc FROM PIPELINE WHERE Client_ID = ?");
                stmt.setInt(1, client.getId());

                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {   // a newly added client may not have a pipeline row yet
                    location = rs.getString("PipeLoc");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            topPanel.add(new JLabel(location), g);

            g.gridx = 2;
            topPanel.add(pipelineButton, g);

            main.add(topPanel, BorderLayout.NORTH);

            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

//---------------PASSPORT-----------------//
            //passport: checked if yes, open if no
            JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

            String colRPP = "Rec_PassPrt";
            EditField PassPrt =
                    new EditField(
                            client.getId(),
                            colRPP,
                            "Has Passport",
                            EditField.FieldType.BOOLEAN,
                            client.getPassPrt()
                    );
            checkPanel.add(PassPrt);
            String input;
//---------------BIRTHDATE-----------------//
            if (client.getBdate() == null) {
                input = "YYYY-MM-DD";
            }
            else {
                input = client.getBdate().toString();
            }
            String colDate = "Birth_Date";
            EditField birth =
                    new EditField(
                            client.getId(),
                            colDate,
                            "Birth date",
                            EditField.FieldType.TEXT,
                            input
                    );
            checkPanel.add(birth);
            center.add(checkPanel);


//---------------TOUCHPOINTS-----------------//
            JPanel touchSection = new JPanel();
            touchSection.setLayout(new BoxLayout(touchSection, BoxLayout.Y_AXIS));
            touchSection.setBorder(BorderFactory.createTitledBorder("Touchpoints"));

            JPanel touchListPanel = new JPanel(new BorderLayout());
            touchListPanel.setBackground(Color.GRAY);
            touchListPanel.setPreferredSize(new Dimension(500, 120));
            try {
                //calls direct sql, so it uses executeQuery() rather than
                //RunClient_and_Methods.execute_query(stmt).
                PreparedStatement stmt = DatabaseConnection.conn.prepareStatement(
                        "SELECT * FROM TOUCHPOINT WHERE Client_ID = ?"
                );
                stmt.setInt(1, client.getId());
                ResultSet rs = stmt.executeQuery();

                /*
                 * creates Touchpoint objects from Touchpoint.java
                 * for limited database calls and easier methods to
                 * get Touchpoint values
                 */
                while (rs.next()) {
                    tpModel.addElement(
                            new Touchpoint(
                                    rs.getInt("TP_ID"),
                                    rs.getDate("TPdateocc"),
                                    rs.getString("TP_Type"),
                                    rs.getInt("EngageScr"),
                                    rs.getString("TP_Note")
                            )
                    );
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            touchListPanel.add(tpList);
            touchListPanel.add(new JScrollPane(tpList));
            touchSection.add(touchListPanel);

            //Touchpoint buttons
            JPanel touchButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            touchButtons.add(addTouchpointButton);
            touchButtons.add(editTouchpointButton);
            touchButtons.add(deleteTouchpointButton);

            touchSection.add(touchButtons);

            center.add(touchSection);


//---------------CONTACTS-----------------//
            JPanel contactSection = new JPanel();
            contactSection.setLayout(new BoxLayout(contactSection, BoxLayout.Y_AXIS));
            contactSection.setBorder(BorderFactory.createTitledBorder("Contact Info"));

            JPanel contactListPanel = new JPanel(new BorderLayout());
            contactListPanel.setPreferredSize(new Dimension(500, 100));
            try {
                PreparedStatement stmt = DatabaseConnection.conn.prepareStatement("SELECT * FROM CONTACT WHERE Client_ID = ?");
                stmt.setInt(1, client.getId());

                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    cModel.addElement(
                            new Contact(
                                    rs.getInt("Contact_ID"),
                                    rs.getString("Cnt_Type"),
                                    rs.getBoolean("Pref_Cont"),
                                    rs.getString("Contact_Info")
                            )
                    );
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            contactListPanel.add(cList);
            contactListPanel.add(new JScrollPane(cList));
            contactSection.add(contactListPanel);

            //Contact buttons
            JPanel contactButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
            contactButtons.add(addContactButton);
            contactButtons.add(editContactButton);
            contactButtons.add(deleteContactButton);

            contactSection.add(contactButtons);

            center.add(contactSection);

//---------------NOTES-----------------//
            String colNote = "Note";
            EditField notes =
                    new EditField(
                            client.getId(),
                            colNote,
                            "Notes",
                            EditField.FieldType.TEXT,
                            client.getNote(),
                            colNote
                    );
            center.add(notes);

            main.add(center, BorderLayout.CENTER);

            //Bottom Menu
            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
            //bottom.add(save_button);
            bottom.add(backButton);
            bottom.add(tripButton);

            main.add(bottom, BorderLayout.SOUTH);

            backButton.setActionCommand("BACK");
            addContactButton.setActionCommand("ADD_CONTACT");
            editContactButton.setActionCommand("EDIT_CONTACT");
            tripButton.setActionCommand("TRIPS");
            pipelineButton.setActionCommand("PIPELINE");
            addTouchpointButton.setActionCommand("ADD_TOUCHPOINT");
            editTouchpointButton.setActionCommand("EDIT_TOUCHPOINT");


            backButton.addActionListener(this);
            tripButton.addActionListener(this);
            pipelineButton.addActionListener(this);
            addContactButton.addActionListener(this);
            editContactButton.addActionListener(this);
            addTouchpointButton.addActionListener(this);
            editTouchpointButton.addActionListener(this);

            this.add(main);
            this.setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "BACK":
                    EventQueue.invokeLater(RunClientAndMethods::homeMenuScreen);
                    ViewEditClientUI.this.dispose();
                    break;
                case "TRIPS":
                    JOptionPane.showMessageDialog(this, "Trips not yet implemented");
                    break;
                case "PIPELINE":
                    EventQueue.invokeLater(RunClientAndMethods::TempScreen);
                    ViewEditClientUI.this.dispose();
                    break;
                case "EDIT_TOUCHPOINT":
                    JOptionPane.showMessageDialog(this, "Editing touchpoints is not yet implemented");
                    break;
                case "ADD_TOUCHPOINT":
                    JOptionPane.showMessageDialog(this, "Adding touchpoints is not yet implemented");
                    break;
                case "EDIT_CONTACT":
                    JOptionPane.showMessageDialog(this, "Editing contacts is not yet implemented");
                    break;
                case "ADD_CONTACT":
                    EventQueue.invokeLater(() -> new AddContact(myClient));
                    ViewEditClientUI.this.dispose();
                    break;
            }
        }
    }

