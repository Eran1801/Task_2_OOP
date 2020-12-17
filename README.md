## Welcome to our submission of the game: "The Pokemon's Challenge"

<img src="src/gameClient/pic/pikachu.png" width="350"> 

### This Object Oriented Programming task was written by:

* Etay Matzliah - 205987795
* Eran Levy - 311382360
    
    The main topic is developing logic for a game where a group of agents needs to eat Pokemon's as much as possible in few steps as possible on directed graph.
    
    #### Game rules:
    The agents earn points while collecting a pokemon. Each pokemon have different value.
    There is a clock that counting backwards and when the clock gets to zero the game ends.
    The direction of the agents movement is based on the value of the pokemon.
    #### How to run the game:
    We have made an executable jar. it is located in Game\Ex2.jar.
    to launch it simply double click the jar file or write the following in terminal in the project root folder:
  
    `java -jar Game/Ex2.jar <ID Here> <Game Level Here>`
  
    #### Our project is divided to two parts:
    
    ### Part 1 : building the graph and his algorithms 
    
    In this part we have 4 important interfaces:
    
  1 . directed_weighted_graph - this interface implemented by the DWGraph_DS class and represents a directional weighted graph.
  
  2 . node_data - this interface implemented by the NodeData class and represents a vertex in our graph.
  
  3 . edge_data - this interface implemented by the EdgeData class and represents a edge in our graph.
  
  4 . dw_graph_algorithms - this interface implemented by the WDGraph_Algo class and represents some algorithms based on the graph we've created in DGraph.

   ### part 2 : building the Pokemons challenge game client
   
   in this part we have a numbers of class that we summarize them here:
   
  1 . Ex2 - this class represents the main class of the program, Running Ex2 main will launch the login menu (or launch the game if passing the ID and level number as args) 
  
  2 . Game_Manager - this class handles the game client logic. it starts the game and calculates algorithms that decides the agents next moves
  
  3 . CL_Agent - this class represents the Agents in the game. it gets the updated data from the Game server and makes a Agent object out of it.
  
  4 . CL-Pokemon - this class represents the Agents in the game. it gets the updated data from the Game server and makes a Pokemon object out of it. 
  
  5 . Arena - this class holds all of the agents, pokemons and has algorithms that helps decide the next move in the game.
  
  6 . GameGUI - this class is a GUI for the game. it gets the updated data from the Arena and shows a visual representation of the game (visualizes the graph, edges, pokemons, agents and game info)
  
  
  
  #### The algorithms that we used in this project is : 
  
1 . Dijkstra algorithms : to get the shortestPathDist and the shortestPath

2 . BFS algorithms : to know if in the graph there is a valid path from each node to each other node

3 . Rare Pokemon Algorithm : a algorithm we made that figures out if there's a pokemon that is much more valuable than the others, if such pokemon found, it will assign the nearest agent to the task of capturing it
   
  #### Time complexity of the algorithms:
	
- Dijkstra algorithms = O(|E|*log|V|)
- BFS algorithms = O(V + E)
- Rare Pokemon Algorithm = O(V + E)

		V = vertex
		E = edges
 

##### for more information you can see our Wiki page!
