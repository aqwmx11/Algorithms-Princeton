/* *****************************************************************************
 *  Name: Mingxuan Wu
 *  Date: 2020-08-31
 *  Description: Implementation of using maxflow to eliminate baseball teams
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.HashMap;

public class BaseballElimination {
    private final int numberOfTeams;
    private final String[] teams;
    private final int[] wins;
    private final int[] losses;
    private final int[] remaining;
    private final int[][] against;
    private final HashMap<String, Integer> teamIds;
    private int maxWin; // used in trivial elimination
    private String bestTeam; // used in trivial elimination
    private int totalAgainst;

    // create a baseball division from given filename in format specified below
    public BaseballElimination(String filename) {
        if (filename == null) throw new IllegalArgumentException();
        In in = new In(filename);
        numberOfTeams = in.readInt();
        teams = new String[numberOfTeams];
        teamIds = new HashMap<>();
        wins = new int[numberOfTeams];
        losses = new int[numberOfTeams];
        remaining = new int[numberOfTeams];
        against = new int[numberOfTeams][numberOfTeams];
        maxWin = 0;
        bestTeam = null;
        for (int i = 0; i < numberOfTeams; i++) {
            teams[i] = in.readString();
            teamIds.put(teams[i], i);
            wins[i] = in.readInt();
            if (wins[i] > maxWin) {
                maxWin = wins[i];
                bestTeam = teams[i];
            }
            losses[i] = in.readInt();
            remaining[i] = in.readInt();
            for (int j = 0; j < numberOfTeams; j++) {
                against[i][j] = in.readInt();
            }
        }
    }

    // number of teams
    public int numberOfTeams() {
        return numberOfTeams;
    }

    // all teams
    public Iterable<String> teams() {
        return Arrays.asList(teams);
    }

    // number of wins for given team
    public int wins(String team) {
        if (team == null) throw new IllegalArgumentException();
        if (!teamIds.containsKey(team)) throw new IllegalArgumentException();
        int teamId = teamIds.get(team);
        return wins[teamId];
    }

    // number of losses for given team
    public int losses(String team) {
        if (team == null) throw new IllegalArgumentException();
        if (!teamIds.containsKey(team)) throw new IllegalArgumentException();
        int teamId = teamIds.get(team);
        return losses[teamId];
    }

    // number of remaining games for given team
    public int remaining(String team) {
        if (team == null) throw new IllegalArgumentException();
        if (!teamIds.containsKey(team)) throw new IllegalArgumentException();
        int teamId = teamIds.get(team);
        return remaining[teamId];
    }

    // number of remaining games between team1 and team2
    public int against(String team1, String team2) {
        if (team1 == null || team2 == null) throw new IllegalArgumentException();
        if (!teamIds.containsKey(team1)) throw new IllegalArgumentException();
        if (!teamIds.containsKey(team2)) throw new IllegalArgumentException();
        int teamId1 = teamIds.get(team1);
        int teamId2 = teamIds.get(team2);
        return against[teamId1][teamId2];
    }

    // helper function to generate maxflow by Ford-Fulkerson algorithm
    private FordFulkerson generateFF(String team) {
        int teamId = teamIds.get(team);
        FlowNetwork FN = new FlowNetwork(
                numberOfTeams + numberOfTeams * numberOfTeams + 2);
        // vertices index: teams 0 - n-1, matches 0+n - n^2-1+n, s n^2+n, t n^2+n+1
        // step 1: construct edges from S to all remaining matches
        int sIndex = numberOfTeams * numberOfTeams + numberOfTeams;
        totalAgainst = 0; // used to check if we can eliminate
        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i == teamId || j == teamId) continue; // we do not consider current team
                int matchIndex = i * numberOfTeams + j + numberOfTeams;
                FN.addEdge(new FlowEdge(sIndex, matchIndex, against[i][j]));
                totalAgainst += against[i][j];
            }
        }
        // step 2: construct edges from remaining matches to each team
        for (int i = 0; i < numberOfTeams; i++) {
            for (int j = i + 1; j < numberOfTeams; j++) {
                if (i == teamId || j == teamId) continue; // we do not consider current team
                int matchIndex = i * numberOfTeams + j + numberOfTeams;
                FN.addEdge(new FlowEdge(matchIndex, i, Double.POSITIVE_INFINITY));
                FN.addEdge(new FlowEdge(matchIndex, j, Double.POSITIVE_INFINITY));
            }
        }
        // step 3: construct edges from teams to t
        int tIndex = sIndex + 1;
        int capacity = wins(team) + remaining(team);
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamId) continue; // we do not consider current team
            FN.addEdge(new FlowEdge(i, tIndex, capacity - wins[i]));
        }
        FordFulkerson FF = new FordFulkerson(FN, sIndex, tIndex);
        return FF;
    }

    // is given team eliminated?
    public boolean isEliminated(String team) {
        if (team == null) throw new IllegalArgumentException();
        // first see if we can eliminate it trivially
        if (wins(team) + remaining(team) < maxWin) return true;
        // next, check nontrivial elimination
        FordFulkerson FF = generateFF(team);
        return FF.value() < totalAgainst;
    }

    // subset R of teams that eliminates given team; null if not eliminated
    public Iterable<String> certificateOfElimination(String team) {
        if (team == null) throw new IllegalArgumentException();
        // first see if we can eliminate it trivially
        Queue<String> r = new Queue<>();
        if (wins(team) + remaining(team) < maxWin) {
            r.enqueue(bestTeam);
            return r;
        }
        // next, check nontrivial elimination
        int teamId = teamIds.get(team);
        FordFulkerson FF = generateFF(team);
        if (FF.value() >= totalAgainst) return null;
        for (int i = 0; i < numberOfTeams; i++) {
            if (i == teamId) continue;
            if (FF.inCut(i)) r.enqueue(teams[i]);
        }
        return r;
    }

    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        for (String team : division.teams()) {
            if (division.isEliminated(team)) {
                StdOut.print(team + " is eliminated by the subset R = { ");
                for (String t : division.certificateOfElimination(team)) {
                    StdOut.print(t + " ");
                }
                StdOut.println("}");
            }
            else {
                StdOut.println(team + " is not eliminated");
            }
        }
    }
}
