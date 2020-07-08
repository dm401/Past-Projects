import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


/**
 * WHERE AS IN THE REST OF THE PROJECT '0,1,2,3' refer to empty, ehead, etail and conductor respectively,
 * this class assigns '0,1,2,3' to empty, conductor, ehead and etail to make the user cycling to conductor(the
 * most commonly used cell type) quicker.
 */

public class Cell extends JButton implements ActionListener {
    ImageIcon empty, eHead, eTail, conductor;
    int cellType = 0;


    public Cell(int size){

        setPreferredSize(new Dimension(size, size));

        empty = new ImageIcon("images/empty.png");
        eHead = new ImageIcon("images/head.png");
        eTail = new ImageIcon("images/tail.png");
        conductor = new ImageIcon("images/conductor.png");

        addActionListener(this);

        setIcon(empty);
    }

    public void actionPerformed(ActionEvent e){
        cellType++;
        cellType = cellType % 4;
        switch(cellType){
            case 1:
                setIcon(conductor);
                break;
            case 2:
                setIcon(eHead);
                break;
            case 3:
                setIcon(eTail);
                break;
            default:
                setIcon(empty);
                break;
        }
    }

    public int getValue(){
        switch(cellType){
            case 0:
                return 0;
            case 1:
                return 3;
            case 2:
                return 1;
            default:
                return 2;
        }
    }

    public void setCellType(int i){
        switch(i){
            case 0:
                setIcon(empty);
                cellType = 0;
                break;
            case 1:
                setIcon(eHead);
                cellType = 2;
                break;
            case 2:
                setIcon(eTail);
                cellType = 3;
                break;
            case 3:
                setIcon(conductor);
                cellType = 1;
                break;
        }
    }
}
