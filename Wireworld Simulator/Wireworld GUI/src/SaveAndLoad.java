import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.lang.*;
import java.util.Scanner;

public class SaveAndLoad extends JDialog {

    int loadAutomataSize = 0;
    int[] loadAutomataCells;

    public SaveAndLoad(JFrame frame, int[][] boardState, int boardSize){

        super(frame, "Save and Load", true);
        setLayout(new FlowLayout());

        JTextField saveText = new JTextField(30);
        add(saveText);

        JButton saveButton = new JButton("Save");
        add(saveButton);

        JTextField loadText = new JTextField(30);
        add(loadText);

        JButton loadButton = new JButton("Load");
        add(loadButton);


        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String filePath = saveText.getText();
                    File file = new File(filePath);
                    if (!file.exists()) file.createNewFile();
                    PrintWriter writer = new PrintWriter(file);


                    for(int i = 0; i < boardSize; i++){
                        for(int j = 0; j < boardSize; j++){
                            if(j == 0 && !(i==0)) writer.print("\n");
                            writer.print(boardState[i][j]);
                        }
                    }

                    writer.close();
                }catch(Exception e1){
                    System.out.println(e1);
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    String filePath = loadText.getText();
                    File file = new File(filePath);

                    FileInputStream fileStream = new FileInputStream(file);
                    InputStreamReader input = new InputStreamReader(fileStream);
                    BufferedReader reader = new BufferedReader(input);

                    String line = reader.readLine();
                    if(line != null){
                        loadAutomataSize = line.length();
                    }

                    Scanner scanner = new Scanner(file);
                    String loadAutomataAsString = "";
                    loadAutomataAsString = scanner.nextLine();
                    while (scanner.hasNextLine()) {
                        loadAutomataAsString = loadAutomataAsString + scanner.nextLine();
                    }

                    int[] tempCells = new int[loadAutomataSize*loadAutomataSize];
                    for(int i = 0; i < loadAutomataSize * loadAutomataSize; i++){
                        tempCells[i] = Character.getNumericValue(loadAutomataAsString.charAt(i));
                    }
                    loadAutomataCells = tempCells;

                }catch (Exception e2){
                    System.out.println(e2);
                }

            }
        });
    }

    public int getLoadAutomataSize(){
        return loadAutomataSize;
    }

    public int[] getLoadAutomataCells(){
        return loadAutomataCells;
    }
}
