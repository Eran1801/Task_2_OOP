package api;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DWGraph_Algo implements dw_graph_algorithms {

	DWGraph_DS graph; //represents a pointer to the original graph.

	@Override
	public void init(directed_weighted_graph g) {
		graph = (DWGraph_DS) g;
	}

	@Override
	public directed_weighted_graph getGraph() {
		return this.graph;
	}

	@Override
	public directed_weighted_graph copy() {
		return this.graph.deepCopy();
	}

	@Override
	public boolean isConnected() {
		int numOfNodes = this.graph.getV().size(); //get number of nodes in the original graph
		if (numOfNodes == 0 || numOfNodes == 1 ) return true; //if the number of nodes is less than 2, the graph is surely connected.
		if (this.graph.edgeSize() < numOfNodes-1) return false; //if the number of edges is less than the number of nodes -1, the graph is surely no connected.

		//if we reached here, we can't tell if the graph is connected by the number of edges & number of nodes
		//so we check using BFS algorithm.
		Iterator<node_data> itr = this.graph.getV().iterator(); //we make an iterator to get list of nodes keys, then we will set the starting node to each key.
		int[] nodesKeysArr = new int[this.graph.nodeSize()];
		int arrayIndex = 0;
		while (itr.hasNext()) { // insert all the nodesKeys to the array
			nodesKeysArr[arrayIndex++] = itr.next().getKey();
		}
		for (int nodeKey : nodesKeysArr) {
			DWGraph_DS g = (DWGraph_DS)this.copy();
			NodeData startingNode = (NodeData)g.getNode(nodeKey);
			Queue<NodeData> q = new LinkedList<NodeData>();
			q.add(startingNode); //add the first node to the queue
			int numOfNodesInSubGraph = 1; //a counter for how many nodes we went through in BFS, in the end of the BFS we compare this to the graph size
			startingNode.setInfo("BLACK"); //mark the node black, so we know we already visited this node.

			while(!q.isEmpty()) {
				NodeData currentNode = q.remove();
				for (edge_data edge : currentNode.getNi().values()) { //iterate through all of the neighbors that we haven't visited yet.
					NodeData nodeNeighbor = (NodeData)g.getNode(edge.getDest());
					if (nodeNeighbor.getInfo() != "BLACK") { //if it's not black, than we haven't visited that node yet
						numOfNodesInSubGraph++;
						nodeNeighbor.setInfo("BLACK"); //mark the node black, so we know we already visited this node.
						q.add(nodeNeighbor);
					}
				}
			}
			if (g.nodeSize() != numOfNodesInSubGraph) return false; //if the number of nodes in the graph does not equals the number of nodes we visited using BFS - the graph is surely not connected.
		}
		return true;
	}

	@Override
	public double shortestPathDist(int src, int dest) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<node_data> shortestPath(int src, int dest) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean save(String file) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean load(String file) {
		// TODO Auto-generated method stub
		return false;
	}
}
