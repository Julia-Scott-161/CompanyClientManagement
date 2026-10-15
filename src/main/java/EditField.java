import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

/**
 * handles the fields which you can edit/save
 * handles the actual saving portion of it, meaning
 * it updates the database/UI when you hit save
 * @author Julia Scott
 */

public class EditField extends JPanel implements ActionListener {
    //declares different field options
    static enum FieldType {
        TEXT,
        BOOLEAN,
        DROPDOWN
    }

    private Object originalValue;
        private FieldType type;
        private String label;

        private Object value;

        private CardLayout layout = new CardLayout();
        private JPanel cards = new JPanel(layout);

        private JLabel viewLabel;

        private JTextArea textArea;
        private JCheckBox checkBox;
        private JComboBox<String> comboBox;

        private JButton edit_button = new JButton("Edit");
        private JButton save_button = new JButton("Save");
        private String columnName;
        private int clientId;


    public EditField(int clientId, String columnName, String label, FieldType type, Object initialValue, String... options) {
            this.clientId = clientId;
            this.columnName = columnName;
            this.label = label;
            this.type = type;
            this.value = initialValue;
            this.originalValue = initialValue;

            setLayout(new BorderLayout());
            setBorder(BorderFactory.createTitledBorder(label));

            viewLabel = new JLabel();

            switch (type) {

                case TEXT -> {
                    textArea = new JTextArea((String) initialValue);
                    textArea.setLineWrap(true);
                    textArea.setWrapStyleWord(true);

                    cards.add(new JScrollPane(textArea), "EDIT");
                }

                case BOOLEAN -> {
                    checkBox = new JCheckBox(label);
                    checkBox.setSelected(Boolean.TRUE.equals(initialValue));

                    cards.add(checkBox, "EDIT");
                }

                case DROPDOWN -> {
                    comboBox = new JComboBox<>(options);
                    if (initialValue != null) {
                        comboBox.setSelectedItem(initialValue.toString());
                    }

                    cards.add(comboBox, "EDIT");
                }
            }

            updateViewLabel();

            cards.add(viewLabel, "VIEW");
            layout.show(cards, "VIEW");

            JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttons.add(edit_button);
            buttons.add(save_button);

            edit_button.addActionListener(this);
            save_button.addActionListener(this);

            add(cards, BorderLayout.CENTER);
            add(buttons, BorderLayout.SOUTH);
        }

    private String formatForDB(Object v) {
        if (v == null) return "NULL";

        if (type == FieldType.BOOLEAN) {
            return ((Boolean) v) ? "1" : "0";
        }

        return RunClientAndMethods.clean(v.toString());
    }

    //TODO: can remove table parameter, as EditFields only currently implemented within CLIENT
    public String getUpdateString(String tableName, String columnName) {

        String newVal = formatForDB(value);

        // WHERE Client_ID = <this client>, so only this client's row changes. (Matching on the old
        // value instead would also update every other client that has the same old value.)
        return "UPDATE:" + "CLIENT" + ":Client_ID:" + clientId + ":" + columnName + ":" + newVal;
    }

    //check if value has changed
    private boolean saveValue() {
        Object old = value;

        switch (type) {
            case TEXT -> value = textArea.getText();
            case BOOLEAN -> value = checkBox.isSelected();
            case DROPDOWN -> value = comboBox.getSelectedItem();
        }

        return !java.util.Objects.equals(old, value);
    }

//---------------VIEW LABEL-----------------//
        private void updateViewLabel() {
            viewLabel.setText(format(value));
        }

        private String format(Object v) {
            if (v == null) return label + ": ";

            if (type == FieldType.BOOLEAN) {
                return label + ": " + ((Boolean) v ? "Yes" : "No");
            }

            return label + ": " + v.toString();
    }

        public Object getValue() {
            return value;
        }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == save_button) {
            boolean changed = saveValue();

            if (changed) {
                String update = getUpdateString("CLIENT", columnName);
                System.out.println(update);
                originalValue = value;

                try {
                    RunClientAndMethods.executeQuery(update);

                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Couldn't save " + label + ": " + ex.getMessage());
                }
            }

            updateViewLabel();
            layout.show(cards, "VIEW");
        }
        if (e.getSource() == edit_button) {
            layout.show(cards, "EDIT");
        }
    }
}

