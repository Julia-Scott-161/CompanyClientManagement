import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Home_Menu extends JFrame implements ActionListener {
    static Color logoYellow = new Color(241,204,97);
    DefaultListModel<Client> model = new DefaultListModel<>();
    JList<Client> list = new JList<>(model);
    JButton update_button = new JButton("View/Update Client");
    JButton add_button = new JButton("    Add Client   ");
    JButton delete_button = new JButton("   Delete Client   ");

    /**
     * Sets up a home menu, launched when database
     * has successfully connected to a database.
     * Uses 3 panels, for title, list, and buttons.
     * Sets up home menu screen with Safari Adventures Logo,
     * gives it an add, delete, and update button,
     * and gets a list with all the client's details.
     * @author Julia Scott
     */
    Home_Menu() {
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
            PreparedStatement stmt = DB_connection.conn.prepareStatement("SELECT * FROM CLIENT");
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

        add_button.setBackground(logoYellow);
        update_button.setBackground(logoYellow);
        delete_button.setBackground(logoYellow);

        buttonPanel.add(add_button);
        add_button.addActionListener(this);
        buttonPanel.add(update_button);
        update_button.addActionListener(this);
        buttonPanel.add(delete_button);
        delete_button.addActionListener(this);

        this.add(buttonPanel);

        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //get the selected client (at top since used in both update/delete actions)
        Client selected = list.getSelectedValue();
        //if add selected
        if (e.getSource() == add_button) {
            EventQueue.invokeLater(RunClient_and_Methods::add_client_screen);
            Home_Menu.this.dispose();
        }
        //if update selected, call view/edit UI based on selected client values
        if (e.getSource() == update_button) {
            if (selected == null) {
                JOptionPane.showMessageDialog(this, "Please select a Client first");
                return;
            }
            EventQueue.invokeLater(() -> new ViewEditClientUI(selected));
            Home_Menu.this.dispose();
        }
        if (e.getSource() == delete_button) {
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
                RunClient_and_Methods.execute_query(message);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            model.removeElement(selected);
        }
    }

}
