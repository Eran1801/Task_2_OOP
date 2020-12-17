package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.game_service;
import api.node_data;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Game_Manager implements Runnable  {

    private int ID;
    private int levelNumber;

    private Arena _ar;
    private game_service game;
    private HashMap<Integer, List<node_data>> nextAgentsNodes;
    private int counter = 0;
    private long levelTime;
    private GameGUI gameGUI;

    public void setGameData(int ID, int levelNumber) {
        this.ID = ID;
        this.levelNumber = levelNumber;
    }

    public void run() {
        initGame();
        startGame();
        while(game.isRunning()) {
            updateGameBoard();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Level Ended");
        System.out.println(game);
        System.exit(0);
    }

    private void initGame() {
        game = Game_Server_Ex2.getServer(levelNumber); // you have [0,23] games
        String g = game.getGraph();
        String ps = game.getPokemons();
        DWGraph_DS dwg = new DWGraph_DS();
        DWGraph_Algo dwgAlgo = new DWGraph_Algo();
        dwgAlgo.init(dwg);

        try {
            PrintWriter pw = new PrintWriter(new File("graph.json"));
            pw.write(g);
            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        dwgAlgo.load("graph.json");
        // initiating Arena
        _ar = new Arena();
        _ar.setGraph(dwgAlgo.getGraph());
        _ar.setPokemons(Arena.json2Pokemons(ps));
        _ar.setGraphAlgo(dwgAlgo);
        _ar.setLevel(levelNumber);

        gameGUI = new GameGUI(_ar);

        //new Thread(new GameGUI(_ar)).start();

        String infoGameString = game.toString(); // returns all the data on the game level
        JSONObject line;
        try {
            line = new JSONObject(infoGameString);// line holds the data on the game level as jsonObject
            JSONObject ttt = line.getJSONObject("GameServer");
            int amountAgents = ttt.getInt("agents"); // how much agents there is in this level
            System.out.println(infoGameString);
            System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            List<CL_Pokemon> cl_ps = _ar.getPokemons();

            // This loop going through all the Pokemon's in the game and set on which edge they present
            for (int i = 0; i < cl_ps.size(); i++) {
                Arena.updateEdge(cl_ps.get(i), dwgAlgo.getGraph());
            }
            // This loop going through all the Agent's in the game and set on which edge they present
            for (int i = 0; i < amountAgents; i++) {
                int ind = i % cl_ps.size();
                CL_Pokemon c = cl_ps.get(ind);
                int key_edge = c.get_edge().getDest(); //the key of the dest node of the edge that the pokemon is present on
                if (c.getType() < 0) {
                    key_edge = c.get_edge().getSrc();  //the key of the src node of the edge that the pokemon is present on
                }

                game.addAgent(key_edge);
                //game.addAgent(i);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initAgents();
    }
    private void initAgents() {
        String getAgentsJson = game.getAgents();
        List<CL_Agent> agents = Arena.getAgents(getAgentsJson, _ar.getGraph());
        _ar.setAgents(agents); //update agents in the arena for the GUI
        _ar.initAgentsValues(_ar.getAgents().size());
    }

    private void startGame() {
        game.startGame();
        _ar.setTime(game.timeToEnd());
        levelTime = game.timeToEnd();
    }

    private void updateGameBoard() {
        _ar.setTime(game.timeToEnd());
        String getAgentsJson = game.getAgents();
        List<CL_Agent> agents = Arena.getAgents(getAgentsJson, _ar.getGraph());
        _ar.setAgents(agents); //update agents in the arena for the GUI

        if (nextAgentsNodes == null) {
            nextAgentsNodes = new HashMap<>();
            for (CL_Agent agent : _ar.getAgents()) {
                nextAgentsNodes.put(agent.getID(), new ArrayList<>());
            }
        }

        //we want to perform an algorithm only after catching a Pokemon or if an agent is idle
        //if we caught a pokemon
        if (_ar.isPokemonCaught()) {
            runAlgorithms();
        } else {
            for (CL_Agent agent : _ar.getAgents()) {
                if (agent.get_curr_edge() == null && nextAgentsNodes.get(agent.getID()).size() == 0) { //if we found an agent that is idle
                    runAlgorithms();
                    break;

                }
            }
        }

        for (CL_Agent agent : _ar.getAgents()) {
            List<node_data> agentPath = nextAgentsNodes.get(agent.getID());
            if (agentPath.size() != 0) {
                if (agent.get_curr_edge() == null)
                    game.chooseNextEdge(agent.getID(), CL_Agent.move(agentPath));
            }
        }

        game.move();
    }

    private void runAlgorithms() {
        counter++;
        //System.out.println("Counter: " + counter);
        /*if (counter==21){
            System.out.println("test");
        }*/

        //System.out.println("Running Algorithms");
        String getPokemonsJson = game.getPokemons();
        List<CL_Pokemon> pokemons = Arena.json2Pokemons(getPokemonsJson);
        _ar.setPokemons(pokemons); // update pokemon's in the arena for the GUI

        // This loop going through all the Pokemon's in the game and set on which edge they present
        for (int i = 0; i < pokemons.size(); i++) {
            Arena.updateEdge(pokemons.get(i), _ar.getGraph());
        }

        CL_Pokemon rarestPokemon = _ar.getRarestPokemon();
        //there is no rare pokemon
        if (rarestPokemon == null) {
            //loop through all Pokemon's
            for (int i=0; i< pokemons.size(); i++) {
                CL_Pokemon currPokemon = pokemons.get(i);
                _ar.searchForNearestAgent(currPokemon);
            }

            HashMap<Integer, List<List<node_data>>> nearestAgentToPokemon = new HashMap<>();

            for (CL_Agent agent : _ar.getAgents()) {
                nearestAgentToPokemon.put(agent.getID(), new ArrayList<>());
            }

            //loop through all Pokemon's
            for (int i=0; i< pokemons.size(); i++) {
                CL_Pokemon currPokemon = pokemons.get(i);
                List<List<node_data>> minPaths = new ArrayList<>();
                List<node_data> minPath = null;
                CL_Agent minAgent = null;
                double minDistance = Double.MAX_VALUE;
                for (CL_Agent agent : _ar.getAgents()) {
                    List<node_data> pokemonPath = agent.getPath(currPokemon);
                    double pathDistance = pokemonPath.get(pokemonPath.size()-1).getWeight();
                    if (pathDistance <= minDistance) {
                        minDistance = pathDistance;
                        minPath = pokemonPath;
                        minAgent = agent;
                    }
                    nearestAgentToPokemon.get(minAgent.getID()).add(minPath);
                    //nearestAgentToPokemon.put(minAgent.getID(), minPath);
                }
            }

            double minDistance = Double.MAX_VALUE;
            List<node_data> minPath = null;
            for (CL_Agent agent : _ar.getAgents()) {
                List<List<node_data>> pathsFromPokemons = nearestAgentToPokemon.get(agent.getID());
                for (List<node_data> pathFromPokemon : pathsFromPokemons) {
                    double pathDistance = pathFromPokemon.get(pathFromPokemon.size() - 1).getWeight();
                    if (pathDistance <= minDistance) {
                        minDistance = pathDistance;
                        minPath = pathFromPokemon;
                    }
                }
                nextAgentsNodes.put(agent.getID(), minPath);
            }

        }


        //TODO: when there are more than 1 agent, check if the other agents are going to the nearest pokemon. if not maybe we rather just delete this algorithm.
        // TODO: yes there are ! in game 17
        //there is a rare pokemon
        else {
            System.out.println("Found rare pokemon! value: " + rarestPokemon.getValue());
            CL_Agent nearestAgent = _ar.searchForNearestAgent(rarestPokemon);
            nextAgentsNodes.put(nearestAgent.getID(), nearestAgent.getPath(rarestPokemon));
        }
    }
}
