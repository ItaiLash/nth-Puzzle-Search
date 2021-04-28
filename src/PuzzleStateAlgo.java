public class PuzzleStateAlgo implements StateAlgo {
    private PuzzleState currentState;
    private PuzzleState goalState;

    public PuzzleStateAlgo(PuzzleState cur, PuzzleState goal){
        this.currentState = cur;
        this.goalState = goal;
    }


    @Override
    public int manhattanDistance() {
        int manhattan = 0;
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(currentState.getCurBoard()[i*currentState.getNumOfCols()+j] != 0 &&
                        currentState.getCurBoard()[i*currentState.getNumOfCols()+j] != goalState.getCurBoard()[i*currentState.getNumOfCols()+j]){
                    manhattan += (Math.abs(i - goalRow(currentState.getCurBoard()[i*currentState.getNumOfCols()+j])) +
                            Math.abs(j - goalCol(currentState.getCurBoard()[i*currentState.getNumOfCols()+j])));
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
                    currentState.getCurBoard()[i] !=goalState.getCurBoard()[i]) {
                hamming++;
            }
        }
        return hamming;
    }


    private int goalRow(int value){
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(goalState.getCurBoard()[i*currentState.getNumOfCols()+j] == value ) {
                    return i;
                }
            }
        }
        return -1;
    }


    private int goalCol(int value){
        for(int i = 0 ; i < currentState.getNumOfRows() ; i++) {
            for(int j = 0 ; j < currentState.getNumOfCols() ; j++){
                if(goalState.getCurBoard()[i*currentState.getNumOfCols()+j] == value ) {
                    return j;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int board[] = {8, 1, 3,
                       4, 0, 2,
                       7, 6, 5};
        PuzzleState p = new PuzzleState(board, 3, 3, 1, 0);
        int goal[] = {1,2,3,4,5,6,7,8,0};
        PuzzleState g = new PuzzleState(goal, 3, 3, 1, 0);
        PuzzleStateAlgo sa = new PuzzleStateAlgo(p,g);
        System.out.println(sa.manhattanDistance());
        System.out.println(sa.heuristic());

        p.printState();
    }
}
