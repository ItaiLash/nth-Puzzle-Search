import java.util.ArrayList;

/**
 * State interface from which problem states inherit. Defines a method to check
 * if the current state is a goal, generate successors, and find the cost to
 * come to the current state.
 *
 * @author Itai Lashover
 */
public interface State {

    boolean isGoal(int[] goal);

    ArrayList<State> genSuccessors();

    int getCost(boolean withHeuristic);

    void printState();

    boolean equals(State s);

    int[] getCurBoard();

    void setPre(PuzzleState pre);

    PuzzleState getPre();

    ArrayList<State> getPath();

    String getStringPath();

    int getId();

    boolean getOut();

    void setOut(boolean b);

    String toString();

}