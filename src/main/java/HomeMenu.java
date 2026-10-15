import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class HomeMenu extends JFrame implements ActionListener {
    DefaultListModel<Client> model = new DefaultListModel<>();
    JList<Client> list = new JList<>(model);
    JButton updateButton = new JButton("View/Update Client");
    JButton addButton = new JButton("    Add Client   ");
    JButton deleteButton = new JButton("   Delete Client   ");

    /**
     * Sets up a home menu, launched when database
     * has successfully connected to a database.
     * Uses 3 panels, for title, list, and buttons.
     * Sets up home menu screen with Safari Adventures Logo,
     * gives it an add, delete, and update button,
     * and gets a list with all the client's details.
     * @author Julia Scott
     */
    HomeMenu() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Launcher stops when application is closed, stored procedure is removed as well
        this.setResizable(false);
        ImageIcon image = new ImageIcon("SF_Logo.png");
        this.setIconImage(image.getImage());
        this.setTitle("Demo Company Client Management");
        this.setSize(500,600);

        this.setLocationRelativeTo(null);
        this.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));

//---------------TITLE-----------------//
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(500, 100));
        titlePanel.setLayout(new BorderLayout(0,15));

        JLabel title = new JLabel("Demo Company Client Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        titlePanel.add(title);

        this.add(titlePanel);


//---------------CLIENT LIST-----------------//
        JPanel clientList = new JPanel();
        clientList.setPreferredSize(new Dimension(375, 350));
        clientList.setLayout(new BorderLayout(0,10));

        JLabel clientsLabel = new JLabel("Clients List:", SwingConstants.CENTER);
        clientsLabel.setFont(new Font("Arial", Font.BOLD, 12));
        clientList.add(clientsLabel, BorderLayout.NORTH);

        //creates client objects, using Client.java class
        try {
            PreparedStatement stmt = DatabaseConnection.conn.prepareStatement("SELECT * FROM CLIENT");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                model.addElement(new Client(rs.getInt("Client_ID"), rs.getString("Legal_Name"),
                        rs.getString("Mailing_Addr"), rs.getBoolean("Rec_PassPrt"), rs.getDate("Birth_Date"),
                        rs.getDate("Date_Added"), rs.getString("Company"), rs.getString("Note"),
                        rs.getString("Source")));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        JScrollPane clientScrollPane = new JScrollPane(list);
        clientScrollPane.setPreferredSize(new Dimension(375, 350));
        clientList.add(clientScrollPane, BorderLayout.CENTER);

        this.add(clientList);

//---------------MENU BUTTONS-----------------//
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(350, 150));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        buttonPanel.add(addButton);
        addButton.addActionListener(this);
        buttonPanel.add(updateButton);
        updateButton.addActionListener(this);
        buttonPanel.add(deleteButton);
        deleteButton.addActionListener(this);

        this.add(buttonPanel);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //get the selected client (at top since used in both update/delete actions)
        Client selected = list.getSelectedValue();
        //if add selected
        if (e.getSource() == addButton) {
            EventQueue.invokeLater(RunClientAndMethods::addClientScreen);
            HomeMenu.this.dispose();
        }
        //if update selected, call view/edit UI based on selected client values
        if (e.getSource() == updateButton) {
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a Client first");
                return;
            }
            EventQueue.invokeLater(() -> new ViewEditClientUI(selected));
            HomeMenu.this.dispose();
        }
        if (e.getSource() == deleteButton) {
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a Client first");
                return;
            }
            int answer = JOptionPane.showConfirmDialog(this,
                    "Delete " + selected.getName() + "?\nAll related contacts, touchpoints and pipeline will be deleted too.",
                    "Delete Client", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (answer != JOptionPane.YES_OPTION) {
                return;
            }
            String message = "DELETE:CLIENT:Client_ID:" + selected.getId();
            try {
                RunClientAndMethods.executeQuery(message);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            model.removeElement(selected);
        }
    }

}
