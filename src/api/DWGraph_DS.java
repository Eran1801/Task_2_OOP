package api;


import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class DWGraph_DS implements directed_weighted_graph {

    private int numOfEdges = 0;
    private int modeCount = 0; //represents number of changes.
    private HashMap<Integer, node_data> nodes;

    public DWGraph_DS() {
        this.nodes = new HashMap<Integer, node_data>();
    }

    public DWGraph_DS(directed_weighted_graph graph) {
        this.numOfEdges = graph.edgeSize();
        this.modeCount = graph.getMC();
    }


    @Override
    public node_data getNode(int key) {
        return nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        if (!nodes.containsKey(src)) return null;
        node_data sourceNode = nodes.get(src);
        return ((NodeData) sourceNode).getEdge(dest);
    }

    @Override
    public void addNode(node_data n) {
        this.modeCount += this.nodes.putIfAbsent(n.getKey(), n) == null ? 1 : 0; // if the node was already in the graph - it will simply do nothing, if it wasn't - it will add it to the graph and increment modeCount by 1
    }

    @Override
    public void connect(int src, int dest, double w) {
        if (src == dest) return; //do nothing if trying to connect a node to itself
        if (w < 0) return; //do nothing if weight is less than 0
        NodeData sourceNode = (NodeData) this.nodes.get(src);
        NodeData destNode = (NodeData) this.nodes.get(dest);
        numOfEdges += sourceNode.hasNi(destNode.getKey()) ? 0 : 1; // add 1 to numOfEdges if there is no connection yet.
        sourceNode.connectEdge(destNode, w); //connect source node to dest node / update weight if already connected.
        modeCount++;
    }

    @Override
    public Collection<node_data> getV() {
        return this.nodes.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        return ((NodeData) this.nodes.get(node_id)).getNeighborEdges().values();
    }

    @Override
    public node_data removeNode(int key) {
        if (!this.nodes.containsKey(key))
            return null; // return null if the node we wish to remove does not exist in the graph

        NodeData nodeToRemove = (NodeData) this.nodes.get(key);
        this.numOfEdges -= nodeToRemove.getNeighborEdges().size();
        this.numOfEdges -= nodeToRemove.getEdgesConnectedToThisNode().size();

        Iterator<edge_data> sourceItr = nodeToRemove.getEdgesConnectedToThisNode().values().iterator();

        while (sourceItr.hasNext()) { // We are iterating over all edges directed at the node that we want to remove
            EdgeData edgeToRemove = (EdgeData) sourceItr.next();
            ((NodeData) (nodes.get(edgeToRemove.getSrc()))).getNeighborEdges().remove(edgeToRemove.getDest()); // Remove the edge from a node that directs to this node
            this.modeCount++;
        }

        Iterator<edge_data> destItr = getE(nodeToRemove.getKey()).iterator();

        while (destItr.hasNext()) { //We are iterating over all edges coming from the node that we want to remove
            EdgeData edgeToRemove = (EdgeData) destItr.next();
            NodeData nodeConnectedFromThisNode = (NodeData) nodes.get(edgeToRemove.getDest()); //get the node that the edge is directed at (the destination node)
            nodeConnectedFromThisNode.getEdgesConnectedToThisNode().remove(nodeToRemove.getKey()); //remove the edge from the destination node
            nodeToRemove.getNeighborEdges().remove(edgeToRemove.getDest()); //remove the edge from the source node
            this.modeCount++;
        }

        this.modeCount++;
        return this.nodes.remove(key);

    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        NodeData nodeToRemoveEdgeFrom = (NodeData) this.nodes.get(src);
        if (!nodeToRemoveEdgeFrom.hasNi(dest)) return null; //return null if there is no edge from src to dest
        EdgeData edgeToRemove = (EdgeData) nodeToRemoveEdgeFrom.getEdge(dest);
        NodeData nodeConnectedFromThisNode = (NodeData) nodes.get(edgeToRemove.getDest()); //get the node that the edge is directed at (the destination node)
        nodeConnectedFromThisNode.getEdgesConnectedToThisNode().remove(nodeToRemoveEdgeFrom.getKey()); //remove the edge from the destination node
        nodeToRemoveEdgeFrom.getNeighborEdges().remove(edgeToRemove.getDest()); //remove the edge from the source node
        this.modeCount++;
        this.numOfEdges--;
        return edgeToRemove;
    }

    @Override
    public int nodeSize() {
        return this.nodes.size();
    }

    @Override
    public int edgeSize() {
        return this.numOfEdges;
    }

    @Override
    public int getMC() {
        return this.modeCount;
    }

    public directed_weighted_graph deepCopy() {
        DWGraph_DS copyGraph = new DWGraph_DS(this); //create a new graph with the original graph data (only primitives)
        HashMap<Integer, node_data> copyNodesMap = new HashMap<Integer, node_data>(); //create a new nodes HashMap for the new graph
        for (node_data node : nodes.values()) { //loop through all nodes in the original graph
            copyNodesMap.put(node.getKey(), new NodeData((NodeData) node)); //makes a duplicate of the original HashMap
        }
        copyGraph.nodes = copyNodesMap; //set the new graph nodes to the new HashMap we made.
        return copyGraph;
    }
    public node_data copyNode(node_data node){
        NodeData nodeUpCasted = (NodeData)node;
        return new NodeData(nodeUpCasted);
    }
}

