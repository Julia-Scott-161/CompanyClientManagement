import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * to show working buttons
 * @author Julia Scott
 */
public class Temp_Screen extends JFrame implements ActionListener {
    JButton back = new JButton("Go Back to Home Menu");
    JLabel title = new JLabel("You have reached an Unimplemented Interface");
    Temp_Screen() {
        JFrame frame = new JFrame();
        this.setSize(300,300);
        this.setLayout(new FlowLayout(FlowLayout.CENTER,500,50));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); //application cannot be closed by X
        this.setLocationRelativeTo(null);
        this.add(title);
        this.add(back);
        this.setVisible(true);
        back.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == back) {
            EventQueue.invokeLater(RunClient_and_Methods::home_menu_screen);
            this.dispose();
        }
    }
}
