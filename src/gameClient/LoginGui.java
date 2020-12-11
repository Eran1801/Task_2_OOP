package gameClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginGui implements ActionListener {

    private static JLabel userIdLable;
    private static JTextField userIdText;
    private static JLabel levelNumberLable;
    private static JTextField levelNumberText;
    private static JButton buttonLogin;

    public static void main(String[] args) {

        // The frame of your GUI
        JFrame frame = new JFrame("Login Game");

        // The panel is the second layout, the frame is the window
        JPanel panel = new JPanel();

        // set the size of your GUI
        frame.setSize(350, 200);

        //It causes the application to exit when the application receives a
        // close window event from the operating system. Pressing the close (X)
        // button on your window causes the operating system to
        // generate a close window event and send it to your Java application
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // we need to put the panel on the frame
        frame.add(panel);

        // now lets work on the panel
        panel.setLayout(null);

        // JLabel is used to display a short string or an image icon. JLabel can display text, image or both
        userIdLable = new JLabel("Enter your id");
        userIdLable.setBounds(10, 20, 80, 25);
        //adding the label to the panel
        panel.add(userIdLable);

        levelNumberLable = new JLabel("Level number");
        levelNumberLable.setBounds(10, 50, 100, 25);
        //adding the label to the panel
        panel.add(levelNumberLable);


        userIdText = new JTextField();
        userIdText.addKeyListener(new KeyAdapter() { // limits the Id number up to 9
            @Override
            public void keyTyped(KeyEvent e) {
                if (userIdText.getText().length() >= 9)
                    e.consume();
            }
        });
        userIdText.setBounds(120, 20, 165, 25);
        panel.add(userIdText);

        levelNumberText = new JTextField(9);
        levelNumberText.addKeyListener(new KeyAdapter() { // limits the level number number up to 2
            @Override
            public void keyTyped(KeyEvent e) {
                if (levelNumberText.getText().length() >= 2)
                    e.consume();
            }
        });
        levelNumberText.setBounds(120, 50, 165, 25);
        panel.add(levelNumberText);


        buttonLogin = new JButton("START GAME");
        buttonLogin.setBounds(10, 90, 110, 25);
        buttonLogin.addActionListener(new LoginGui()); // To use the actionListener you need to implement the
        panel.add(buttonLogin);
        frame.setLocationRelativeTo(null); // make thr GUI shows in the middle
        ImageIcon icon = new ImageIcon("src/gameClient/pic/pikachu.png");
        frame.setIconImage(icon.getImage());



        //The setVisible(true) method makes the frame appear on the screen. If you forget to do this,
        //the frame object will exist as an object in memory, but no picture will appear on the screen.
        frame.setVisible(true);


    }


    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO : to set a listener to the button start game to the graph
    }
}
