package io.github.birdsong.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InputBox {
    private JPanel panel;
    private JFrame inputBox;
    private JTextField textField;
    private JButton submitButton;
    private boolean pressed;
    private String inputText;

    public InputBox() {
        panel = new JPanel();
        pressed = false;
        inputBox = new JFrame("Audio Directory");
        submitButton = new JButton("Submit");
        textField = new JTextField("");
        textField.setPreferredSize(new Dimension(190, 28));

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buttonPressed();
            }
        });

        panel.add(textField);
        panel.add(submitButton);

        inputBox.add(panel);
        inputBox.setSize(300, 74);
        inputBox.setVisible(true);
        inputBox.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        inputBox.setLocationRelativeTo(null);
    }

    public void buttonPressed() {
        if (!pressed) {
            pressed = true;
            inputText = textField.getText();
        }
    }

    public String getText() {
        if (inputText != null) {
            dispose();
            return inputText;
        } else {
            return null;
        }
    }

    public void dispose() {
        inputBox.dispose();
    }
}
