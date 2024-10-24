package com.ladi;
import javax.swing.*;

public class MalomProgram extends JFrame {
   public MalomProgram() {
        setTitle("MalomProgram");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null);
        MalmoPanel malomPanel = new MalmoPanel();
        //malomPanel.setBackground(Color.BLACK);
        add(malomPanel);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MalomProgram().setVisible(true));
    }
}