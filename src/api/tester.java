package api;

public class tester {
	public static void main(String[] args) {

		DWGraph_DS g = new DWGraph_DS();

		dw_graph_algorithms gAlgo = new DWGraph_Algo();
		gAlgo.init(g);
		
		NodeData node0 = new NodeData(0);
		NodeData node1 = new NodeData(1);
		NodeData node2 = new NodeData(2);
		NodeData node3 = new NodeData(3);
		NodeData node4 = new NodeData(4);
		NodeData node5 = new NodeData(5);
		NodeData node6 = new NodeData(6);
		
		g.addNode(node0);
		g.addNode(node1);
		g.addNode(node2);
		g.addNode(node3);
		g.addNode(node4);
		g.addNode(node5);
		g.addNode(node6);
		
		
		g.connect(node0.getKey(), node1.getKey(), 3);
		g.connect(node0.getKey(), node2.getKey(), 1);
		g.connect(node1.getKey(), node4.getKey(), 7);
		g.connect(node2.getKey(), node5.getKey(), 3);
		g.connect(node3.getKey(), node2.getKey(), 2);
		g.connect(node3.getKey(), node6.getKey(), 4);
		g.connect(node3.getKey(), node5.getKey(), 1);
		g.connect(node4.getKey(), node3.getKey(), 3);
		
		
		System.out.println(gAlgo.shortestPathDist(0,3));


		DWGraph_DS g2 = new DWGraph_DS();
		DWGraph_Algo g2Algo = new DWGraph_Algo();

		node0 = new NodeData(0);
		node1 = new NodeData(1);
		node2 = new NodeData(2);
		node3 = new NodeData(3);
		node4 = new NodeData(4);

		g2.addNode(node0);
		g2.addNode(node1);
		g2.addNode(node2);
		g2.addNode(node3);
		g2.addNode(node4);

		g2.connect(node0.getKey(), node2.getKey(), 1);
		g2.connect(node2.getKey(), node4.getKey(), 1);
		g2.connect(node4.getKey(), node3.getKey(), 1);
		g2.connect(node3.getKey(), node1.getKey(), 1);
		g2.connect(node1.getKey(), node2.getKey(), 1);
		g2.connect(node1.getKey(), node0.getKey(), 1);

		g2Algo.init(g2);
		System.out.println(g2Algo.isConnected());

		g2.removeEdge(1, 0);

		System.out.println(g2Algo.isConnected());
		
	}

}
