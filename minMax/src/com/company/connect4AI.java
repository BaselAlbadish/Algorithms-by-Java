package com.company;

import java.util.Arrays;
import java.util.ArrayList;
import java.awt.Point;
import java.util.*;

class State implements Cloneable {

    int rows, cols;
    char[][] board;

    /* basic methods for constructing and proper hashing of State objects */
    public State(int n_rows, int n_cols) {
        this.rows = n_rows;
        this.cols = n_cols;
        this.board = new char[n_rows][n_cols];

        //fill the board up with blanks
        for (int i = 0; i < n_rows; i++)
            for (int j = 0; j < n_cols; j++)
                this.board[i][j] = '.';
    }

    public boolean equals(Object obj) {
        //have faith and cast
        State other = (State) obj;
        return Arrays.deepEquals(this.board, other.board);
    }

    public int hashCode() {
        String b = "";
        for (int i = 0; i < board.length; i++)
            b += String.valueOf(board[0]);
        return b.hashCode();
    }

    public Object clone() throws CloneNotSupportedException {
        State new_state = new State(this.rows, this.cols);
        for (int i = 0; i < this.rows; i++)
            new_state.board[i] = (char[]) this.board[i].clone();
        return new_state;
    }


    /* returns a list of actions that can be taken from the current state
    actions are integers representing the column where a coin can be dropped */
    public ArrayList<Integer> getLegalActions() {
        ArrayList<Integer> actions = new ArrayList<Integer>();
        for (int j = 0; j < this.cols; j++)
            if (this.board[0][j] == '.')
                actions.add(j);
        return actions;
    }

    /* returns a State object that is obtained by the agent (parameter)
    performing an action (parameter) on the current state */
    public State generateSuccessor(char agent, int action) throws CloneNotSupportedException {

        int row;
        for (row = 0; row < this.rows && this.board[row][action] != 'X' && this.board[row][action] != 'O'; row++) ;
        State new_state = (State) this.clone();
        new_state.board[row - 1][action] = agent;

        return new_state;
    }

    /* Print's the current state's board in a nice pretty way */
    public void printBoard() {
        System.out.println(new String(new char[this.cols * 2]).replace('\0', '-'));
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                System.out.print(this.board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println(new String(new char[this.cols * 2]).replace('\0', '-'));
    }

    /* returns True/False if the agent(parameter) has won the game
    by checking all rows/columns/diagonals for a sequence of >=4 */
    public boolean isGoal(char agent) {

        String find = "" + agent + "" + agent + "" + agent + "" + agent;

        //check rows
        for (int i = 0; i < this.rows; i++)
            if (String.valueOf(this.board[i]).contains(find))
                return true;

        //check cols
        for (int j = 0; j < this.cols; j++) {
            String col = "";
            for (int i = 0; i < this.rows; i++)
                col += this.board[i][j];

            if (col.contains(find))
                return true;
        }

        //check diags
        ArrayList<Point> pos_right = new ArrayList<Point>();
        ArrayList<Point> pos_left = new ArrayList<Point>();

        for (int j = 0; j < this.cols - 4 + 1; j++)
            pos_right.add(new Point(0, j));
        for (int j = 4 - 1; j < this.cols; j++)
            pos_left.add(new Point(0, j));
        for (int i = 1; i < this.rows - 4 + 1; i++) {
            pos_right.add(new Point(i, 0));
            pos_left.add(new Point(i, this.cols - 1));
        }

        //check right diags
        for (Point p : pos_right) {
            String d = "";
            int x = p.x, y = p.y;
            while (true) {
                if (x >= this.rows || y >= this.cols)
                    break;
                d += this.board[x][y];
                x += 1;
                y += 1;
            }
            if (d.contains(find))
                return true;
        }

        //check left diags
        for (Point p : pos_left) {
            String d = "";
            int x = p.x, y = p.y;
            while (true) {
                if (y < 0 || x >= this.rows || y >= this.cols)
                    break;
                d += this.board[x][y];
                x += 1;
                y -= 1;
            }
            if (d.contains(find))
                return true;
        }
        return false;
    }

    public double evaluationFunction() {
        if (this.isGoal('O'))
            return 1000.0;
        if (this.isGoal('X'))
            return -1000.0;

        return 0.0;
    }

}

class miniMax {
    int depth;
    int BestAction;
    int counter = 0;

    public miniMax(int depth) {
        this.depth = depth;
    }

