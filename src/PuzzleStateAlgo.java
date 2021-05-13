import java.util.ArrayList;

public class PuzzleStateAlgo implements StateAlgo {
    private final PuzzleState currentState;
    private final int[] goalState;

    public PuzzleStateAlgo(PuzzleState cur, int[] goal){
        this.currentState = cur;
        this.goalState = goal;
    }


    //@Override
    public int manhattanDistance() {
        if(currentState.getNumOfEmptyBlocks() == 1){
            return manhattan1();
        }
        else{
            return manhattan2();
        }
    }

    public int manhattan1(){
        int manhattan = 0;
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(currentState.getCurBoard()[i*currentState.getNumOfCols()+j] != 0 &&
                        currentState.getCurBoard()[i*currentState.getNumOfCols()+j] != goalState[i*currentState.getNumOfCols()+j]){
                    manhattan += ((Math.abs(i - goalRow(currentState.getCurBoard()[i*currentState.getNumOfCols()+j])) +
                            Math.abs(j - goalCol(currentState.getCurBoard()[i*currentState.getNumOfCols()+j]))))*5;
                }
            }
        }
        return manhattan;
    }

    public int manhattan2(){
        int minManhattan = manhattan1();
        if(currentState.getHolesState() == 2){
            ArrayList<int[]> arr = swap2Ver(currentState.getCurBoard(), currentState.getHolesIndex());
            for(int[] s : arr){
                int currentMan = manhattan1(s)+6;
                if(currentMan < minManhattan){
                    minManhattan = currentMan;
                }
            }
            return minManhattan;
        }
        else if(currentState.getHolesState() == 1){
            ArrayList<int[]> arr = swap2Hor(currentState.getCurBoard(), currentState.getHolesIndex());
            for(int[] s : arr){
                int currentMan = manhattan1(s)+6;
                if(currentMan < minManhattan){
                    minManhattan = currentMan;
                }
            }
        }
        return minManhattan;
    }

    private int manhattan1(int[] board){
        int manhattan = 0;
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(board[i*currentState.getNumOfCols()+j] != 0 &&
                        board[i*currentState.getNumOfCols()+j] != goalState[i*currentState.getNumOfCols()+j]){
                    manhattan += ((Math.abs(i - goalRow(board[i*currentState.getNumOfCols()+j])) +
                            Math.abs(j - goalCol(board[i*currentState.getNumOfCols()+j]))))*5;
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

    public int getCost(){
        return currentState.getCost()+this.manhattanDistance();
    }

    public int[] getGoal(){
        return goalState;
    }

    public State getCurState(){
        return currentState;
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
        for(int i=0 ; i<arr.length ; i++){
            cpy[i] = arr[i];
        }
        swap(cpy, x1, y1);
        swap(cpy, x2, y2);
        return cpy;
    }

    private void swap(int[] arr, int x, int y){
        int temp = arr[x];
        arr[x] = arr[y];
        arr[y] = temp;
    }



    public static void main(String[] args) {
        int[] board = {8, 1, 3,
                       4, 0, 2,
                       7, 6, 5};
        int[] goal = {1,2,3,4,5,6,7,8,0};
        PuzzleState p = new PuzzleState(board, 3, 3, 1, 0, goal);
        PuzzleStateAlgo sa = new PuzzleStateAlgo(p,goal);
        System.out.println(sa.manhattanDistance());
        System.out.println(sa.heuristic());

        p.printState();
    }
}
