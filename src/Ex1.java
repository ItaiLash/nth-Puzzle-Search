import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Ex1 {

    public static void main(String[] args) throws FileNotFoundException {
        String selectedAlgo = "";
        boolean withTime = false;
        boolean withOpen = false;
        int numOfRows = 0;
        int numOfCols = 0;
        int numOfEmptyBlocks = 0;
        State startState;
        State goalState;


        File file = new File("../src/input.txt");

        Scanner sc = new Scanner(file);

        //First line in input.txt: Selected Algo
        selectedAlgo = sc.nextLine();

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
        String start = "";
        for (int i = 0; i < numOfRows; i++) {
            start += sc.nextLine();
        }
        int[] startArr = string2Arr(start);

        sc.nextLine();
        String goal = "";
        for (int i = 0; i < numOfRows; i++) {
            goal += sc.nextLine();
        }
        int[] goalArr = string2Arr(goal);

        startState = new PuzzleState(startArr, numOfRows, numOfCols, count(startArr, 0),0, goalArr);
        goalState = new PuzzleState(goalArr, numOfRows, numOfCols, count(startArr, 0),0, goalArr);


    }

    public static int[] string2Arr(String s){
        List<String> arr = Arrays.asList(fixString(s).split(",").clone());
        Collections.replaceAll(arr,"_","0");
        int[] board = new int[arr.size()];
        for(int i=0 ; i<board.length ; i++){
            board[i] = Integer.parseInt(arr.get(i));
            if(board[i] == 0){
            }
        }
        return board;
    }

    private static String fixString(String s) {
        String fixed = "";
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c != '\n') {
                fixed += c;
            } else {
                fixed += ',';
            }
        }
        return fixed;
    }

    public static int count(int[] arr, int num){
        int counter = 0;
        for(int i=0 ; i<arr.length ; i++){
            if(arr[i] == num){
                counter++;
            }
        }
        return counter;
    }
}
