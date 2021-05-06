import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Ex1 {

    public static void main(String[] args)
            throws FileNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String selectedAlgo;
        boolean withTime = false;
        boolean withOpen = false;
        int numOfRows;
        int numOfCols;
        State startState;
        State goalState;


        File file = new File("/Users/itailash/Documents/tttt/nth-Puzzle-Search/src/input2.txt");

        Scanner sc = new Scanner(file);

        //First line in input.txt: Selected Algo
        selectedAlgo = sc.nextLine();
        selectedAlgo = fixAlgoName(selectedAlgo);
        //Second line in input.txt: With/no time
        if (sc.nextLine().contains("with")) {
            withTime = true;
        }

        //Third line in input.txt: With/no open
        if (sc.nextLine().contains("with")) {
            withOpen = true;
        }

        //Fourth line in input.txt: nxm
        String[] nXm = sc.nextLine().split("x");
        numOfRows = Integer.parseInt(nXm[0]);
        numOfCols = Integer.parseInt(nXm[1]);

        //From the fifth row to the fifth row + numOfRows number in input.txt: Start State
        StringBuilder start = new StringBuilder();
        for (int i = 0; i < numOfRows; i++) {
            start.append(sc.nextLine()).append(",");
        }
        int[] startArr = string2Arr(start.toString());

        sc.nextLine();
        StringBuilder goal = new StringBuilder();
        for (int i = 0; i < numOfRows; i++) {
            goal.append(sc.nextLine()).append(",");
        }
        int[] goalArr = string2Arr(goal.toString());
//        System.out.println(Arrays.toString(startArr));
//        System.out.println(numOfRows);
//        System.out.println(numOfCols);
//        System.out.println(count(startArr, 0));
//        System.out.println(goalArr);
        startState = new PuzzleState(startArr, numOfRows, numOfCols, count(startArr, 0),0, goalArr);
        goalState = new PuzzleState(goalArr, numOfRows, numOfCols, count(startArr, 0),0, goalArr);

        Algo algo = new Algo(selectedAlgo, withOpen, withTime);
        algo.run(startState, goalState);


    }

    public static int[] string2Arr(String s){
        List<String> arr = Arrays.asList(fixString(s).split(",").clone());
        Collections.replaceAll(arr,"_","0");
        int[] board = new int[arr.size()];
        for(int i=0 ; i<board.length ; i++){
            board[i] = Integer.parseInt(arr.get(i));
        }
        return board;
    }

    private static String fixString(String s) {
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

    public static int count(int[] arr, int num){
        int counter = 0;
        for (int j : arr) {
            if (j == num) {
                counter++;
            }
        }
        return counter;
    }

    public static String fixAlgoName(String algo){
        return switch (algo) {
            case "A*" -> "AStar";
            case "IDA*" -> "IDAStar";
            case "DFbnb", "DFBNB", "dfbnb" -> "DFBnB";
            default -> algo;
        };
    }
}
