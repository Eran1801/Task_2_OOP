package api;

import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

public class DWGraph_Algo implements dw_graph_algorithms {

    DWGraph_DS graph; //represents a pointer to the original graph.

    /**
     * Init the graph on which this set of algorithms operates on.
     */
    @Override
    public void init(directed_weighted_graph g) {
        graph = (DWGraph_DS) g;
    }

    /**
     * Return the underlying graph of which this class works.
     */
    @Override
    public directed_weighted_graph getGraph() {
        return this.graph;
    }

    /**
     * Compute a deep copy of this weighted graph.
     */
    @Override
    public directed_weighted_graph copy() {
        return this.graph.deepCopy();
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     */
    @Override
    public boolean isConnected() {
        int numOfNodes = this.graph.getV().size(); //get number of nodes in the original graph
        if (numOfNodes == 0 || numOfNodes == 1)
            return true; //if the number of nodes is less than 2, the graph is surely connected.
        if (this.graph.edgeSize() < numOfNodes - 1)
            return false; //if the number of edges is less than the number of nodes -1, the graph is surely no connected.

        //if we reached here, we can't tell if the graph is connected by the number of edges & number of nodes
        //so we check using BFS algorithm.
        Iterator<node_data> itr = this.graph.getV().iterator(); //we make an iterator to get list of nodes keys, then we will set the starting node to each key.
        int[] nodesKeysArr = new int[this.graph.nodeSize()];
        int arrayIndex = 0;
        while (itr.hasNext()) { // insert all the nodesKeys to the array
            nodesKeysArr[arrayIndex++] = itr.next().getKey();
        }
        for (int nodeKey : nodesKeysArr) {
            DWGraph_DS g = (DWGraph_DS) this.copy();
            NodeData startingNode = (NodeData) g.getNode(nodeKey);
            Queue<NodeData> q = new LinkedList<>();
            q.add(startingNode); //add the first node to the queue
            int numOfNodesInSubGraph = 1; //a counter for how many nodes we went through in BFS, in the end of the BFS we compare this to the graph size
            startingNode.setInfo("BLACK"); //mark the node black, so we know we already visited this node.

            while (!q.isEmpty()) {
                NodeData currentNode = q.remove();
                for (edge_data edge : currentNode.getNi().values()) { //iterate through all of the neighbors that we haven't visited yet.
                    NodeData nodeNeighbor = (NodeData) g.getNode(edge.getDest());
                    if (nodeNeighbor.getInfo() != "BLACK") { //if it's not black, than we haven't visited that node yet
                        numOfNodesInSubGraph++;
                        nodeNeighbor.setInfo("BLACK"); //mark the node black, so we know we already visited this node.
                        q.add(nodeNeighbor);
                    }
                }
            }
            if (g.nodeSize() != numOfNodesInSubGraph)
                return false; //if the number of nodes in the graph does not equals the number of nodes we visited using BFS - the graph is surely not connected.
        }
        return true;
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        if (src == dest) return 0;
        DWGraph_DS g = (DWGraph_DS) this.copy();
        PriorityQueue<EdgeData> pq = new PriorityQueue<>();

        NodeData sourceNode = (NodeData) g.getNode(src); //get the source node
        NodeData destNode = (NodeData) g.getNode(dest); //get the destination node

        Collection<edge_data> neighborEdgesCollection = sourceNode.getNeighborEdges().values();
        for (edge_data neighborEdge : neighborEdgesCollection) {
            pq.add((EdgeData) neighborEdge); //add all edges that coming out from the source node to the priority queue
        }
        sourceNode.setWeight(0); //set the weight of the source node to 0
        sourceNode.setInfo("BLACK"); //mark that node as visited.

        while (!pq.isEmpty()) { //loop through all edges in the priority queue
            EdgeData prioritezedEdge = pq.poll(); //take out the lightest Edge from the priority queue
            prioritezedEdge.setInfo("BLACK"); //mark that edge as visited.

            double edgeWeight = prioritezedEdge.getWeight();

            NodeData neighborNode = (NodeData) g.getNode(prioritezedEdge.getDest());
            if (edgeWeight < neighborNode.getWeight()) //if we found a path with less weight - update the node weight
                neighborNode.setWeight(edgeWeight); //update the weight of the neighbor node
            if (neighborNode.getInfo() != "BLACK") { //if we haven't visited this node yet
                Collection<edge_data> neighborNodeEdges = neighborNode.getNeighborEdges().values(); // get all edges of neighborNode
                for (edge_data neighborNodeEdge : neighborNodeEdges) {
                    int newSource = neighborNodeEdge.getSrc();
                    int newDest = neighborNodeEdge.getDest();
                    double newWeight = neighborNodeEdge.getWeight() + neighborNode.getWeight();
                    String newInfo = neighborNodeEdge.getInfo();
                    EdgeData updatedNeighborNodeEdge = new EdgeData(newSource, newDest, newWeight, newInfo); //make new Edge with updated weight
                    pq.add(updatedNeighborNodeEdge); //add the edge to the queue
                }
                neighborNode.setInfo("BLACK"); //mark that node as visited.
            }
        }
        return destNode.getWeight() != Double.MAX_VALUE ? destNode.getWeight() : -1; //return the destination weight or -1 if there is no path from src to dest.
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        DWGraph_DS g = (DWGraph_DS) this.copy();
        PriorityQueue<EdgeData> pq = new PriorityQueue<>();

        NodeData sourceNode = (NodeData) g.getNode(src); //get the source node
        NodeData destNode = (NodeData) g.getNode(dest); //get the destination node

        if (src == dest) {
            List<node_data> list = new LinkedList<>();
            list.add(sourceNode);
            return list;
        }

        Collection<edge_data> neighborEdgesCollection = sourceNode.getNeighborEdges().values();
        for (edge_data neighborEdge : neighborEdgesCollection) {
            pq.add((EdgeData) neighborEdge); //add all edges that coming out from the source node to the priority queue
        }
        sourceNode.setWeight(0); //set the weight of the source node to 0
        sourceNode.setInfo("BLACK"); //mark that node as visited.

        while (!pq.isEmpty()) { //loop through all edges in the priority queue
            EdgeData prioritezedEdge = pq.poll(); //take out the lightest Edge from the priority queue
            prioritezedEdge.setInfo("BLACK"); //mark that edge as visited.

            double edgeWeight = prioritezedEdge.getWeight();

            NodeData neighborNode = (NodeData) g.getNode(prioritezedEdge.getDest());
            if (edgeWeight < neighborNode.getWeight()) { //if we found a path with less weight - update the node weight
                neighborNode.setWeight(edgeWeight); //update the weight of the neighbor node
                neighborNode.setTag(prioritezedEdge.getSrc()); //set the parent of neighborNode
            }
            if (neighborNode.getInfo() != "BLACK") { //if we haven't visited this node yet
                Collection<edge_data> neighborNodeEdges = neighborNode.getNeighborEdges().values(); // get all edges of neighborNode
                for (edge_data neighborNodeEdge : neighborNodeEdges) {
                    int newSource = neighborNodeEdge.getSrc();
                    int newDest = neighborNodeEdge.getDest();
                    double newWeight = neighborNodeEdge.getWeight() + neighborNode.getWeight();
                    String newInfo = neighborNodeEdge.getInfo();
                    EdgeData updatedNeighborNodeEdge = new EdgeData(newSource, newDest, newWeight, newInfo); //make new Edge with updated weight
                    pq.add(updatedNeighborNodeEdge); //add the edge to the queue
                }
                neighborNode.setInfo("BLACK"); //mark that node as visited.
            }
        }

        NodeData nextParent = destNode;
        if (nextParent.getTag() == -1) return null; //there's no path from src to dest.
        Stack<node_data> pathReversed = new Stack<node_data>(); //represents the path from the source to the destination
        while (nextParent.getTag() != -1) { //while we did not reach the src
            pathReversed.add(nextParent); //add the node the the path.
            nextParent = (NodeData) g.getNode(nextParent.getTag());
        }
        pathReversed.add(nextParent); // add the last node
        Stack<node_data> path = new Stack<node_data>(); //used to reverse the order of the path, so the source will be first and dest last.
        while (pathReversed.size() > 0)
            path.add(pathReversed.pop());
        return path;
    }


    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     */
    @Override
    public boolean save(String file) {

        // create two arrays for the nodes and the edges
        JsonArray nodesJsonArray = new JsonArray();
        JsonArray edgesJsonArray = new JsonArray();

        // going through all the nodes&edges in the graph and put them in the Json file
        Iterator<node_data> nitr = graph.getV().iterator();
        while (nitr.hasNext()) {
            node_data node = nitr.next();
            JsonObject nodeJson = new JsonObject();
            nodeJson.addProperty("id", node.getKey());
            nodeJson.addProperty("pos", node.getLocation().toString());
            nodesJsonArray.add(nodeJson);
            Iterator<edge_data> eitr = ((NodeData) node).getNeighborEdges().values().iterator();
            while (eitr.hasNext()) {
                edge_data edge = eitr.next();
                JsonObject edgeJson = new JsonObject();
                edgeJson.addProperty("src", edge.getSrc());
                edgeJson.addProperty("w", edge.getWeight());
                edgeJson.addProperty("dest", edge.getDest());
                edgesJsonArray.add(edgeJson);
            }
        }
        JsonObject graphJson = new JsonObject();
        graphJson.add("Nodes", nodesJsonArray);
        graphJson.add("Edges", edgesJsonArray);


        try {
            PrintWriter pw = new PrintWriter(new File(file));
            pw.write(graphJson.toString());
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     */
    @Override
    public boolean load(String file) {

        try {
            FileReader reader = new FileReader(file);

            // JsonParser convert the reader to a JsonObject
            JsonElement jsonElement = JsonParser.parseReader(reader).getAsJsonObject();
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            DWGraph_DS loadedGraphFromJson = new DWGraph_DS();

            // convert the nodes&edges in the Json file to an Array var
            JsonArray nodesJsonArray = jsonObject.getAsJsonArray("Nodes");
            JsonArray edgesJsonArray = jsonObject.getAsJsonArray("Edges");

            // for the nodes
            for (int i = 0; i < nodesJsonArray.size(); i++) {
                // nodeJsonObject holds the nodesArray in 'i' index
                JsonObject nodeJsonObject = nodesJsonArray.get(i).getAsJsonObject();
                geo_location newLocation;
                if (!nodeJsonObject.has("pos")) {
                    Random r = new Random();
                    double random_number_x = 35.185 + (35.215 - 35.185) * r.nextDouble();
                    double random_number_y = 32.098 + (32.11 - 32.098) * r.nextDouble();
                    newLocation = new Location(random_number_x, random_number_y, 0);
                } else {
                    String pos = nodeJsonObject.get("pos").getAsString();
                    String[] posArrayString = pos.split(",");
                    Double[] posArray = new Double[posArrayString.length];
                    for (int j = 0; j < posArrayString.length; j++) {
                        posArray[j] = Double.parseDouble(posArrayString[j]);
                    }
                    newLocation = new Location(posArray[0], posArray[1], posArray[2]);
                }
                int key = nodeJsonObject.get("id").getAsInt();
                node_data newNode = new NodeData(key);
                newNode.setLocation(newLocation);
                loadedGraphFromJson.addNode(newNode);
            }

            // for the edges
            for (int i = 0; i < edgesJsonArray.size(); i++) {
                JsonObject edgeJsonObject = edgesJsonArray.get(i).getAsJsonObject();
                int srcJson = edgeJsonObject.get("src").getAsInt();
                int destJson = edgeJsonObject.get("dest").getAsInt();
                double weightJson = edgeJsonObject.get("w").getAsDouble();
                loadedGraphFromJson.connect(srcJson, destJson, weightJson);

            }
            System.out.println(loadedGraphFromJson.toString());
            this.graph = loadedGraphFromJson;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<List<Integer>> connected_components () {

        List<List<Integer>> ans_list = new ArrayList<>();
        List<Integer> check_for_components_in_nodes = new ArrayList<>(graph.getNodes().keySet());

        while (!check_for_components_in_nodes.isEmpty()){
            int node_key = check_for_components_in_nodes.get(0);
            List<Integer> scc = connected_component(node_key);
            ans_list.add(scc);
            for ( int connected_node : scc){
                // remove the nodes that we found their sccs from check_for_components_in_nodes
                check_for_components_in_nodes.remove((Integer)connected_node);
            }

        }
        return ans_list;
    }

    public List<Integer> connected_component(int id) {

        List<Integer> list = new ArrayList<Integer>();

        if (graph == null) return list;

        node_data node = graph.getNode(id);

        if (node == null) return list;

        List<Integer> connected_from_node = bfs_from(node);
        List<Integer> connected_to_node = bfs_to(node);

        // keeps only the variables that are in the intersection of connected_from_node and connected_to_node
        connected_from_node.retainAll(connected_to_node);

        return connected_from_node;

    }


    private List<Integer> bfs_from(node_data starting_node) {

        Queue<node_data> queue = new LinkedList<node_data>();
        queue.add(starting_node);

        List<Integer> list_connected_from = new ArrayList<Integer>();
        list_connected_from.add(starting_node.getKey());

        HashMap<Integer, Boolean> visited = new HashMap<>();

        for (int node_key : graph.getNodes().keySet()) {
            visited.put(node_key, false);
        }

        visited.put(starting_node.getKey(), true);

        while (!queue.isEmpty()) {

            node_data node_from_queue = queue.poll();
            for (edge_data edge_from_node : ((NodeData) node_from_queue).getNeighborEdges().values()) {
                int edge_from_node_key = edge_from_node.getDest();
                if (!visited.get(edge_from_node_key)) {
                    queue.add(graph.getNode(edge_from_node_key));
                    list_connected_from.add(edge_from_node_key);
                    visited.put(edge_from_node_key, true);
                }
            }
        }

        return list_connected_from;
    }

    private List<Integer> bfs_to(node_data starting_node) {

        Queue<node_data> queue = new LinkedList<node_data>();
        queue.add(starting_node);

        List<Integer> list_connected_to = new ArrayList<Integer>();
        list_connected_to.add(starting_node.getKey());

        HashMap<Integer, Boolean> visited = new HashMap<>();

        for (int node_key : graph.getNodes().keySet()) {
            visited.put(node_key, false);
        }

        visited.put(starting_node.getKey(), true);

        while (!queue.isEmpty()) {

            node_data node_from_queue = queue.poll();
            for (edge_data edge_from_node : ((NodeData) node_from_queue).getEdgesConnectedToThisNode().values()) {
                int edge_from_node_key = edge_from_node.getSrc();
                if (!visited.get(edge_from_node_key)) {
                    queue.add(graph.getNode(edge_from_node_key));
                    list_connected_to.add(edge_from_node_key);
                    visited.put(edge_from_node_key, true);
                }
            }
        }
        return list_connected_to;
    }

}
