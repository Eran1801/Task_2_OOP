package gameClient;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Ex2 {

    private static LoginGUI loginGUI;

    public static void main(String[] args) {

        // This is for the cmd operation
        if (args.length == 2) {
            int ID = Integer.parseInt(args[0]);
            int levelNumber = Integer.parseInt(args[1]);
            Game_Manager gameManager = new Game_Manager();
            gameManager.setGameData(ID, levelNumber);
            new Thread(gameManager).start();
        } else {
            loginGUI = new LoginGUI();
        }
    }
}

class LoginGUI extends JFrame {

    private JLabel userIdLable;
    private JTextField userIdText;
    private JLabel levelNumberLable;
    private JTextField levelNumberText;
    private JButton buttonLogin;
    private JFrame thisGUI;

    public LoginGUI() {
        initLoginGUIFrame();
        initLoginGUIPanel();
        this.setVisible(true);
        this.thisGUI = this;
    }

    /**
     * This method takes care of the frame
     */
    private void initLoginGUIFrame() {

        // Set Title
        this.setTitle("Login - Pokemon Game");

        // set the size of the GUI
        this.setSize(320, 170);

        //Exit the program when clicking the X button in the top of the JFrame
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.setLocationRelativeTo(null); // make the GUI show in the middle
        ImageIcon icon = new ImageIcon("src/gameClient/pic/pikachu.png");
        this.setIconImage(icon.getImage());
    }

    /**
     * This method takes care of the panel
     */
    private void initLoginGUIPanel() {

        //create new panel
        JPanel panel = new JPanel();

        this.add(panel);

        // now lets work on the panel
        panel.setLayout(null);

        // JLabel is used to display a short string or an image icon. JLabel can display text, image or both
        userIdLable = new JLabel("ID:");
        userIdLable.setBounds(10, 20, 100, 25);
        //adding the label to the panel
        panel.add(userIdLable);

        levelNumberLable = new JLabel("Level Number:");
        levelNumberLable.setBounds(10, 50, 100, 25);
        //adding the label to the panel
        panel.add(levelNumberLable);


        userIdText = new JTextField();
        userIdText.addKeyListener(new KeyAdapter() { // limits the Id number up to 9
            @Override
            public void keyTyped(KeyEvent e) { // taking care that the user id will be max 9 numbers
                if (e.getKeyChar() != KeyEvent.VK_ENTER) {
                    if (userIdText.getText().length() >= 9)
                        e.consume();
                } else {
                    validateData();
                }
            }
        });
        userIdText.setBounds(120, 20, 165, 25);
        panel.add(userIdText);

        levelNumberText = new JTextField();

        levelNumberText.setBounds(120, 50, 165, 25);
        levelNumberText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    validateData();
                }
            }
        });
        panel.add(levelNumberText);

        buttonLogin = new JButton("START GAME");
        buttonLogin.setBounds(100, 90, 120, 25);
        buttonLogin.addActionListener(new ActionListener() {

            // this method start to work when you press the 'buttonLogin' button
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == buttonLogin) { // the source is the object that happens on this action
                    validateData();
                }
            }
        });
        panel.add(buttonLogin);
    }

    /**
     * This method checks if the data that enter to the LoginGUI is valid and set the game
     */
    private void validateData() {
        if (userIdText.getText().length() == 9 && levelNumberText.getText().length() != 0) {
            Game_Manager gameManager = new Game_Manager();
            gameManager.setGameData(Integer.parseInt(userIdText.getText()), Integer.parseInt(levelNumberText.getText()));
            new Thread(gameManager).start();
            thisGUI.dispose();
        }
    }
}
