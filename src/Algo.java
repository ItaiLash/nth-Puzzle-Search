import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Algo {

    static int numOfStates = 0;
    static int cost = 0;
    String algo;
    boolean withTime;
    boolean withOpen;

    public Algo(String algo, boolean withOpen, boolean withTime){
        this.algo = algo;
        this.withOpen = withOpen;
        this.withTime = withTime;
    }

    public void run(State start, State goal) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
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
        System.out.println(algo);
        System.out.println(ans);
        if(!ans.equals("fail")) {
            System.out.println("Num: " + numOfStates);
            System.out.println("Cost: " + cost);
            if (withTime) {
                System.out.println((double) totalTime / 1_000_000_000 + " seconds");
            }
        }

    }

    public String BFS(State start, State goal) {
        numOfStates = 0;
        Hashtable<String, State> open = new Hashtable();
        Queue<State> q = new LinkedList<>();
        Hashtable<String, State> close = new Hashtable();
        open.put(start.toString(), start);
        q.add(start);
        numOfStates++;
        while (!q.isEmpty()) {
            State curState = q.poll();
            open.remove(curState.toString());
            close.put(curState.toString(), curState);
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                numOfStates++;
                if (!open.containsKey(next.toString()) && !close.containsKey(next.toString())) {
                    if (next.isGoal(goal.getCurBoard())) {
                        cost = next.getCost();
                        return next.getStringPath();
                    }
                    open.put(next.toString(), next);
                    q.add(next);
                }
            }
        }
        return "fail";
    }

    public String DFID(State start, State goal) {
        numOfStates = 0;
        for (int depth = 1; ; depth++) {
            Hashtable h = new Hashtable();
            String result = Limited_DFS(start, goal, depth, h);
            if (!result.equals("cutoff")) {
                return result;
            }
        }
    }



    public String Limited_DFS(State curState, State goal, int limit, Hashtable h) {
        if (curState.equals(goal)) {
            cost = curState.getCost();
            return curState.getStringPath();
        } else if (limit == 0) {
            return "cutoff";
        } else {
            h.put(curState.toString(), curState);
            boolean isCutoff = false;
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                numOfStates++;
                if (!h.containsKey(next.toString())) {
                    String result = Limited_DFS(next, goal, limit - 1, h);
                    if (result.equals("cutoff")) {
                        isCutoff = true;
                    } else if (!result.equals("fail")) {
                        return result;
                    }
                }
            }
            h.remove(curState.toString());
            if (isCutoff) {
                return "cutoff";
            } else {
                return "fail";
            }
        }
    }

    public String AStar(State start, State goal) {
        numOfStates = 0;
        Hashtable<String, State> close = new Hashtable<>();
        PriorityQueue<State> q = new PriorityQueue<>();
        Hashtable<String, State> open = new Hashtable<>();
        q.add(start);
        open.put(start.toString(), start);
        numOfStates++;
        while (!q.isEmpty()) {
            State current = q.poll(); // Get the cheapest state to explore.
            open.remove(current.toString(), current);
            if (current.isGoal(goal.getCurBoard())) {
                cost = current.getCost();
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
                    if (n.getGn() > next.getGn()) {
                        q.remove(n);
                        open.remove(n.toString());
                        q.add(next);
                        open.put(next.toString(), next);
                    }
                }
            }
        }
        return "fail";
    }

    public String IDAStar(State start, State goal) {
        numOfStates = 0;
        Stack<State> stack = new Stack();
        Hashtable<String, State> h = new Hashtable<>();
        int t = start.getCost();
        while (t != Integer.MAX_VALUE) {
            int minF = Integer.MAX_VALUE;
            stack.push(start);
            h.put(stack.toString(), start);
            while (!stack.isEmpty()) {
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
                        if (next.getCost() > t) {
                            minF = Math.min(minF, next.getCost());
                            continue;
                        }
                        if (h.containsKey(next.toString())) {
                            State same = h.get(next.toString());
                            if (same.getOut()) {
                                continue;
                            } else {
                                if (same.getCost() > next.getCost()) {
                                    stack.remove(same);
                                    h.remove(same.toString());
                                } else {
                                    continue;
                                }
                            }
                        }
                        if (next.isGoal(goal.getCurBoard())) {
                            cost = next.getCost();
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
        return "fail";
    }


    public String DFBnB(State start, State goal) {
        numOfStates = 0;
        Stack<State> stack = new Stack();
        Hashtable<String, State> h = new Hashtable<>();
        stack.push(start);
        h.put(start.toString(), start);

        String result = "fail";
        int t = Integer.MAX_VALUE;
        while (!stack.isEmpty()) {
            State current = stack.pop();
            if (current.getOut()) {
                h.remove(current.toString());
            }
            else {
                current.setOut(true);
                stack.push(current);
                ArrayList<State> suc = current.genSuccessors();
                Comparator<State> stateComparator = (s1, s2) -> {
                    if (s1.getCost() < s2.getCost()) {
                        return -1;
                    } else if (s1.getCost() > s2.getCost()) {
                        return 1;
                    } else {
                        if (s1.getId() < s2.getId()) {
                            return -1;
                        } else {           //s1.getId() > s2.getId()
                            return 1;
                        }
                    }
                };
                suc.sort(stateComparator);
                // Iterate over all of the allowed operators.
                for (int i = 0; i < suc.size(); i++) {
                    State next = suc.get(i);
                    numOfStates++;
                    if (next.getCost() >= t) {
                        suc.subList(i, suc.size()).clear();
                    } else if (h.containsKey(next.toString())) {
                        State same = h.get(next.toString());
                        if (same.getOut()) {
                            suc.remove(next);
                        } else {
                            if (same.getCost() <= next.getCost()) {
                                suc.remove(next);
                            } else {
                                stack.remove(same);
                                h.remove(same.toString());
                            }
                        }
                    } else if (next.isGoal(goal.getCurBoard())) {   // if we reached here, f(g) < t
                        t = next.getCost();
                        cost = next.getCost();
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



        public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
//        int board[] = {1,2,3,4,
//                       5,6,11,7,
//                       9,10,8,0};
//        int goal[] = {1,2,3,4,5,6,7,8,9,10,11,0};
////
//        State p = new PuzzleState(board, 3, 4, 1, 0,goal);
//        State g = new PuzzleState(goal, 3, 4, 1, 0,goal);
//        p.printState();
////        System.out.println(BFS(p, g));
////        System.out.println("Num: " + numOfStates);
////        System.out.println("Cost: " + cost);
////        System.out.println();
//        System.out.println(DFBnB(p, g));
//        System.out.println("Num: " + numOfStates);
//        System.out.println("Cost: " + cost);
//
        int[] board = {1,0,4,
                       3,5,6,
                       2,0,7};
        int[] goal = {1,2,3,4,5,6,7,0,0};

        State p = new PuzzleState(board, 3, 3, 2, 0,goal);
        State g = new PuzzleState(goal, 3, 3, 2, 0, goal);
//        System.out.println(DFBnB(p, g));
//        System.out.println("Num: " + numOfStates);
//        System.out.println("Cost: " + cost);
//
            Algo a = new Algo("DFBnB", false, true);
            a.run(p,g);

    }
}
