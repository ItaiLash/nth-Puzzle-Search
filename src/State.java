import java.util.ArrayList;

/**
 * State interface from which problem states inherit. Defines a method to check
 * if the current state is a goal, generate successors, and find the cost to
 * come to the current state.
 *
 * @author Itai Lashover
 */
public interface State {
    // determine if current state is goal
    boolean isGoal(int[] goal);

    // generate successors to the current state
    ArrayList<State> genSuccessors();

    // determine cost from initial state to THIS state
     int getCost();

    // print the current state
    void printState();

    // compare the actual state data
    boolean equals(State s);

    int[] getCurBoard();

    void setPre(PuzzleState pre);

    PuzzleState getPre();

    ArrayList<State> getPath();

    String getStringPath();

    String toString();

}