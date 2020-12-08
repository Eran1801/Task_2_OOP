package api;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_AlgoTest {

    final double epsilon = 0.0000001;

    //Creates a new 6 nodes graph with some connections
    directed_weighted_graph createSmallGraphWithSomeConnections() {
        directed_weighted_graph g = new DWGraph_DS();

        node_data n0 = new NodeData(0);
        node_data n1 = new NodeData(1);
        node_data n2 = new NodeData(2);
        node_data n3 = new NodeData(3);
        node_data n4 = new NodeData(4);
        node_data n5 = new NodeData(5);

        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);

        g.connect(0, 1, 1.0);
        g.connect(0, 3, 2.5);
        g.connect(1, 2, 3.5);
        g.connect(2, 4, 1.5);
        g.connect(4, 0, 2.0);
        g.connect(3, 2, 2.5);

        return g;
    }

    @BeforeAll
    public static void startProgram() {
        System.out.println();
        System.out.println("---- Starts the test for DWGraph_Algo class ----");
    }

    @Test
    void isConnected() {

        directed_weighted_graph g = createSmallGraphWithSomeConnections();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);

        assertEquals(ga.isConnected(), false);

        g.connect(0, 5, 2.6);
        assertEquals(ga.isConnected(), false);

        g.connect(5, 0, 4.5);
        assertEquals(ga.isConnected(), true);

        g.removeEdge(4, 0);
        assertEquals(ga.isConnected(), false);

    }

    @Test
    void shortestPathDist() {

        directed_weighted_graph g = createSmallGraphWithSomeConnections();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);

        assertEquals(ga.shortestPathDist(2, 5), -1, epsilon);

        g.connect(5, 0, 4.5);
        assertEquals(ga.shortestPathDist(2, 5), -1, epsilon);


        g.connect(0, 5, 2.6);
        assertEquals(ga.shortestPathDist(2, 5), 6.1, epsilon);

        g.removeEdge(4, 0);
        assertEquals(ga.shortestPathDist(2, 5), -1, epsilon);

        assertEquals(ga.shortestPathDist(2, 2), 0, epsilon); //there's no path from a node to itself

    }

    @Test
    void shortestPath() {

        directed_weighted_graph g = createSmallGraphWithSomeConnections();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);

        List<node_data> list = ga.shortestPath(2, 5);
        assertEquals(list, null);

        g.connect(5, 0, 4.5);
        list = ga.shortestPath(2, 5);
        assertEquals(list, null);

        g.connect(0, 5, 2.6);
        list = ga.shortestPath(2, 5);
        List<node_data> compareList = new LinkedList<>();
        compareList.add(g.getNode(2));
        compareList.add(g.getNode(4));
        compareList.add(g.getNode(0));
        compareList.add(g.getNode(5));
        for (int i = 0; i < compareList.size(); i++) {
            assertEquals(compareList.get(i), list.get(i));
        }

        g.removeEdge(4, 0);
        list = ga.shortestPath(2, 5);
        assertEquals(list, null);
    }

    @Test
    void save_and_load() {
        directed_weighted_graph g = createSmallGraphWithSomeConnections();
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);

        ga.save("graph.json");

        assertEquals(ga.getGraph().getEdge(0, 3).getWeight(), 2.5, epsilon);

        g.connect(0, 3, 3.5);

        assertEquals(ga.getGraph().getEdge(0, 3).getWeight(), 3.5, epsilon);

        ga.load("graph.json");

        assertEquals(ga.getGraph().getEdge(0, 3).getWeight(), 2.5, epsilon);
    }
}
