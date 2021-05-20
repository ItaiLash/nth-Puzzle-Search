import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * The main program:
 * reads from the input file written in the following format:
 *
 */
public class Ex1 {

    public static void main(String[] args)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, FileNotFoundException {

        Ex1 mainProg = new Ex1("input.txt");
            mainProg.run();

    }

    String selectedAlgo;
    boolean withTime = false;
    boolean withOpen = false;
    int numOfRows;
    int numOfCols;
    State startState;
    State goalState;
    Scanner scanner;

    public Ex1(String path) throws FileNotFoundException {
        File file = new File(path);
        scanner = new Scanner(file);
    }

    public void run() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        read();
        Algo algo = new Algo(selectedAlgo, withOpen, withTime);
        algo.run(startState, goalState);
    }

    public void read(){
        readAlgo();
        readOpen();
        readTime();
        readDimensions();
        int[] startArr = readStartState();
        scanner.nextLine();
        int[] goalArr = readGoalState();
        startState = new PuzzleState(startArr, numOfRows, numOfCols, count(startArr, 0),0, goalArr);
        goalState = new PuzzleState(goalArr, numOfRows, numOfCols, count(startArr, 0),0, goalArr);
    }

    //First line in input.txt: Selected Algo
    private void readAlgo(){
        selectedAlgo = scanner.nextLine();
        selectedAlgo = fixAlgoName(selectedAlgo);
    }

    //Second line in input.txt: With/no time
    private void readOpen(){
        if (scanner.nextLine().contains("with")) {
            withTime = true;
        }
    }

    //Third line in input.txt: With/no open
    private void readTime(){
        if (scanner.nextLine().contains("with")) {
            withOpen = true;
        }
    }

    //Fourth line in input.txt: nxm
    private void readDimensions(){
        String[] nXm = scanner.nextLine().split("x");
        numOfRows = Integer.parseInt(nXm[0]);
        numOfCols = Integer.parseInt(nXm[1]);
    }

    //From the fifth row to the fifth row + numOfRows number in input.txt: Start State
    private int[] readStartState() {
        StringBuilder start = new StringBuilder();
        for (int i = 0; i < numOfRows; i++) {
            start.append(scanner.nextLine()).append(",");
        }
        return string2Arr(start.toString());
    }

    private int[] readGoalState() {
        StringBuilder goal = new StringBuilder();
        for (int i = 0; i < numOfRows; i++) {
            goal.append(scanner.nextLine()).append(",");
        }
        return string2Arr(goal.toString());
    }


    /**
     * Receives a string that represents an array and returns the appropriate array
     * @param s - A string that represents an array
     * @return an array
     */
    private int[] string2Arr(String s){
        List<String> arr = Arrays.asList(fixString(s).split(",").clone());
        Collections.replaceAll(arr,"_","0");
        int[] board = new int[arr.size()];
        for(int i=0 ; i<board.length ; i++){
            board[i] = Integer.parseInt(arr.get(i));
        }
        return board;
    }

    /**
     * Arranges the string obtained from the input file
     * for example:
     * "1,2,3
     *  4,5,6    -->  "1,2,3,4,5,6,7,8,_"
     *  7,8,_"
     * @param s - String as obtained from the input file
     * @return the fixed String
     */
    private String fixString(String s) {
        StringBuilder fixed = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\n') {
                fixed.append(c);
            } else {
                fixed.append(',');
            }
        }
        return fixed.toString();
    }

    /**
     * @param arr - An integer array
     * @param num - A integer
     * @return the number of times that num appears in the array
     */
    private int count(int[] arr, int num){
        int counter = 0;
        for (int j : arr) {
            if (j == num) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Since there are several ways to write the names of the different algorithms,
     * the method will return a particular name to each algorithm
     * @param algo - The string obtained from the input file
     * @return the appropriate name for the algorithm
     */
    private String fixAlgoName(String algo){
        switch (algo) {
            case "bfs" :
            case "Bfs":
                return "BFS";
            case "dfid" :
            case "Dfid":
                return "DFID";
            case "A*" :
                return "AStar";
            case "IDA*" :
                return "IDAStar";
            case "DFBNB" :
            case "DFbnb" :
            case "dfbnb" :
                return "DFBnB";
            default : return algo;
        }
    }
}
