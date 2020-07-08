import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.*;

public class Resize extends JDialog{

    int size = 40;

    public Resize(JFrame frame){
        super(frame, "Resize", true);
        setLayout(new FlowLayout());

        JTextField newSize = new JTextField(5);
        add(newSize);

        JButton button = new JButton("Resize");
        add(button);

        JLabel label = new JLabel("Select a size between 1 and 100");
        add(label);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                size = Integer.parseInt(newSize.getText());
            }
        });
    }

    public int returnNewSize(){

        return size;
    }
}
