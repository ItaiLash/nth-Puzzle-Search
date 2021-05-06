import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * PuzzleState defines a state for the nth-puzzle problem. The board is always
 * represented by a single dimensioned array.
 * In terms of the actual tiles, '0' represents the hole in the board, and 0 is
 * treated special when generating successors.
 *
 * @author Itai Lashover
 */
public class PuzzleState implements State, Comparable<State> {

    private static int uniqueKey = 0;

    private int id;
    private int[] curBoard;
    private int puzzleSize;
    private int numOfRows;
    private int numOfCols;
    private int numOfEmptyBlocks;
    private int cost;
    private Boolean out = false;
    private PuzzleState pre = null;
    private String path = "";

    private PuzzleStateAlgo psa;

    public int gn;
    public int fn;
    public int hn;

    /**
     *Constructor for PuzzleState
     * @param board       - An array that represent the puzzle state
     * @param n           - Number of rows in the Puzzle
     * @param m           - Number of column in the Puzzle
     * @param emptyBlocks - Number of empty blocks in the Puzzle
     * @param cost        - The total cost to reach this state
     */
    public PuzzleState(int[] board, int n, int m, int emptyBlocks, int cost, int[] goal) {
        this.id = uniqueKey++;
        this.numOfRows = n;
        this.numOfCols = m;
        this.puzzleSize = n*m;
        this.numOfEmptyBlocks = emptyBlocks;
        this.curBoard = board;
        this.cost = cost;
        this.psa = new PuzzleStateAlgo(this,goal);
        gn = cost;
        hn = psa.manhattanDistance();
        fn = getCost();
    }

    private PuzzleState(PuzzleState pre, int[] board, int cost){
        this.id = uniqueKey++;
        this.numOfRows = pre.numOfRows;
        this.numOfCols = pre.numOfCols;
        this.puzzleSize = pre.puzzleSize;
        this.numOfEmptyBlocks = pre.numOfEmptyBlocks;
        this.curBoard = board;
        this.cost = cost;
        this.psa = new PuzzleStateAlgo(this,pre.psa.getGoal());
        gn = cost;
        hn = psa.manhattanDistance();
        fn = getCost();
    }



    /**
     * Attempt to locate the "0" spot on the current board
     * @return the index of the "hole" (or 0 spot)
     */
    private int getHole() {
        for (int i = 0; i < puzzleSize; i++) {
            if (curBoard[i] == 0)
                return i;
        }
        return -1;
    }

    /**
     * Attempt to locate the "0" spots on the current board
     * @return the a two dimension array with the indexes of the two "hole" (or 0 spot)
     */
    private int[] getHoles() {
        int holes[] = new int[2];
        int index = 0;
        for (int i = 0; i < puzzleSize; i++) {
            if (curBoard[i] == 0)
                holes[index++] = i;
        }
        return holes;
    }

    /**
     * Attempt to locate the "0" spots on the current board
     * @return 1 if they are horizontally adjacent,
     *         2 if they are vertically adjacent and 0 otherwise
     */
    private int getHolesState() {
        int[] holes = getHoles();
        if(holes[0]+numOfRows == holes[1]) {        //horizontal
            return 1;
        }
        else if(holes[0] + 1 == holes[1] && holes[0]/numOfRows == holes[1]/numOfRows){      //vertical
            return 2;
        }
        else{
            return 0;
        }
    }

    /**
     * States copy
     * @param state - The State that need to be copied
     * @return a copy of the state
     */
    private int[] copyBoard(int[] state) {
        int[] ret = new int[puzzleSize];
        for (int i = 0; i < puzzleSize; i++) {
            ret[i] = state[i];
        }
        return ret;
    }

