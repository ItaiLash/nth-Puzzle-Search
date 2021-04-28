import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Algo {

    public static int numOfStates = 0;
    public static int cost = 0;

    public static String BFS(State start, State goal) {
        numOfStates = 0;
        Hashtable<String, State> open = new Hashtable();
        Queue<State> q = new LinkedList<>();
        Hashtable<String, State> close = new Hashtable();
        open.put(start.toString(), start);
        q.add(start);
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
        return null;
    }

    public static String DFID(State start, State goal) {
        numOfStates = 0;
        for (int depth=1 ;  ; depth++){
            Hashtable h = new Hashtable();
            String result = Limited_DFS(start,goal,depth,h);
            if(!result.equals("cutoff")){
                return result;
            }
        }
    }


    //-1 = fail
    //0 = cutoff
    //1 = pass
    public static String Limited_DFS(State curState, State goal, int limit, Hashtable h) {
        if(curState.equals(goal)){
            cost = curState.getCost();
            return curState.getStringPath();
        }
        else if(limit == 0){
            return "cutoff";
        }
        else {
            h.put(curState.toString(), curState);
            boolean isCutoff = false;
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                numOfStates++;
                if (!h.containsKey(next.toString())) {
                    String result = Limited_DFS(next, goal, limit - 1, h);
                    if(result.equals("cutoff")){
                        isCutoff =true;
                    } else if(!result.equals("fail")) {
                        return result;
                    }
                }
            }
            h.remove(curState.toString());
            if (isCutoff){
                return "cutoff";
            }
            else{
                return "fail";
            }
        }
    }



    public static void main(String[] args) {
        int board[] = {1,2,3,4,5,6,11,7,9,10,8,0};
        State p = new PuzzleState(board, 3, 4, 1, 0);
        int goal[] = {1,2,3,4,5,6,7,8,9,10,11,0};
        State g = new PuzzleState(goal, 3, 4, 1, 0);
        p.printState();
        System.out.println(BFS(p, g));
        System.out.println("Num: " + numOfStates);
        System.out.println("Cost: " + cost);
        System.out.println();
        System.out.println(DFID(p, g));
        System.out.println("Num: " + numOfStates);
        System.out.println("Cost: " + cost);

    }
}