// ---------------------------------------------------------------------------------------------------------------------------------------------------------------

class NodeData implements node_data {

    private int key;
    private HashMap<Integer, edge_data> neighborEdges; //edges coming out from this node.
    private HashMap<Integer, edge_data> edgesConnectedToThisNode; // when we want to remove a node, we need to have a reference to the nodes that are connected to this node.
    private double weight;
    private String info; //represents if we visited the node in algorithms (WHITE = Not visited, BLACK= = visited)
    private int tag; //represents parent key in algorithms
    private Location location;


    public NodeData(NodeData node) { // Constructor for the DeepCopy
        this.key = node.key;
        this.info = node.info;
        this.tag = node.tag;
        this.weight = node.weight;
        this.location = node.location;

        this.neighborEdges = new HashMap<Integer, edge_data>();
        for (edge_data edge : node.neighborEdges.values()) {
            this.neighborEdges.put(edge.getDest(), new EdgeData((EdgeData) edge));
        }

        this.edgesConnectedToThisNode = new HashMap<Integer, edge_data>();
        for (edge_data edge : node.edgesConnectedToThisNode.values()) {
            this.edgesConnectedToThisNode.put(edge.getSrc(), new EdgeData((EdgeData) edge));
        }
    }

    public NodeData(int key) {
        this.key = key;
        this.neighborEdges = new HashMap<Integer, edge_data>();
        this.edgesConnectedToThisNode = new HashMap<Integer, edge_data>();
        this.weight = Double.MAX_VALUE;
        this.info = "WHITE";
        this.tag = -1;
        this.location = new Location(0, 0, 0);
    }

    public void connectEdge(NodeData destNode, double w) {
        EdgeData edge = new EdgeData(this.getKey(), destNode.getKey(), w);
        this.neighborEdges.put(destNode.getKey(), edge);
        destNode.edgesConnectedToThisNode.put(this.getKey(), edge);
    }

    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public geo_location getLocation() {
        return location;
    }

    @Override
    public void setLocation(geo_location p) {
        this.location.setX(p.x());
        this.location.setY(p.y());
        this.location.setZ(p.z());
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight = w;

    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;

    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;

    }

    public boolean hasNi(int nodeKey) {
        return this.neighborEdges.get(nodeKey) != null ? true : false;
    }

    public HashMap<Integer, edge_data> getNi() {
        return neighborEdges;
    }

    public edge_data getEdge(int nodeKey) {
        return this.neighborEdges.get(nodeKey);
    }

    public HashMap<Integer, edge_data> getNeighborEdges() {
        return this.neighborEdges;
    }