    /**
     * Depending on the number of empty blocks the method will return all States that can be reached.
     * In addition the method will update the pre of each new state to be the current state.
     * @return an ArrayList containing all of the successors for the current state
     */
    @Override
    public ArrayList<State> genSuccessors() {
        ArrayList<State> successors = new ArrayList<State>();
        if (numOfEmptyBlocks == 1) {
            int hole = getHole();
            genSuccessors1(successors, hole);
        }
        else if (numOfEmptyBlocks == 2) {
            int holes[] = getHoles();
            genSuccessors2(successors, holes);
            genSuccessors1(successors, holes[0]);
            genSuccessors1(successors, holes[1]);
        }
        for (State suc : successors){
            suc.setPre(this);
        }
        return successors;
    }

    public void genSuccessors1(ArrayList<State> successors, int hole) {
        left(successors, hole);
        up(successors, hole);
        right(successors, hole);
        down(successors, hole);
    }

    public void genSuccessors2(ArrayList<State> successors, int holes[]) {
        int holesState = getHolesState();
        if(holesState == 1) {
            twoLeft(successors, holes);
        }
        if (holesState == 2) {
            twoUp(successors, holes);
        }
        if(holesState == 1) {
            twoRight(successors, holes);
        }
        if (holesState == 2) {
            twoDown(successors, holes);
        }
    }

    private void right(ArrayList<State> successors ,int hole) {
        if (hole % numOfCols != 0) {
            String path = ""+curBoard[hole-1]+"R";
            swapAndStore(hole - 1, hole, cost + 5, successors, path);
        }
    }

    private void left(ArrayList<State> successors ,int hole) {
        if (hole % numOfCols != numOfCols-1) {
            String path = ""+curBoard[hole+1]+"L";
            swapAndStore(hole + 1, hole, cost + 5, successors, path);
        }
    }

    private void down(ArrayList<State> successors ,int hole) {
        if (hole >= numOfCols) {
            String path = ""+curBoard[hole - numOfCols]+"D";
            swapAndStore(hole - numOfCols, hole, cost + 5, successors, path);
        }
    }

    private void up(ArrayList<State> successors ,int hole) {
        if (hole < (numOfRows - 1) * numOfCols) {
            String path = ""+curBoard[hole + numOfCols]+"U";
            swapAndStore(hole + numOfCols, hole, cost + 5, successors, path);
        }
    }

    private void twoRight(ArrayList<State> successors ,int[] holes) {
        if (holes[0] % numOfCols != 0) {
            String path = ""+curBoard[holes[0] - 1]+"&"+curBoard[holes[1] - 1]+"R";
            swap2AndStore(holes[0] - 1, holes[0], holes[1] - 1, holes[1], cost + 6, successors, path);
        }
    }

    private void twoLeft(ArrayList<State> successors ,int[] holes) {
        if (holes[0] % numOfCols != numOfCols-1) {
            String path = ""+curBoard[holes[0] + 1]+"&"+curBoard[holes[1] + 1]+"L";
            swap2AndStore(holes[0] + 1, holes[0], holes[1] + 1, holes[1], cost + 6, successors, path);
        }
    }

    private void twoDown(ArrayList<State> successors ,int[] holes) {
        if (holes[0] >= numOfCols) {
            String path = ""+curBoard[holes[0] - numOfCols]+"&"+curBoard[holes[1] - numOfCols]+"D";
            swap2AndStore(holes[0] - numOfCols, holes[0], holes[1] - numOfCols, holes[1], cost + 7, successors, path);
        }
    }
    private void twoUp(ArrayList<State> successors ,int[] holes) {
        if (holes[0] < (numOfRows - 1) * numOfCols) {
            String path = ""+curBoard[holes[0] + numOfCols]+"&"+curBoard[holes[1] + numOfCols]+"U";
            swap2AndStore(holes[0] + numOfCols, holes[0], holes[1] + numOfCols, holes[1], cost + 7, successors, path);
        }
    }


