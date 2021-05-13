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

    private final int id;
    private final int[] curBoard;
    private final int puzzleSize;
    private final int numOfRows;
    private final int numOfCols;
    private final int numOfEmptyBlocks;
    private final int cost;
    private Boolean out = false;
    private PuzzleState pre = null;
    private String path = "";

    private final PuzzleStateAlgo psa;

    public int gn;
    public int fn;
    public int hn;

    /**
     * Constructor for PuzzleState
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

    /**
     * Copy constructor for PuzzleState
     * @param pre   - PuzzleState
     * @param board - An array that represent the puzzle state
     * @param cost  - The total cost to reach this state
     */
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
     * @return a two dimension array with the indexes of the two "hole" (or 0 spot)
     */
    private int[] getHoles() {
        int[] holes = new int[2];
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
     *         2 if they are vertically adjacent
     *         0 otherwise
     */
    public int getHolesState() {
        int[] holes = getHoles();
        if(holes[0]+numOfRows == holes[1]) {
            return 1;
        }
        else if(holes[0] + 1 == holes[1] && holes[0]/numOfRows == holes[1]/numOfRows){
            return 2;
        }
        else{
            return 0;
        }
    }

    /**
     * States board deep copy
     * @param state - The State that need to be copied
     * @return a copy of the state
     */
    private int[] copyBoard(int[] state) {
        int[] ret = new int[puzzleSize];
        if (puzzleSize >= 0) System.arraycopy(state, 0, ret, 0, puzzleSize);
        return ret;
    }

    /**
     * Depending on the number of empty blocks the method will return all States that can be reached.
     * In addition the method will update the pre of each new state to be the current state.
     * @return an ArrayList containing all of the successors for the current state
     */
    @Override
    public ArrayList<State> genSuccessors() {
        ArrayList<State> successors = new ArrayList<>();
        if (numOfEmptyBlocks == 1) {
            int hole = getHole();
            genSuccessors1(successors, hole);
        }
        else if (numOfEmptyBlocks == 2) {
            int[] holes = getHoles();
            genSuccessors2(successors, holes);
            genSuccessors1(successors, holes[0]);
            genSuccessors1(successors, holes[1]);
        }
        for (State suc : successors){
            suc.setPre(this);
        }
        return successors;
    }

    /**
     * If the current state contains a single empty block, the method will add to the successors list
     * all the states that able to reach from this state (by moving a block left, up, right and down)
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param hole       - The location of the empty block (the '0' spot) in the current state
     */
    public void genSuccessors1(ArrayList<State> successors, int hole) {
        left(successors, hole);
        up(successors, hole);
        right(successors, hole);
        down(successors, hole);
    }

    /**
     * If the current state contains two adjacent empty blocks (horizontally or vertically),
     * the method will add to the successors list all the states that able to reach from this state
     * (by moving those two blocks left, up, right and down in parallel)
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param holes      - An array that represent the locations of the empty blocks (the '0' spots) in the current state
     */
    public void genSuccessors2(ArrayList<State> successors, int[] holes) {
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

    /**
     * If a new state can be created by moving left one of the blocks,
     * the method will add to the successors list the state obtained from moving the block left.
     * for example: -------     -------
     *              |1|2|3|     |1|2|3|
     *              |4|0|5| --> |4|5|0|
     *              |6|7|8|     |6|7|8|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param hole       - The location of the empty block (the '0' spot) in the current state
     */
    private void right(ArrayList<State> successors ,int hole) {
        if (hole % numOfCols != 0) {
            String path = ""+curBoard[hole-1]+"R";
            swapAndStore(hole - 1, hole, cost + 5, successors, path);
        }
    }

    /**
     * If a new state can be created by moving right one of the blocks,
     * the method will add to the successors list the state obtained from moving the block right.
     * for example: -------     -------
     *              |1|2|3|     |1|2|3|
     *              |4|6|0| --> |4|0|6|
     *              |7|5|8|     |7|5|8|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param hole       - The location of the empty block (the '0' spot) in the current state
     */
    private void left(ArrayList<State> successors ,int hole) {
        if (hole % numOfCols != numOfCols-1) {
            String path = ""+curBoard[hole+1]+"L";
            swapAndStore(hole + 1, hole, cost + 5, successors, path);
        }
    }

    /**
     * If a new state can be created by moving down one of the blocks,
     * the method will add to the successors list the state obtained from moving the block down.
     * for example: -------     -------
     *              |1|2|3|     |1|2|3|
     *              |4|8|6| --> |4|0|6|
     *              |5|0|7|     |5|8|7|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param hole       - The location of the empty block (the '0' spot) in the current state
     */
    private void down(ArrayList<State> successors ,int hole) {
        if (hole >= numOfCols) {
            String path = ""+curBoard[hole - numOfCols]+"D";
            swapAndStore(hole - numOfCols, hole, cost + 5, successors, path);
        }
    }

    /**
     * If a new state can be created by moving up one of the blocks,
     * the method will add to the successors list the state obtained from moving the block up.
     * for example: -------     -------
     *              |1|0|3|     |1|2|3|
     *              |4|2|6| --> |4|0|6|
     *              |5|8|7|     |5|8|7|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param hole       - The location of the empty block (the '0' spot) in the current state
     */
    private void up(ArrayList<State> successors ,int hole) {
        if (hole < (numOfRows - 1) * numOfCols) {
            String path = ""+curBoard[hole + numOfCols]+"U";
            swapAndStore(hole + numOfCols, hole, cost + 5, successors, path);
        }
    }

    /**
     * If a new state can be created by moving right two vertical blocks,
     * the method will add to the successors list the state obtained from moving those blocks right.
     * for example: -------     -------
     *              |2|0|3|     |0|2|3|
     *              |5|0|6| --> |0|5|6|
     *              |1|4|7|     |1|4|7|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param holes      - An array that represent the locations of the empty blocks (the '0' spots) in the
     *                     current state
     */
    private void twoRight(ArrayList<State> successors ,int[] holes) {
        if (holes[0] % numOfCols != 0) {
            String path = ""+curBoard[holes[0] - 1]+"&"+curBoard[holes[1] - 1]+"R";
            swap2AndStore(holes[0] - 1, holes[0], holes[1] - 1, holes[1], cost + 6, successors, path);
        }
    }

    /**
     * If a new state can be created by moving left two vertical blocks,
     * the method will add to the successors list the state obtained from moving those blocks left.
     * for example: -------     -------
     *              |0|1|3|     |1|0|3|
     *              |0|4|6| --> |4|0|6|
     *              |2|5|7|     |2|5|7|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param holes      - An array that represent the locations of the empty blocks (the '0' spots) in the
     *                     current state
     */
    private void twoLeft(ArrayList<State> successors ,int[] holes) {
        if (holes[0] % numOfCols != numOfCols-1) {
            String path = ""+curBoard[holes[0] + 1]+"&"+curBoard[holes[1] + 1]+"L";
            swap2AndStore(holes[0] + 1, holes[0], holes[1] + 1, holes[1], cost + 6, successors, path);
        }
    }

    /**
     * If a new state can be created by moving down two horizontal blocks,
     * the method will add to the successors list the state obtained from moving those blocks down.
     * for example: -------     -------
     *              |4|5|3|     |0|0|3|
     *              |0|0|6| --> |4|5|6|
     *              |1|2|7|     |1|2|7|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param holes      - An array that represent the locations of the empty blocks (the '0' spots) in the
     *                     current state
     */
    private void twoDown(ArrayList<State> successors ,int[] holes) {
        if (holes[0] >= numOfCols) {
            String path = ""+curBoard[holes[0] - numOfCols]+"&"+curBoard[holes[1] - numOfCols]+"D";
            swap2AndStore(holes[0] - numOfCols, holes[0], holes[1] - numOfCols, holes[1], cost + 7, successors, path);
        }
    }

    /**
     * If a new state can be created by moving up two horizontal blocks,
     * the method will add to the successors list the state obtained from moving those blocks up.
     * for example: -------     -------
     *              |4|5|3|     |4|5|3|
     *              |0|0|6| --> |1|2|6|
     *              |1|2|7|     |0|0|7|
     *              -------     -------
     * @param successors - An ArrayList to which will be added all the states that can be reached
     *                     from the current state
     * @param holes      - An array that represent the locations of the empty blocks (the '0' spots) in the
     *                     current state
     */
    private void twoUp(ArrayList<State> successors ,int[] holes) {
        if (holes[0] < (numOfRows - 1) * numOfCols) {
            String path = ""+curBoard[holes[0] + numOfCols]+"&"+curBoard[holes[1] + numOfCols]+"U";
            swap2AndStore(holes[0] + numOfCols, holes[0], holes[1] + numOfCols, holes[1], cost + 7, successors, path);
        }
    }

    /**
     * Copies the current state and swapped the data in indexes d1 and d2.
     * Then added the new State the ArrayList
     * @param d1   - The index of the data in the array that needs to be swapped
     * @param d2   - The index of the data in the array that needs to be swapped
     * @param cost - The cost of replacement (5)
     * @param s    - The ArrayList to which will be added the new state created from the block replacement
     * @param path - A String that represents the movement of the block
     */
    private void swapAndStore(int d1, int d2, int cost, ArrayList<State> s, String path) {
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

    /**
     * Copies the current state and swapped the data in indexes d1 and d2 and in e1 and e2.
     * Then added the new State the ArrayList
     * @param d1   - The index of the data in the array that needs to be swapped with the data in d2
     * @param d2   - The index of the data in the array that needs to be swapped with the data in d1
     * @param e1   - The index of the data in the array that needs to be swapped with the data in e2
     * @param e2   - The index of the data in the array that needs to be swapped with the data in e1
     * @param cost - The cost of replacement (6 or 7)
     * @param s    - The ArrayList to which will be added the new state created from the block replacement
     * @param path - A String that represents the movement of the block
     */
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
     * Checks if the current state equal to the goalState by comparing the two arrays.
     * @return true or false, depending on whether the current state matches
     *         the goal
     */
    @Override
    public boolean isGoal(int[] goalState) {
        return Arrays.equals(curBoard, goalState);
    }

    /**
     * Getter to return the current board array
     * @return the curState
     */
    public int[] getCurBoard() {
        return curBoard;
    }

    /**
     * Builds an ArrayList of states from the start State to the current State.
     * @return an ArrayList of States
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

    /**
     * Setter for path filed
     * @param pre     - A string that represents the path to the previous state
     * @param current - A string that represents the path of getting from the previous State to the current State
     */
    private void setStringPath(String pre, String current){
        if(pre.length() == 0) {
            this.path += current;
        }
        else{
            this.path += pre + "-" + current;
        }
    }

    /**
     * Getter for path filed
     * @return a string that represents the path to this State from the start State
     */
    public String getStringPath(){
        return path;
    }


    /**
     * @return the total cost ( g(n) + f(n) ) to reach to the current State
     */
    public int getCost() {
        return cost + psa.manhattanDistance();
    }

    /**
     * @return the cost ( g(n) ) to reach to the current State
     */
    public int getGn(){
        return gn;
    }

    /**
     * @return the heuristic function that estimates the distance of the current State from the goal State
     */
    public int getHn(){
        return hn;
    }

    /**
     * @return the number of rows in the current State board
     */
    public int getNumOfRows(){
        return numOfRows;
    }

    /**
     * @return the number of columns in the current State board
     */
    public int getNumOfCols(){
        return numOfCols;
    }

    /**
     * Getter for the MetaData, that uses later in the search algorithm
     * @return if the current State marked as "out" or not
     */
    public boolean getOut(){ return out; }

    /**
     * Setter for the MetaData, that uses later in the search algorithm
     * @param b - true will marked the State as "out", and false marked the state as "not out"
     */
    public void setOut(boolean b){ out = b; }

    /**
     * @return the number of empty blocks ('0' spots) in the current State board
     */
    public int getNumOfEmptyBlocks() {
        return numOfEmptyBlocks;
    }

    /**
     * @return an Array that holds the indexes of the empty blocks
     */
    public int[] getHolesIndex(){
        int[] arr = new int[numOfEmptyBlocks];
        if(numOfEmptyBlocks == 1){
            arr[0] = getHole();
        }
        else{
            arr = getHoles();
        }
        return arr;
    }

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

    /**
     * @return the unique id of this State
     */
    public int getId(){ return  this.id; }

    /**
     * Overloaded equals method to compare two states
     * @return true or false, depending on whether the states are equal
     */
    @Override
    public boolean equals(State s) {
        return Arrays.equals(this.curBoard, s.getCurBoard());
    }

    /**
     * Overloaded toString method
     * @return the PuzzleState as String
     */
    @Override
    public String toString() {
        return Arrays.toString(curBoard);
    }

    /**
     * Method to print out the current state
     * Prints the puzzle board
     */
    @Override
    public void printState() {
        for(int i=0 ; i<numOfRows ; i++){
            StringBuilder row = new StringBuilder(" | ");
            for (int j = 0; j< numOfCols; j++){
                row.append(curBoard[j + (i * numOfCols)]).append(" | ");
            }
            System.out.println(row);
            System.out.println("---------");
        }
    }

    /**
     * Compares two States according to their costs, if their costs are equals the method will compare
     * the two States according to their production time
     * @param o - the other State on which we will compare the current State
     * @return a negative number if this State is "smaller" (its cost is lower)
     *         and a positive number if the other State is smaller
     */
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
