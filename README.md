### This is a submission of EX1 by Etay Matzliah - 205987795 and Eran Levy - 311382360 

DWGraph_DS is an implementation of directed_weighted_graph which represents a Directed Weighted Graph.
a Directed Weighted Graph holds Nodes and each Edge between nodes is weighted and directed.

In DWGraph_DS.java file there's also NodeData class which is an implementation of node_data interface which represents a Node in a Graph.
a Node holdes data of neighbor Nodes (a list of edge_data) and variables for algorithms.

In DWGraph_DS.java file there's also EdgeData class which is an implementation of edge_data interface which represents a edge between two nodes.
a Edge holdes data such as source node, destination node, weight etc...

DWGraph_Algo is an implementation of dw_graph_algorithms interface.
DWGraph_Algo has a couple usefull functions on Directed Weighted Graphs and it does a deep copy beforehand, making sure the original Graph stays unchanged.
It also has a Save/Load to/from a file functions which uses JSON files.
