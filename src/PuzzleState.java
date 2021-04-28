import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * EightPuzzleState defines a state for the 8puzzle problem. The board is always
 * represented by a single dimensioned array, we attempt to provide the illusion
 * that the state representation is 2 dimensional and this works very well. In
 * terms of the actual tiles, '0' represents the hole in the board, and 0 is
 * treated special when generating successors. We do not treat '0' as a tile
 * itself, it is the "hole" in the board (as we refer to it herein)
 *
 * @author Itai Lashover
 */
public class PuzzleState implements State {

    private int puzzleSize;
    private int n;
    private int m;
    private PuzzleState pre = null;
    private int cost = 0;
    private int numOfEmptyBlocks;
    private int outOfPlace = 0;
    private int manDist = 0;
    int[] goalState;
    private int[] curBoard;

    /**
     *Constructor for PuzzleState
     * @param board       - An array that represent the puzzle state
     * @param n           - Number of rows in the Puzzle
     * @param m           - Number of column in the Puzzle
     * @param emptyBlocks - Number of empty blocks in the Puzzle
     * @param cost        - The total cost to reach this state
     */
    public PuzzleState(int[] board, int n, int m, int emptyBlocks, int cost) {
        puzzleSize = n*m;
        numOfEmptyBlocks = emptyBlocks;
        goalState = new int[n*m];
        for(int i=0 ; i<puzzleSize-numOfEmptyBlocks ; i++) {
            goalState[i] = i + 1;
        }
        curBoard = board;
        setOutOfPlace();
        setManDist();
        this.n = n;
        this.m = m;
        this.cost += cost;
    }

//    /**
//     * How much it costs to come to this state
//     */
//    @Override
//    public double findCost() {
//        int cost = 0;
//        for (int i = 0; i < curBoard.length; i++) {
//            int goalNumber = goalState[i] == 0 ? 9 : goalState[i];
//            cost += Math.abs(curBoard[i] - goalNumber);
//        }
//        return cost;
//    }

    /**
     * @return אhe total cost to reach this state
     */
    public double findCost() {
        return cost;
    }

    /**
     * Set the 'tiles out of place' distance for the current board
     */
    private void setOutOfPlace() {
        for (int i = 0; i < curBoard.length; i++) {
            if (curBoard[i] != goalState[i]) {
                outOfPlace++;
            }
        }
    }

