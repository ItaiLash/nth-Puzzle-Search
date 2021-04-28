import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

public class Algo {
    public static ArrayList<State> BFS(State start) {
        Hashtable open = new Hashtable();
        Queue<State> q = new LinkedList<>();
        Hashtable close = new Hashtable();
        open.put(start.toString(), start);
        q.add(start);
        while (!q.isEmpty()) {
            State curState = q.poll();
            open.remove(curState.toString());
            close.put(curState.toString(), curState);
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                if (!q.contains(next) && !close.containsKey(next.toString())) {
                    if (next.isGoal()) {
                        return next.getPath();
                    }
                    open.put(next.toString(), next);
                    q.add(next);
                }
            }
        }
        return null;
    }

    public static ArrayList<State> DFID(PuzzleState start) {
        boolean goalFound = false;
        int cutoff = 0;
        for (int depth=1 ; !goalFound ; depth++){
            Hashtable h = new Hashtable();
            ArrayList<State> result = Limited_DFS(start,depth,h,cutoff);
            if(cutoff != 0){
                return result;
            }
        }
        return null;
    }


    //-1 = fail
    //0 = cutoff
    //1 = pass
    public static ArrayList<State> Limited_DFS(State curState, int limit, Hashtable h, int cutoff) {
        if(curState.isGoal()){
            return curState.getPath();
        }
        else if(limit == 0){
            cutoff = 0;
            return null;
        }
        else {
            h.put(curState.toString(), curState);
            cutoff = 2;
            ArrayList<State> suc = curState.genSuccessors();
            for (State next : suc) {
                if (!h.containsKey(next.toString())) {
                    ArrayList<State> result = Limited_DFS(next, limit - 1, h, cutoff);
                    if (cutoff == 0) {
                        cutoff = 0;
                    } else if(cutoff != -1) {
                        return result;
                    }
                }
            }
            h.remove(curState.toString());
            if (cutoff == 1){
                return null;
            }
            else{
                cutoff = -1;
                return null;
            }
        }
    }



    public static void main(String[] args) {
        int board[] = {1,2,0,4,5,6,3,8,9,0,7,10};
        State p = new PuzzleState(board, 3, 4, 2, 0);
        p.printState();
//        System.out.println("\t|\t");
//        System.out.println("\tV\t");
//        for (int i=0 ; i<50 ; i++){
//            ArrayList<State> mix = p.genSuccessors();
//            p = mix.get((int)(Math.random()*mix.size()));
//            p.printState();
//            System.out.println("\t|\t");
//            System.out.println("\tV\t");
//        }
        ArrayList<State> ans = BFS(p);
        System.out.println("\nAlgo return");
       //ArrayList<State> ans = DFID(p);
        for (State cur : ans){
            cur.printState();
            System.out.println("\t|\t");
            System.out.println("\tV\t");
        }
    }
}
