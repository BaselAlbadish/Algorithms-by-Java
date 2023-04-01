import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class Node implements Comparable<Node> {
    private static int idCounter = 0;
    int id;
    Node parent = null;
    List<Edge> neighbors;
    double f = Double.MAX_VALUE;
    double g = Double.MAX_VALUE;
    String name;
    double h;

    Node(double h,String name) {
        this.h = h;
        this.name = name;
        this.id = idCounter++;
        this.neighbors = new ArrayList<>();
    }

    @Override
    public int compareTo(Node n) {
        return Double.compare(this.f, n.f);
    }

    public static class Edge {
        Edge(int weight, Node node) {
            this.weight = weight;
            this.node = node;
        }

        public int weight;
        public Node node;
    }

    public void addBranch(int weight, Node node) {
        Edge newEdge = new Edge(weight, node);
        neighbors.add(newEdge);
    }

    public double calculateHeuristic(Node target) {
        Node n = target;
        double heuristic = 0;

        if (n == null)
            return this.h;

        while (n.parent != null) {
            heuristic = heuristic + n.h;
            n = n.parent;
        }
        return heuristic;
    }
}

class AStar {

    static Node aStar(Node start, Node target) {
        PriorityQueue<Node> closedList = new PriorityQueue<>();
        PriorityQueue<Node> openList = new PriorityQueue<>();

        start.f = start.g + start.calculateHeuristic(target);
        openList.add(start);

        while (!openList.isEmpty()) {
            Node n = openList.peek();
            if (n == target) {
                return n;
            }

            for (Node.Edge edge : n.neighbors) {
                Node m = edge.node;
                double totalWeight = n.g + edge.weight;

                if (!openList.contains(m) && !closedList.contains(m)) {
                    m.parent = n;
                    m.g = totalWeight;
                    m.f = m.g + m.calculateHeuristic(target);
                    openList.add(m);
                } else {
                    if (totalWeight < m.g) {
                        m.parent = n;
                        m.g = totalWeight;
                        m.f = m.g + m.calculateHeuristic(target);

                        if (closedList.contains(m)) {
                            closedList.remove(m);
                            openList.add(m);
                        }
                    }
                }
            }

            openList.remove(n);
            closedList.add(n);
        }
        return null;
    }

    static void printPath(Node target) {
        Node n = target;

        if (n == null)
            return;

        List<Node> nodes = new ArrayList<>();

        while (n.parent != null) {
            nodes.add(n);
            n = n.parent;
        }
        nodes.add(n);
        Collections.reverse(nodes);

        for (Node node : nodes) {
            System.out.print(node.name + "--> ");
        }
        System.out.println("");
    }

    static void printCost(Node target) {
        Node n = target;

        if (n == null)
            return;

        List<Node> nodes = new ArrayList<>();

        while (n.parent != null) {
            nodes.add(n);
            n = n.parent;
        }
        nodes.add(n);
        Collections.reverse(nodes);
        double cost = 0;
        for (int i = 0; i < nodes.size(); i++) {
            for(int j = 0; j < nodes.get(i).neighbors.size() ; j++){
                if(nodes.get(i).neighbors.get(j).node == nodes.get(i+1)){
                    cost = cost + nodes.get(i).neighbors.get(j).weight;
                }
            }
        }
        System.out.print(cost + " ");
    }
}

class Main {
    public static void main(String[] args) {
        Node S = new Node(9,"S");
        S.g = 0;

        Node A = new Node(7,"A");
        Node B = new Node(4,"B");
        Node R = new Node(2,"R");
        Node D = new Node(1,"D");
        Node G = new Node(0,"G");

        S.addBranch(2, A);
        S.addBranch(2, B);
        A.addBranch(4, R);
        A.addBranch(10, G);
        B.addBranch(3, R);
        B.addBranch(6, D);
        R.addBranch(2, G);
        R.addBranch(3, D);
        D.addBranch(2, G);

        Node res = AStar.aStar(S, G);
        System.out.println("the path :");
        AStar.printPath(res);
        System.out.println("the path cost :");
        AStar.printCost(res);

    }
}