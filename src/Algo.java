import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The Algo class contains five search algorithms:
 * BFS, DFID, A*, IDA*, DFBnB
 *
 * @author Itai Lashover
 */
public class Algo {

    static int numOfStates = 0;
    static int cost = 0;
    String algo;
    boolean withTime;
    boolean withOpen;
    final static String outputFilePath = "output.txt";
    File file;

    /**
     * Constructor for Algo
     * @param algo      - The algorithm to be run as a string
     * @param withOpen  - Boolean variable, for true value the open list will be printed on the screen,
     *                    otherwise the list will be written to the file
     * @param withTime  - Boolean variable, if true the algorithm's runtime will be printed to the screen
     */
    public Algo(String algo, boolean withOpen, boolean withTime){
        this.algo = algo;
        this.withOpen = withOpen;
        this.withTime = withTime;
    }

    /**
     * Runs the required algorithm from the start State and prints to the screen the desired values
     * (run time, open list, number of States generated during the algorithm run and cost)
     * @param start - The start State, the state from which the algorithm begins
     * @param goal  - The target state, the state in which the algorithm will end
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public void run(State start, State goal) throws NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        Method m = Algo.class.getDeclaredMethod(algo,State.class,State.class);
        long startTime = 0, stopTime, totalTime = 0;
        if (withTime){
            startTime = System.nanoTime();
        }
        String ans = (String)m.invoke(this,start, goal);
        if(withTime){
            stopTime = System.nanoTime();
            totalTime = stopTime - startTime;
        }
        file = new File(outputFilePath);
        try {
            BufferedWriter bf = new BufferedWriter(new FileWriter(file));
            //bf.write(algo +"\n");
            bf.write(ans + "\n");
            bf.write("Num: " + numOfStates +"\n");
            if(!ans.equals("no path")) {
                bf.write("Cost: " + cost +"\n");
            }
            if (withTime) {
                bf.write((double) totalTime / 1_000_000_000 + " seconds");
            }
            bf.flush();
            bf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * BFS Algorithm
     * @param start - start State
     * @param goal  - goal State
     * @return The order of operations from the start State to the goal State as a string,
     *         If there is no such route the method will return "no path"
     */
    public String BFS(State start, State goal) {
        numOfStates = 0;
        Hashtable<String, State> open = new Hashtable<>();
        Queue<State> q = new LinkedList<>();
        Hashtable<String, State> close = new Hashtable<>();
        open.put(start.toString(), start);
        q.add(start);
        numOfStates++;
        while (!q.isEmpty()) {
            handleOpenList(open);
            State curState = q.poll();
            open.remove(curState.toString());
            close.put(curState.toString(), curState);
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                numOfStates++;
                if (!open.containsKey(next.toString()) && !close.containsKey(next.toString())) {
                    if (next.isGoal(goal.getCurBoard())) {
                        cost = next.getCost(false);
                        return next.getStringPath();
                    }
                    open.put(next.toString(), next);
                    q.add(next);
                }
            }
        }
        return "no path";
    }

    /**
     * DFID Algorithm
     * @param start - start State
     * @param goal  - goal State
     * @return The order of operations from the start State to the goal State as a string,
     *         If there is no such route the method will return "no path"
     */
    public String DFID(State start, State goal) {
        numOfStates = 0;
        for (int depth = 1 ;; depth++) {
            Hashtable<String, State> h = new Hashtable<>();
            String result = Limited_DFS(start, goal, depth, h);
            if (!result.equals("cutoff")) {
                return result;
            }
        }
    }

    /**
     * Limited DFS Algorithm
     * @param curState - current State
     * @param goal     - goal State
     * @param limit    - The maximum depth that the DFS will reach during the run
     * @param h        - loop avoidance
     * @return The order of operations from the start State to the goal State as a string,
     *         If there is no such route the method will return "no path",
     *         If the method has reached the limit "cutoff" will return
     */
    public String Limited_DFS(State curState, State goal, int limit, Hashtable<String, State> h) {
        if (curState.isGoal(goal.getCurBoard())) {
            cost = curState.getCost(false);
            return curState.getStringPath();
        } else if (limit == 0) {
            return "cutoff";
        } else {
            handleOpenList(h);
            h.put(curState.toString(), curState);
            boolean isCutoff = false;
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                numOfStates++;
                if(h.containsKey(next.toString())){
                    continue;
                }
                String result = Limited_DFS(next, goal, limit - 1, h);
                if (result.equals("cutoff")) {
                    isCutoff = true;
                } else if (!result.equals("no path")) {
                    return result;
                }
            }
            h.remove(curState.toString());
            if (isCutoff) {
                return "cutoff";
            } else {
                return "no path";
            }
        }
    }

    /**
     * A* Algorithm
     * @param start - start State
     * @param goal  - goal State
     * @return The order of operations from the start State to the goal State as a string,
     *         If there is no such route the method will return "no path"
     */
    public String AStar(State start, State goal) {
        numOfStates = 0;
        Hashtable<String, State> close = new Hashtable<>();
        PriorityQueue<State> q = new PriorityQueue<>();
        Hashtable<String, State> open = new Hashtable<>();
        q.add(start);
        open.put(start.toString(), start);
        numOfStates++;
        while (!q.isEmpty()) {
            handleOpenList(open);
            State current = q.poll(); // Get the cheapest state to explore.
            open.remove(current.toString(), current);
            if (current.isGoal(goal.getCurBoard())) {
                cost = current.getCost(true);
                return current.getStringPath();
            }
            close.put(current.toString(), current); // Put it in the closed list.
            ArrayList<State> suc = current.genSuccessors();
            // Iterate over all of the allowed operators.
            for (State next : suc) {
                numOfStates++;
                if (!open.containsKey(next.toString()) && !close.containsKey(next.toString())) {
                    q.add(next);
                    open.put(next.toString(), next);
                } else if (open.containsKey(next.toString())) {
                    State n = open.get(next.toString());
                    if (n.getCost(false) > next.getCost(false)) {
                        q.remove(n);
                        open.remove(n.toString());
                        q.add(next);
                        open.put(next.toString(), next);
                    }
                }
            }
        }
        return "no path";
    }

    /**
     * IDA* (Iterative deepening A*) Algorithm
     * @param start - start State
     * @param goal  - goal State
     * @return The order of operations from the start State to the goal State as a string,
     *         If there is no such route the method will return "no path"
     */
    public String IDAStar(State start, State goal) {
        numOfStates = 0;
        Stack<State> stack = new Stack<>();
        Hashtable<String, State> h = new Hashtable<>();
        int t = start.getCost(true);
        while (t != Integer.MAX_VALUE) {
            int minF = Integer.MAX_VALUE;
            stack.push(start);
            h.put(stack.toString(), start);
            while (!stack.isEmpty()) {
                handleOpenList(h);
                State current = stack.pop();
                if (current.getOut()) {
                    h.remove(current.toString());
                } else {
                    current.setOut(true);
                    stack.push(current);
                    ArrayList<State> suc = current.genSuccessors();
                    // Iterate over all of the allowed operators.
                    for (State next : suc) {
                        numOfStates++;
                        if (next.getCost(true) > t) {
                            minF = Math.min(minF, next.getCost(true));
                            continue;
                        }
                        if (h.containsKey(next.toString())) {
                            State same = h.get(next.toString());
                            if (same.getOut()) {
                                continue;
                            } else {
                                if (same.getCost(true) > next.getCost(true)) {
                                    stack.remove(same);
                                    h.remove(same.toString());
                                } else {
                                    continue;
                                }
                            }
                        }
                        if (next.isGoal(goal.getCurBoard())) {
                            cost = next.getCost(true);
                            return next.getStringPath();
                        }
                        stack.push(next);
                        h.put(next.toString(), next);
                    }
                }
            }
            start.setOut(false);
            t = minF;
        }
        return "no path";
    }

    /**
     * DFBnB (Depth First Branch and Bound) Algorithm
     * @param start - start State
     * @param goal  - goal State
     * @return The order of operations from the start State to the goal State as a string,
     *         If there is no such route the method will return "no path"
     */
    public String DFBnB(State start, State goal) {
        numOfStates = 0;
        Stack<State> stack = new Stack<>();
        Hashtable<String, State> h = new Hashtable<>();
        stack.push(start);
        h.put(start.toString(), start);
        String result = "no path";
        int t = Integer.MAX_VALUE;
        while (!stack.isEmpty()) {
            handleOpenList(h);
            State current = stack.pop();
            if (current.getOut()) {
                h.remove(current.toString());
            }
            else {
                current.setOut(true);
                stack.push(current);
                ArrayList<State> suc = current.genSuccessors();
                Comparator<State> stateComparator = (s1, s2) -> {
                    if (s1.getCost(true) < s2.getCost(true)) {
                        return -1;
                    } else if (s1.getCost(true) > s2.getCost(true)) {
                        return 1;
                    } else {
                        return Integer.compare(s1.getId(), s2.getId());
                    }
                };
                suc.sort(stateComparator);
                // Iterate over all of the allowed operators.
                for (int i = 0; i < suc.size(); i++) {
                    State next = suc.get(i);
                    numOfStates++;
                    if (next.getCost(true) >= t) {
                        suc.subList(i, suc.size()).clear();
                    } else if (h.containsKey(next.toString())) {
                        State same = h.get(next.toString());
                        if (same.getOut()) {
                            suc.remove(next);
                        } else {
                            if (same.getCost(true) <= next.getCost(true)) {
                                suc.remove(next);
                            } else {
                                stack.remove(same);
                                h.remove(same.toString());
                            }
                        }
                    } else if (next.isGoal(goal.getCurBoard())) {   // if we reached here, f(g) < t
                        t = next.getCost(true);
                        cost = next.getCost(true);
                        result = next.getStringPath();
                        suc.subList(i, suc.size()).clear();
                    }
                }
                Collections.reverse(suc);
                for (State s : suc) {
                    stack.push(s);
                    h.put(s.toString(), s);
                }
            }
        }
        return result;
    }

    /**
     * Handles the open list, if the 'withOpen' value is true it will call the 'print' method that will print
     * the list to the screen
     * @param h - Hashtable that represents the open list
     */
    private void handleOpenList(Hashtable<String, State> h) {
        if(withOpen){ print(h); }
    }

    /**
     * print the list (Hashtable values) to the screen
     * @param h - Hashtable that represents the open list
     */
    private void print(Hashtable<String, State> h){
        System.out.println("\nOpen List:");
        if(h.isEmpty()){
            System.out.println("empty...");
        }
        for (Map.Entry<String, State> entry : h.entrySet()) {
            System.out.println(entry.getValue());
        }
    }
}
