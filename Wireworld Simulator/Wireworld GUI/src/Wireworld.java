public class Wireworld {

    int size = 0;
    int[][] board;
    int timeInterval = 0;

    //default constructor; size 50, timeInterval = 500
    public Wireworld(){
        this.size = 50;
        this.board = new int[size][size];
        this.timeInterval = 500;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    public void resize(int size){
        this.size = size;
        this.board = new int[size][size];
        this.timeInterval = 500;

        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    //constructor w/o timeInterval; timeInterval = 500
    public Wireworld(int size){
        this.size = size;
        board = new int[size][size];

        this.timeInterval = 500;


        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    //constructor with all args
    public Wireworld(int size, int timeInterval){
        this.size = size;
        board = new int[size][size];
        this.timeInterval = timeInterval;


        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++) {
                board[i][j] = 0;
            }
        }
    }

    //prints the 2D array Board;
    public void printBoard(){
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                System.out.print(board[i][j]);
                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    public void generateNextBoard(){
        int[][] boardClone = new int[size][size];

        for(int i =0; i < size; i++){
            for(int j = 0; j<size; j++){
                boardClone[i][j] = board[i][j];
            }
        }



        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++) {

                //Wireworld rules for empty, electron head and electron tail.
                if(boardClone[i][j] == 0){
                    board[i][j] = 0;
                }
                else if(boardClone[i][j] == 1){
                    board[i][j] = 2;
                }
                else if(boardClone[i][j] == 2){
                    board[i][j] = 3;
                }
                else if(boardClone[i][j] == 3){
                    calculateConductor(boardClone, i, j);
                }
            }
        }

    }

    public void calculateConductor(int[][] boardClone, int i, int j){
        int[] mooreNeighbourhood= new int[8];

        //If a cell is on the edge or corner, unaccounted for elements in moore neighbourhood set to 0.
        try {
            mooreNeighbourhood[0] = boardClone[i - 1][j - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[0] = 0;
        }
        try {
            mooreNeighbourhood[1] = boardClone[i - 1][j];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[1] = 0;
        }
        try {
            mooreNeighbourhood[2] = boardClone[i - 1][j + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[2] = 0;
        }
        try {
            mooreNeighbourhood[3] = boardClone[i][j-1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[3] = 0;
        }
        try {
            mooreNeighbourhood[4] = boardClone[i][j + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[4] = 0;
        }
        try {
            mooreNeighbourhood[5] = boardClone[i + 1][j - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[5] = 0;
        }
        try {
            mooreNeighbourhood[6] = boardClone[i + 1][j];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[6] = 0;
        }
        try {
            mooreNeighbourhood[7] = boardClone[i + 1][j + 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            mooreNeighbourhood[7] = 0;
        }



        int electronHeadCounter = 0;

        for (int c = 0; c < 8; c++){
            if (mooreNeighbourhood[c] == 1){
                electronHeadCounter++;
            }
        }

        if(electronHeadCounter == 1 || electronHeadCounter == 2){
            board[i][j] = 1;
        }else{
            board[i][j] = 3;
        }
    }

    public void setBoardRow(int rowNum, int[] values){
        for (int j = 0; j < size; j++){
            board[rowNum][j] = values[j];
        }
    }

    public int getValue(int i, int j){
        return board[i][j];
    }

    //dunno if ill ever use this, probs not
    public void clearConsole(){
        String newLines = "";
        for (int i = 0; i<size; i++){
            newLines += "\r\n";
        }
        System.out.println(newLines);
    }
}


