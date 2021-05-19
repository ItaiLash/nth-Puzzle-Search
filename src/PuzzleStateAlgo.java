/**
 * The PuzzleStateAlgo class is responsible for calculating the heuristic function of a given state
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
     * @return the Manhattan distance + Linear Conflict of the current state from the goal State
     */
    public int manhattanDistance() {
        if(currentState.getNumOfEmptyBlocks() == 1){
            return (manhattan(5) + (linearConflict()*2*5));
        }
        else{
            return (manhattan(3) + (linearConflict()*2*3));
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

    /**
     * Two tiles are in linear conflict if both tiles are in their target rows or columns, and in the same setting of
     * the manhattan heuristic, they must pass over each other in order to reach their final goal position.
     * Since there is no possible way for tiles to actually slide over each other.
     * If such a conflict arises, then one of the tiles would need to move out of the aforementioned row or column,
     * and back in again, adding 2 moves to the sum of their manhattan distances.
     * @return the number of linear conflicts in the current State
     */
    private int linearConflict(){
        return linearConflictHor() + linearConflictVer();
    }

    /**
     * @return the number of horizontal linear conflicts in the current State
     */
    private int linearConflictHor(){
        int numOfConflicts = 0;
        for(int i=0 ; i< currentState.getNumOfRows() ; i++){
            for(int j=0 ; j< currentState.getNumOfCols() ; j++){
                int val = currentState.getCurBoard()[i*currentState.getNumOfCols()+j];
                if(val == 0){
                    continue;
                }
                int goalRow = goalRow(val);
                int goalCol = goalCol(val);
                if(goalRow == i){
                    for(int k=j+1 ; k< currentState.getNumOfCols() ; k++){
                        int val2 = currentState.getCurBoard()[i*currentState.getNumOfCols()+k];
                        if(val2 == 0){
                            continue;
                        }
                        int goalRow2 = goalRow(val2);
                        int goalCol2 = goalCol(val2);
                        if(goalRow2 == i){
                            if(goalCol > goalCol2){
                                numOfConflicts++;
                                j = k-1;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return numOfConflicts;
    }

    /**
     * @return the number of vertical linear conflicts in the current State
     */
    private int linearConflictVer(){
        int numOfConflicts = 0;
        for(int i=0 ; i< currentState.getNumOfCols() ; i++){
            for(int j=0 ; j< currentState.getNumOfRows() ; j++){
                int val = currentState.getCurBoard()[j*currentState.getNumOfCols()+i];
                if(val == 0){
                    continue;
                }
                int goalRow = goalRow(val);
                int goalCol = goalCol(val);
                if(goalCol == i){
                    for(int k=j+1 ; k< currentState.getNumOfRows() ; k++){
                        int val2 = currentState.getCurBoard()[k*currentState.getNumOfCols()+i];
                        if(val2 == 0){
                            continue;
                        }
                        int goalRow2 = goalRow(val2);
                        int goalCol2 = goalCol(val2);
                        if(goalCol2 == i){
                            if(goalRow > goalRow2){
                                numOfConflicts++;
                                j=k-1;
                                break;
                            }
                        }
                    }
                }
            }
        }
        return numOfConflicts;
    }
}