    public int getAction(State state) throws CloneNotSupportedException {
        System.out.println("Min-MAx ...................");
        double value = max_value(state, depth);
        return BestAction;
    }

    public double max_value(State state, int d) throws CloneNotSupportedException {
        ArrayList<Integer> children;
        if (d == 0)
            return state.evaluationFunction();
        else {
            children = state.getLegalActions();
            double smallValue = -10000000;
            double successState;
//            System.out.println("Depth: " + depth);
            for (Integer child : children) {
                successState = min_value(state.generateSuccessor('O', child), d - 1);
//                System.out.println("Child: " + child + " Evaluation: " + successState);
                counter++;
                if (successState > smallValue) {
                    smallValue = successState;
//                    System.out.println("Best: " + BestAction + " Best eval: " + smallValue);
                    if (d == depth) {
                        this.BestAction = child;
                    }
                }

            }
            return smallValue;
        }
    }

    public double min_value(State state, int d) throws CloneNotSupportedException {
        ArrayList<Integer> children;
        if (d == 0)
            return state.evaluationFunction();
        else {
            children = state.getLegalActions();
            double bigValue = 10000000;
            double successState;

            for (Integer child : children) {
                successState = max_value(state.generateSuccessor('X', child), d - 1);
                if (successState < bigValue)
                    bigValue = successState;
            }
            return bigValue;
        }
    }
}
class AlphaBeta {
    int depth;
    int BestAction;
    int counter = 0;

    public AlphaBeta(int depth) {
        this.depth = depth;
    }

    public int getAction(State state) throws CloneNotSupportedException {
        System.out.println("Alpha-Beta ...................");
        double value = max_min(state, depth, -1000000, 1000000);
        return BestAction;
    }

    public double max_min(State state, int d, int alpha, int beta) throws CloneNotSupportedException {
        ArrayList<Integer> children;
        if (d == 0)
            return state.evaluationFunction();
        else {
            children = state.getLegalActions();
            double smallValue = -10000000;
            double successState;
            for (Integer child : children) {
                successState = min_value(state.generateSuccessor('O', child), d - 1, alpha, beta);
                counter++;
                if (successState > smallValue) {
                    smallValue = successState;
                    if (d == depth) {
                        this.BestAction = child;
                    }
                }
                alpha = (int) Math.max(alpha, smallValue);
                if (beta <= alpha)
                    break;

            }
            return smallValue;
        }
    }

    public double min_value(State state, int d, int alpha, int beta) throws CloneNotSupportedException {
        ArrayList<Integer> children;
        if (d == 0)
            return state.evaluationFunction();
        else {
            children = state.getLegalActions();
            double bigValue = 10000000;
            double successState;
            for (Integer child : children) {
                successState = max_min(state.generateSuccessor('X', child), d - 1, alpha, beta);
                if (successState < bigValue)
                    bigValue = successState;

                beta = (int) Math.min(beta, bigValue);

                if (beta <= alpha)
                    break;
            }
            return bigValue;
        }
    }
}

public class connect4AI {

    public static void main(String[] args) throws CloneNotSupportedException {
        Scanner in = new Scanner(System.in);
        Random RN = new Random();

        System.out.println("Enter who is the second player :");
        System.out.println("0 for min-max or 1 for alpha-beta");
        String firstPlayer = in.next();
        System.out.println("Enter who is the first player :");
        System.out.println("user or random");
        String secondPlayer = in.next();

        System.out.println("Enter the depth:");
        int depth = in.nextInt();

        State s = new State(6, 7);
        miniMax MM = new miniMax(depth);
        AlphaBeta AP = new AlphaBeta(depth);


        while (true) {
            int action;
            if (firstPlayer.equals("0")) {
                action = MM.getAction(s);
            } else
                action = AP.getAction(s);

            s = s.generateSuccessor('O', action);
            s.printBoard();
            if (s.isGoal('O'))
                break;

            int enemy_move;
            if (secondPlayer.equals("user")) {
                enemy_move = in.nextInt();
                s = s.generateSuccessor('X', enemy_move);

            } else {
                while (true) {
                    enemy_move = RN.nextInt(100) % 7;
                    try {
                        s = s.generateSuccessor('X', enemy_move);
                        break;
                    } catch (IndexOutOfBoundsException ignored) {
                    }
                }
            }

            s.printBoard();
            if (s.isGoal('X'))
                break;
        }
        if (firstPlayer.equals("0"))
            System.out.println("number of nodes    :" + MM.counter);
        else
            System.out.println("number of nodes    :" + AP.counter);
    }
}