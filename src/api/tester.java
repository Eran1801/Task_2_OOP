package api;

public class tester {
	public static void main(String[] args) {

		DWGraph_DS g = new DWGraph_DS();
		
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
		
		
		g.connect(node0.getKey(), node1.getKey(), 1);
		g.connect(node0.getKey(), node2.getKey(), 2);
		g.connect(node1.getKey(), node4.getKey(), 3);
		g.connect(node2.getKey(), node5.getKey(), 4);
		g.connect(node3.getKey(), node2.getKey(), 5);
		g.connect(node3.getKey(), node6.getKey(), 6);
		g.connect(node3.getKey(), node5.getKey(), 7);
		
		
		System.out.println(g.getEdge(0, 2).getWeight());
		System.out.println(g.getEdge(3, 2).getWeight());
		System.out.println(g.getEdge(2, 5).getWeight());
		
		System.out.println(g.edgeSize());
		g.removeEdge(2, 5);
		System.out.println(g.getEdge(2, 5));
		System.out.println(g.edgeSize());
		System.out.println(g.removeEdge(2, 5));

		
	}

}