    /**
     * Switches the data at indices d1 and d2, in a copy of the current board
     * creates a new state based on this new board and pushes into s.
     */
    private void swapAndStore(int d1, int d2,int cost, ArrayList<State> s, String path) {
        int[] cpy = copyBoard(curBoard);
        int temp = cpy[d1];
        cpy[d1] = curBoard[d2];
        cpy[d2] = temp;
        PuzzleState newState = new PuzzleState(this ,cpy ,cost);
        if(!this.equals(newState)){
            if(this.pre == null || !this.pre.equals(newState)) {
                s.add(newState);
                newState.setStringPath(this.path, path);
            }
        }
    }

    private void swap2AndStore(int d1, int d2,int e1, int e2, int cost, ArrayList<State> s, String path) {
        int[] cpy = copyBoard(curBoard);
        int temp1 = cpy[d1];
        int temp2 = cpy[e1];
        cpy[d1] = curBoard[d2];
        cpy[e1] = curBoard[e2];
        cpy[d2] = temp1;
        cpy[e2] = temp2;
        PuzzleState newState = new PuzzleState(this ,cpy ,cost);
        if(!this.equals(newState)){
            if(this.pre == null || !this.pre.equals(newState)) {
                s.add(newState);
                newState.setStringPath(this.path, path);
            }
        }
    }

    /**
     * Check to see if the current state is the goal state.
     *
     * @return - true or false, depending on whether the current state matches
     * the goal
     */
    @Override
    public boolean isGoal(int[] goalState) {
        return Arrays.equals(curBoard, goalState);
    }

    /**
     * Getter to return the current board array
     *
     * @return the curState
     */
    public int[] getCurBoard() {
        return curBoard;
    }

    /**
     *
     * @return
     */
    public ArrayList<State> getPath(){
        ArrayList<State> path = new ArrayList<>();
        PuzzleState curState = this;
        path.add(curState);
        while(curState.getPre() != null){
            curState = curState.getPre();
            path.add(curState);
        }
        Collections.reverse(path);
        return path;
    }

    private void setStringPath(String pre, String current){
        if(pre.length() == 0) {
            this.path += current;
        }
        else{
            this.path += pre + "-" + current;
        }
    }

    public String getStringPath(){
        return path;
    }


    /**
     * @return The total cost ( g(n)+f(n) )to reach this state
     */
    public int getCost() {
        return cost + psa.manhattanDistance();
    }

    public int getGn(){
        return gn;
    }

    public int getHn(){
        return hn;
    }

    public int getNumOfRows(){
        return numOfRows;
    }

    public int getNumOfCols(){
        return numOfCols;
    }

    public boolean getOut(){ return out; }

    public void setOut(boolean b){ out = b; }


    /**
     * Setter the previous state of the current state
     * @param pre - The previous State from which we came to the current situation
     */
    public void setPre(PuzzleState pre) {
        this.pre = pre;
    }

    /**
     * Getter the previous state of the current state
     * @return The previous State from which we came to the current situation
     */
    public PuzzleState getPre() {
        return pre;
    }

    public int getId(){ return  this.id; }
    /**
     * Overloaded equals method to compare two states.
     *
     * @return true or false, depending on whether the states are equal
     */
    @Override
    public boolean equals(State s) {
        return Arrays.equals(this.curBoard, ((PuzzleState) s).getCurBoard());
    }

    @Override
    public String toString() {
        return Arrays.toString(curBoard);
    }

    /**
     * Method to print out the current state. Prints the puzzle board.
     */
    @Override
    public void printState() {
        for(int i=0 ; i<numOfRows ; i++){
            String row = " | ";
            for (int j = 0; j< numOfCols; j++){
                row += curBoard[j+(i* numOfCols)] + " | ";
            }
            System.out.println(row);
            System.out.println("---------");
        }
    }

    @Override
    public int compareTo(State o) {
        if(this.getCost()  < o.getCost()){
            return -1;
        }
        else if(this.getCost() > o.getCost()){
            return 1;
        }
        else{
            if(this.getId() < o.getId()){
                return -1;
            }
            else{           //this.getId() > o.getId()
                return 1;
            }
        }
    }
}
