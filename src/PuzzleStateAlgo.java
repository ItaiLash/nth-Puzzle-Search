/**
 *The PuzzleStateAlgo class is responsible for calculating the heuristic function of a given state
 * according to its distance from the goal state.
 *
 * @author Itai Lashover
 */
public class PuzzleStateAlgo {
    private final PuzzleState currentState;
    private final int[] goalState;

    /**
     * PuzzleStateAlgo Constructor
     * @param cur  - current State
     * @param goal - goal State
     */
    public PuzzleStateAlgo(PuzzleState cur, int[] goal){
        this.currentState = cur;
        this.goalState = goal;
    }

    /**
     * @return the Manhattan distance of the current state from the goal State
     */
    public int manhattanDistance() {
        if(currentState.getNumOfEmptyBlocks() == 1){
            return manhattan(5);
        }
        else{
            return manhattan(3);
        }
    }

    /**
     * @param c - The cost of moving each individual tile
     * @return the Manhattan distance of the current state from the goal State
     */
    public int manhattan(double c){
        int manhattan = 0;
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(currentState.getCurBoard()[i*currentState.getNumOfCols()+j] != 0 &&
                        currentState.getCurBoard()[i*currentState.getNumOfCols()+j] != goalState[i*currentState.getNumOfCols()+j]){
                    manhattan += ((Math.abs(i - goalRow(currentState.getCurBoard()[i*currentState.getNumOfCols()+j])) +
                            Math.abs(j - goalCol(currentState.getCurBoard()[i*currentState.getNumOfCols()+j]))))*c;
                }
            }
        }
        return manhattan;
    }

    /**
     * @param value - The value to look for
     * @return the row where a certain value in the goal State
     */
    private int goalRow(int value){
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(goalState[i*currentState.getNumOfCols()+j] == value ) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * @param value - The value to look for
     * @return the column where a certain value in the goal State
     */
    private int goalCol(int value){
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(goalState[i*currentState.getNumOfCols()+j] == value ) {
                    return j;
                }
            }
        }
        return -1;
    }

    /**
     * @return the array representing the goal State
     */
    public int[] getGoal(){
        return goalState;
    }
/*
    public int manhattan2(){
        int minManhattan = manhattan1(5);
        if(currentState.getHolesState() == 2){
            ArrayList<int[]> arr = swap2Ver(currentState.getCurBoard(), currentState.getHolesIndex());
            for(int[] s : arr){
                int currentMan = manhattan1(s,5)+6;
                if(currentMan < minManhattan){
                    minManhattan = currentMan;
                }
            }
            return minManhattan;
        }
        else if(currentState.getHolesState() == 1){
            ArrayList<int[]> arr = swap2Hor(currentState.getCurBoard(), currentState.getHolesIndex());
            for(int[] s : arr){
                int currentMan = manhattan1(s,5)+7;
                if(currentMan < minManhattan){
                    minManhattan = currentMan;
                }
            }
        }
        return minManhattan;
    }

    private int manhattan1(int[] board, int c){
        int manhattan = 0;
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(board[i*currentState.getNumOfCols()+j] != 0 &&
                        board[i*currentState.getNumOfCols()+j] != goalState[i*currentState.getNumOfCols()+j]){
                    manhattan += ((Math.abs(i - goalRow(board[i*currentState.getNumOfCols()+j])) +
                            Math.abs(j - goalCol(board[i*currentState.getNumOfCols()+j]))))*c;
                }
            }
        }
        return manhattan;
    }

    @Override
    public int heuristic() {
        int hamming = 0;
        for (int i = 0; i < currentState.getCurBoard().length; i++) {
            if (currentState.getCurBoard()[i] != 0 &&
                    currentState.getCurBoard()[i] !=goalState[i]) {
                hamming++;
            }
        }
        return hamming;
    }
    private ArrayList<int[]> swap2Ver(int[] currentBoard, int[] holes){
        ArrayList<int[]> swaps = new ArrayList<>();
        if(isInside(currentBoard, holes[0]+currentState.getNumOfCols()) && isInside(currentBoard, holes[1]+currentState.getNumOfCols())){
            swaps.add(swap(currentBoard, holes[0], holes[0]+currentState.getNumOfCols(), holes[1], holes[1]+currentState.getNumOfCols()));
        }
        if(isInside(currentBoard, holes[0]-currentState.getNumOfCols()) && isInside(currentBoard, holes[1]-currentState.getNumOfCols())){
            swaps.add(swap(currentBoard, holes[0], holes[0]-currentState.getNumOfCols(), holes[1], holes[1]-currentState.getNumOfCols()));
        }
        return swaps;
    }

    private ArrayList<int[]> swap2Hor(int[] currentBoard, int[] holes){
        ArrayList<int[]> swaps = new ArrayList<>();
        if(isInside(currentBoard, holes[0]+1) && isInside(currentBoard, holes[1]+1)){
            swaps.add(swap(currentBoard, holes[0], holes[0]+1, holes[1], holes[1]+1));
        }
        if(isInside(currentBoard, holes[0]-1) && isInside(currentBoard, holes[1]-1)){
            swaps.add(swap(currentBoard, holes[0], holes[0]-1, holes[1], holes[1]-1));
        }
        return swaps;
    }

    private boolean isInside(int[] arr, int x){
        return (x < arr.length && x>=0);
    }

    private int[] swap(int[] arr, int x1, int y1, int x2, int y2){
        int[] cpy = new int[arr.length];
        System.arraycopy(arr, 0, cpy, 0, arr.length);
        swap(cpy, x1, y1);
        swap(cpy, x2, y2);
        return cpy;
    }

    private void swap(int[] arr, int x, int y){
        int temp = arr[x];
        arr[x] = arr[y];
        arr[y] = temp;
    }
 */
}