    /**
     * Set the Manhattan Distance for the current board
     */
    private void setManDist() {
        // linearly search the array independent of the nested for's below
        int index = -1;

        // just keeps track of where we are on the board (relatively, can't use
        // 0 so these values need to be shifted to the right one place)
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++) {
                index++;

                // sub 1 from the val to get the index of where that value
                // should be
                int val = (curBoard[index] - 1);

                /*
                 * If we're not looking at the hole. The hole will be at
                 * location -1 since we subtracted 1 before to turn val into the
                 * index
                 */
                if (val != -1) {
                    // Horizontal offset, mod the tile value by the horizontal
                    // dimension
                    int horiz = val % n;
                    // Vertical offset, divide the tile value by the vertical
                    // dimension
                    int vert = val / m;

                    manDist += Math.abs(vert - (y)) + Math.abs(horiz - (x));
                }
                // If we are looking at the hole, skip it
            }
        }
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
     * @return 1 if they are horizontally adjacent, 2 if they are vertically adjacent and 0 otherwise
     */
    private int getHolesState() {
        int[] holes = getHoles();
        if(holes[0]+n == holes[1]) {        //horizontal
            return 1;
        }
        else if(holes[0] + 1 == holes[1] && holes[0]/n == holes[1]/n){      //vertical
            return 2;
        }
        else{
            return 0;
        }
    }

    /**
     * Getter for the outOfPlace value
     * @return the outOfPlace h(n) value
     */
    public int getOutOfPlace() {
        return outOfPlace;
    }

    /**
     * Getter for the Manhattan Distance value
     * @return the Manhattan Distance h(n) value
     */
    public int getManDist() {
        return manDist;
    }

    /**
     * Setter the previous state of the current state
     * @param pre - The previous State from which we came to the current situation
     */
    public void setPre(PuzzleState pre) {
        this.pre = pre;
    }

    public PuzzleState getPre() {
        return pre;
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
            genSuccessors1(successors, holes[0]);
            genSuccessors1(successors, holes[1]);
            genSuccessors2(successors, holes);
        }
        for (State suc : successors){
            suc.setPre(this);
        }
        return successors;
    }

    public void genSuccessors1(ArrayList<State> successors, int hole) {
        // try to generate a state by sliding a tile leftwise into the hole
        // if we CAN slide into the hole
        if (hole % n != 0) {
            /*
             * we can slide leftwise into the hole, so generate a new state for
             * this condition and throw it into successors
             */
            swapAndStore(hole - 1, hole, cost + 5, successors);
        }
        // try to generate a state by sliding a tile topwise into the hole
        if (hole < (m - 1) * n) {
            swapAndStore(hole + n, hole, cost + 5, successors);
        }
        // try to generate a state by sliding a tile bottomwise into the hole
        if (hole >= n) {
            swapAndStore(hole - n, hole, cost + 5, successors);
        }
        // try to generate a state by sliding a tile rightwise into the hole
        if (hole % n != n - 1) {
            swapAndStore(hole + 1, hole, cost + 5, successors);
        }
    }

    public void genSuccessors2(ArrayList<State> successors, int holes[]) {
        int holesState = getHolesState();
        if(holesState == 1) {
            if (holes[0] % n != 0) {
                swap2AndStore(holes[0] - 1, holes[0], holes[1] - 1, holes[1], cost + 6, successors);
            }
            if (holes[0] % n != n - 1) {
                swap2AndStore(holes[0] + 1, holes[0], holes[1] + 1, holes[1], cost + 6, successors);
            }
        }
        else if (holesState == 2) {
                // try to generate a state by sliding a tile topwise into the hole
                if (holes[0] < (m - 1) * n) {
                    swap2AndStore(holes[0] + n, holes[0], holes[1] + n, holes[1], cost + 7, successors);
                }

                // try to generate a state by sliding a tile bottomwise into the hole
                if (holes[0] >= n) {
                    swap2AndStore(holes[0] - n, holes[0], holes[1] - n, holes[1], cost + 7, successors);
                }
            }
        }

    /*
     * Switches the data at indices d1 and d2, in a copy of the current board
     * creates a new state based on this new board and pushes into s.
     */
    private void swapAndStore(int d1, int d2,int cost, ArrayList<State> s) {
        int[] cpy = copyBoard(curBoard);
        int temp = cpy[d1];
        cpy[d1] = curBoard[d2];
        cpy[d2] = temp;
        PuzzleState newState = new PuzzleState(cpy, n, m, numOfEmptyBlocks, cost);
        if(!this.equals(newState)){
            s.add(newState);
        }
    }

    private void swap2AndStore(int d1, int d2,int e1, int e2, int cost, ArrayList<State> s) {
        int[] cpy = copyBoard(curBoard);
        int temp1 = cpy[d1];
        int temp2 = cpy[e1];
        cpy[d1] = curBoard[d2];
        cpy[e1] = curBoard[e2];
        cpy[d2] = temp1;
        cpy[e2] = temp2;
        s.add((new PuzzleState(cpy,n,m,numOfEmptyBlocks,cost)));
    }

    /**
     * Check to see if the current state is the goal state.
     *
     * @return - true or false, depending on whether the current state matches
     * the goal
     */
    @Override
    public boolean isGoal() {
        return Arrays.equals(curBoard, goalState);
    }

    /**
     * Method to print out the current state. Prints the puzzle board.
     */
    @Override
    public void printState() {
        for(int i=0 ; i<n ; i++){
            String row = " | ";
            for (int j=0 ; j<m ; j++){
                row += curBoard[j+(i*m)] + " | ";
            }
            System.out.println(row);
            System.out.println("---------");
        }
    }

    /**
     * Overloaded equals method to compare two states.
     *
     * @return true or false, depending on whether the states are equal
     */
    @Override
    public boolean equals(State s) {
        return Arrays.equals(this.curBoard, ((PuzzleState) s).getCurBoard());

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


    public static void main(String[] args) {
        int board[] = {3,4,5,1,7,0,6,2,0};
        PuzzleState p = new PuzzleState(board,3,3,2,0);
        p.printState();
        ArrayList<State> s = p.genSuccessors();
        System.out.println();
        for (int i=0 ; i<s.size() ; i++){
            s.get(i).printState();
            System.out.println(s.get(i).findCost());
            System.out.println();
        }
        System.out.println();
        ArrayList<State> s2 = s.get(3).genSuccessors();
        for (int i=0 ; i<s2.size() ; i++){
            s2.get(i).printState();
            System.out.println(s2.get(i).findCost());
            System.out.println();
        }

        int board2[] = {1, 3, 4, 2, 0, 0};
        PuzzleState p2 = new PuzzleState(board2, 2, 3, 2, 0);
        p2.printState();
    }

}
