import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class GUI extends JFrame {

    int size = 40;
    boolean isPaused = false;


    Wireworld wireworld = new Wireworld(size, 2000);

    JPanel panel = new JPanel();
    Cell[] cells = new Cell[(wireworld.size) * (wireworld.size)];

    public static void main(String[] args) {
        try {
            new GUI();
        } catch (InterruptedException e) {
            System.out.println("oopsie");
        }
    }

    public GUI() throws InterruptedException {
        super("Wireworld");
        setSize(1600, 900);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        panel.setLayout(new GridLayout(wireworld.size, wireworld.size));
        add(panel);


        for (int i = 0; i < (wireworld.size) * (wireworld.size); i++) {
            double cellSize = 1.00/(double)size * 800.00;
            cells[i] = new Cell((int)cellSize);
            panel.add(cells[i]);
        }


        Container c = getContentPane();
        c.setLayout(new FlowLayout());

        JButton execute = new JButton("Execute");
        execute.setPreferredSize(new Dimension(100, 50));
        add(execute);

        JButton pause = new JButton("Stop");
        pause.setPreferredSize(new Dimension(100, 50));
        add(pause);

        JButton saveLoad = new JButton("Save/Load");
        saveLoad.setPreferredSize(new Dimension(100, 50));
        add(saveLoad);

        JButton resizeButton = new JButton("Resize");
        resizeButton.setPreferredSize(new Dimension(100, 50));
        add(resizeButton);

        execute.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                //initialise wireworld instance's board in setup of GUI at time of execute

                for (int a = 0; a < wireworld.size; a++) {
                    int[] rowContents = new int[wireworld.size];
                    for (int b = 0; b < wireworld.size; b++) {
                        rowContents[b] = cells[b + (a * wireworld.size)].getValue();
                    }
                    wireworld.setBoardRow(a, rowContents);
                }

                //An infinite loop that generates the next board then draws it to GUI


                wireworld.generateNextBoard();
                for (int x = 0; x < wireworld.size; x++) {
                    for (int y = 0; y < wireworld.size; y++) {
                        cells[(x * wireworld.size) + y].setCellType(wireworld.getValue(x, y));
                    }
                }

                wireworld.printBoard();

                if(isPaused == false) {
                    Timer timer = new Timer(500, this::actionPerformed);
                    timer.setRepeats(false);
                    timer.start();
                }

                isPaused = false;

            }

        });

        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                    isPaused = true;
            }
        });

        saveLoad.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int oldSize = size;

                SaveAndLoad saveAndLoad = new SaveAndLoad(GUI.this, wireworld.board, wireworld.size);
                saveAndLoad.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                saveAndLoad.setSize(400, 400);
                saveAndLoad.setLocation(800, 250);
                saveAndLoad.setVisible(true);


                //if something is to be loaded, load
                if (saveAndLoad.getLoadAutomataSize() != 0){

                    size = saveAndLoad.getLoadAutomataSize();

                    //clear old cells
                    for(int i = 0; i < oldSize*oldSize; i++) {
                        panel.remove(cells[i]);
                    }

                    //setup new Wireworld
                    wireworld.resize(saveAndLoad.getLoadAutomataSize());
                    panel.setLayout(new GridLayout(wireworld.size, wireworld.size));

                    isPaused = true;

                    //add a new cell GUI (All black cells)
                    Cell[] newCells = new Cell[(wireworld.size) * (wireworld.size)];
                    for (int i = 0; i < (wireworld.size) * (wireworld.size); i++) {
                        double cellSize = 1.00/(double)size * 800.00;
                        newCells[i] = new Cell((int)cellSize);
                        panel.add(newCells[i]);
                    }
                    cells = newCells;

                    //set every cell to the state from the load file
                    for(int i = 0; i < wireworld.size*wireworld.size; i++){
                        cells[i].setCellType(saveAndLoad.loadAutomataCells[i]);
                    }


                    revalidate();
                    repaint();
                }

            }
        });

        resizeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int oldSize = size;
                Resize resize = new Resize(GUI.this);
                resize.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                resize.setSize(300, 100);
                resize.setLocation(800, 250);
                resize.setVisible(true);

                size = resize.returnNewSize();
                System.out.println("size is " + size);


                //clear old cells
                for(int i = 0; i < oldSize*oldSize; i++) {
                    panel.remove(cells[i]);
                }


                //setup new Wireworld
                wireworld.resize(size);

                panel.setLayout(new GridLayout(wireworld.size, wireworld.size));

                isPaused = true;

                //add new cells to gui
                Cell[] newCells = new Cell[(wireworld.size) * (wireworld.size)];
                for (int i = 0; i < (wireworld.size) * (wireworld.size); i++) {
                    double cellSize = 1.00/(double)size * 800.00;
                    newCells[i] = new Cell((int)cellSize);
                    panel.add(newCells[i]);
                }

                cells = newCells;

                revalidate();
                repaint();

            }
        });

        setVisible(true);
    }
}

