import javax.swing.*;
import java.util.ArrayList;

public class Main extends JFrame {
    private ArrayList<JButton> buttons;


    public Main() {
        JFrame frame = new JFrame("Mill");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        JPanel boardPanel = new JPanel();
        frame.getContentPane().add(boardPanel);
        frame.setVisible(true);
        // create the main 3x3 board
        buttons = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            JButton button = new JButton();
            buttons.add(button);
            boardPanel.add(button);
        }
        // create the 3x3 grid in the middle
        for (int i = 9; i < 18; i++) {
            JButton button = new JButton();
            buttons.add(button);
            boardPanel.add(button);
        }
        // create the 3x3 grid in the middle of the 3x3 grid
        for (int i = 18; i < 24; i++) {
            JButton button = new JButton();
            buttons.add(button);
            boardPanel.add(button);
        }
        // set the layout and add the board panel to the JFrame
        boardPanel.setLayout(null);
        getContentPane().add(boardPanel);
        // set the JFrame properties
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 800);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
}