    public HashMap<Integer, edge_data> getEdgesConnectedToThisNode() {
        return this.edgesConnectedToThisNode;
    }

    @Override
    public boolean equals(Object o) {
        NodeData compareToNode = (NodeData) o; //cast to NodeData

        //Compare keys (There is no need to compare other primitives that used for algorithms, because there is no need for them to be equal)
        if (this.getKey() != compareToNode.getKey())
            return false;

        //if size of neighborEdges is different between the two nodes
        if (this.neighborEdges.size() != compareToNode.neighborEdges.size()) return false;

        //loop through all neighborEdges in the original node and compare them to the other node neighborEdges (using keys to get the edge data)
        //(There is no need to compare other variables that used for algorithms, because there is no need for them to be equal)
        for (int edgeKey : this.neighborEdges.keySet()) {
            EdgeData originalEdge = (EdgeData) this.neighborEdges.get(edgeKey);
            EdgeData compareToEdge = (EdgeData) compareToNode.neighborEdges.get(edgeKey);
            if (originalEdge.getSrc() != compareToEdge.getSrc() || originalEdge.getDest() != compareToEdge.getDest()
                    || originalEdge.getWeight() != compareToEdge.getWeight())
                return false;
        }

        //if size of edgesConnectedToThisNode is different between the two nodes
        if (this.edgesConnectedToThisNode.size() != compareToNode.edgesConnectedToThisNode.size()) return false;

        //loop through all edgesConnectedToThisNode in the original node and compare them to the other node edgesConnectedToThisNode (using keys to get the edge data)
        //(There is no need to compare other variables that used for algorithms, because there is no need for them to be equal)
        for (int edgeKey : this.edgesConnectedToThisNode.keySet()) {
            EdgeData originalEdge = (EdgeData) this.edgesConnectedToThisNode.get(edgeKey);
            EdgeData compareToEdge = (EdgeData) compareToNode.edgesConnectedToThisNode.get(edgeKey);
            if (originalEdge.getSrc() != compareToEdge.getSrc() || originalEdge.getDest() != compareToEdge.getDest()
                    || originalEdge.getWeight() != compareToEdge.getWeight())
                return false;
        }
        return true;
    }

    public String toString() {
        return "" + this.key;
    }
}

//---------------------------------------------------------------------------------------------------------------------------------------------------------------

class EdgeData implements edge_data, Comparable<edge_data> {

    private int sourceKey;
    private int destKey;
    private double weight;
    private String info;
    private int tag;

    public EdgeData(int sourceKey, int destKey, double weight) {
        this.sourceKey = sourceKey;
        this.destKey = destKey;
        this.weight = weight;
        this.info = "WHITE";
        this.tag = 0;
    }

    public EdgeData(int sourceKey, int destKey, double weight, String info) {
        this.sourceKey = sourceKey;
        this.destKey = destKey;
        this.weight = weight;
        this.info = info;
        this.tag = 0;
    }

    public EdgeData(EdgeData edge) { // Constructor for the DeepCopy
        this.sourceKey = edge.sourceKey;
        this.destKey = edge.destKey;
        this.weight = edge.weight;
        this.info = edge.info;
        this.tag = edge.tag;
    }

    @Override
    public int getSrc() {
        return this.sourceKey;
    }

    @Override
    public int getDest() {
        return this.destKey;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    @Override
    public int compareTo(edge_data o) {
        return this.weight < o.getWeight() ? -1 : this.weight > o.getWeight() ? 1 : 0;
    }
}

//---------------------------------------------------------------------------------------------------------------------------------------------------------------

class Location implements geo_location {

    private double x;
    private double y;
    private double z;

    public Location(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public double x() {
        return x;
    }

    @Override
    public double y() {
        return y;
    }

    @Override
    public double z() {
        return z;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public double distance(geo_location g) {
        return Math.sqrt(Math.pow(this.x - g.x(), 2) + Math.pow(this.y - g.y(), 2));
    }

    public String toString() {
        return "" + x + "," + y + "," + z;
    }
}