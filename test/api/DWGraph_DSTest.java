package api;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {

    //Creates a new 5 nodes graph
    directed_weighted_graph createSmallGraph() {
        directed_weighted_graph g = new DWGraph_DS();

        node_data n0 = new NodeData(0);
        node_data n1 = new NodeData(1);
        node_data n2 = new NodeData(2);
        node_data n3 = new NodeData(3);
        node_data n4 = new NodeData(4);
        node_data n5 = new NodeData(5);
        node_data n6 = new NodeData(6);
        node_data n7 = new NodeData(7);
        node_data n8 = new NodeData(8);
        node_data n9 = new NodeData(9);
        node_data n10 = new NodeData(10);

        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);
        g.addNode(n6);
        g.addNode(n7);
        g.addNode(n8);
        g.addNode(n9);
        g.addNode(n10);

        return g;
    }


    @BeforeAll
    public static void start_program() {
        System.out.println();
        System.out.println("---- Starts the test for WGraph_DS class ----");
    }

    @Test
    void getNode() {
        directed_weighted_graph g = new DWGraph_DS();

        node_data n0 = new NodeData(0);
        node_data n1 = new NodeData(1);
        node_data n2 = new NodeData(2);
        node_data n3 = new NodeData(3);
        node_data n4 = new NodeData(4);
//        node_data n5 = new NodeData(5);
//        node_data n6 = new NodeData(6);
//        node_data n7 = new NodeData(7);
//        node_data n8 = new NodeData(8);
//        node_data n9 = new NodeData(9);
//        node_data n10 = new NodeData(10);

        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
//        g.addNode(n5);
//        g.addNode(n6);
//        g.addNode(n7);
//        g.addNode(n8);
//        g.addNode(n9);
//        g.addNode(n10);

        node_data getN0 = g.getNode(0);
        assertEquals(n0, getN0);
    }

    @Test
    void getEdge() {
        directed_weighted_graph g = createSmallGraph();
        g.connect(0, 1, 1.2);
        g.connect(0, 3, 2.5);
        g.connect(1, 2, 3.5);
        g.connect(2, 4, 1.5);
        g.connect(4, 0, 2.0);
        g.connect(3, 2, 2.5);

        edge_data edge1 = g.getEdge(0, 3);
        assertEquals(edge1.getSrc(), 0);
        assertEquals(edge1.getDest(), 3);
        assertEquals(edge1.getWeight(), 2.5);

        g.removeEdge(0, 3);
        edge_data edge2 = g.getEdge(0, 3);
        assertEquals(null, edge2);

        g.connect(0, 3, 3.6);
        edge_data edge3 = g.getEdge(0, 3);
        assertEquals(edge3.getWeight(), 3.6);

    }


    @Test
    void removeNode() {

        directed_weighted_graph g = new DWGraph_DS();

        node_data n0 = new NodeData(0);
        node_data n1 = new NodeData(1);
        node_data n2 = new NodeData(2);
        node_data n3 = new NodeData(3);
        node_data n4 = new NodeData(4);
        node_data n5 = new NodeData(5);
        node_data n6 = new NodeData(6);
        node_data n7 = new NodeData(7);
        node_data n8 = new NodeData(8);
        node_data n9 = new NodeData(9);
        node_data n10 = new NodeData(10);

        g.addNode(n0);
        g.addNode(n1);
        g.addNode(n2);
        g.addNode(n3);
        g.addNode(n4);
        g.addNode(n5);
        g.addNode(n6);
        g.addNode(n7);
        g.addNode(n8);
        g.addNode(n9);
        g.addNode(n10);

        node_data removedNode = g.removeNode(0);
        assertEquals(removedNode, n0);
        removedNode = g.removeNode(0);
        assertEquals(removedNode, null);
    }

    @Test
    void removeEdge() {

        directed_weighted_graph g = createSmallGraph();
        g.connect(0, 1, 1.2);
        g.connect(0, 3, 2.5);
        g.connect(1, 2, 3.5);
        g.connect(2, 4, 1.5);
        g.connect(4, 0, 2.0);
        g.connect(3, 2, 2.5);

        edge_data edge1 = g.removeEdge(0, 3);
        assertEquals(edge1.getSrc(), 0);
        assertEquals(edge1.getDest(), 3);
        assertEquals(edge1.getWeight(), 2.5);

        edge1 = g.removeEdge(0, 3);
        assertEquals(null, edge1);

    }

    @Test
    void test_For_Nodes_Edges_And_Mc() {

        directed_weighted_graph g = createSmallGraph();
        g.connect(0, 1, 1.2);
        g.connect(0, 3, 2.5);
        g.connect(1, 2, 3.5);
        g.connect(2, 4, 1.5);
        g.connect(4, 0, 2.0);
        g.connect(3, 2, 2.5);

        int nodeSize = g.nodeSize();
        int numberOfEdges = g.edgeSize();
        int mcCounter = g.getMC();
        assertEquals(11, nodeSize);
        assertEquals(6, numberOfEdges);
        assertEquals(17, mcCounter);

        g.removeNode(3);
        nodeSize = g.nodeSize();
        numberOfEdges = g.edgeSize();
        mcCounter = g.getMC();
        assertEquals(10, nodeSize);
        assertEquals(4, numberOfEdges);
        assertEquals(20, mcCounter);

        g.removeNode(3);
        nodeSize = g.nodeSize();
        numberOfEdges = g.edgeSize();
        mcCounter = g.getMC();
        assertEquals(10, nodeSize);
        assertEquals(4, numberOfEdges);
        assertEquals(20, mcCounter);


    }

    @AfterAll
    public static void endProgram() {

        Random rnd = new Random(20);
        int v = 1000000, e = 1000000;
        double start = System.currentTimeMillis();
        directed_weighted_graph graph = new DWGraph_DS();
        for (int i = 0; i < v; i++) {
            node_data n1 = new NodeData(i);
            graph.addNode(n1);
        }
        int n1 = 0, n2 = 0, n3 = 0, n4 = 0;
        for (int i = 0; i < e; i++) {
            double w = Math.random() * 20;
            n1 = rnd.nextInt((v - 1));
            n2 = rnd.nextInt((v - 1));
            n3 = rnd.nextInt((v - 1));
            graph.connect(n1, n2, w);
            graph.connect(n2, n3, w);
            graph.connect(n4, n1, w);
        }
        double end = System.currentTimeMillis();
        System.out.println("The program time running is -> " + (end - start) / 1000.0);
        System.out.println();
        System.out.println("--- Test for WGraph_DS end successfully ---");
    }

}