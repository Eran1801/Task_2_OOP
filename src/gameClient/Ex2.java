package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;


public class Ex2 {

    private static MyFrame _win;
    private static Arena _ar;
    private static game_service game;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private static int nextNode = -1;
    private static HashMap<Integer, List<node_data>> nextAgentsNodes;

    public static void main(String[] args) {
//        LoginGui start = new LoginGui();
//        start.GUI();

        // THE IDEA :
        // To set a Thread that will wait to a call from the LoginGui when the "start game" button will press
        // and then will enter all of this ( start game and .. .. )
        // because first of all, the LOGIN needs to appear when the Ex2 main runs
        // and after pressing the start game button then the graph GUI needs to start.

        int level_number = 7;
        game = Game_Server_Ex2.getServer(level_number); // you have [0,23] games

        init();
        game.startGame();
        System.out.println("game.toString()=" + game.toString());
        System.out.println(game.getAgents());
        game.chooseNextEdge(0, 6);

        //CL_Agent agent = _ar.getAgents().get(0);
        //game.chooseNextEdge(agent.getID(), 13);
        //game.chooseNextEdge(0, 13);

        while (game.isRunning()) {
            updateGameBoard();
            game.move();
            try {
                _win.repaint();
                Thread.sleep(75);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        System.exit(0);

    }

    private static void updateGameBoard() {
        String getAgentsJson = game.getAgents();
        List<CL_Agent> agents = Arena.getAgents(getAgentsJson, _ar.getGraph());
        _ar.setAgents(agents); //update agents in the arena for the GUI
        //we want to perform an algorithm only after catching a Pokemon

        boolean isCaught = _ar.isPokemonCaught();

        if (isCaught) {
            System.out.println("Entered isCaught");
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
                System.out.println("No rare pokemon found.");
                //loop through all Pokemon's
                for (int i=0; i< pokemons.size(); i++) {
                    CL_Pokemon currPokemon = pokemons.get(i);
                }
            }

            //there is a rare pokemon
            else {
                System.out.println("Found rare pokemon! value: " + rarestPokemon.getValue());
                nextAgentsNodes = _ar.searchForNearestAgent(rarestPokemon);
                //System.out.println("Removed First Node: " + nearestAgent.removeFirstNode());
                //nextNode = nearestAgent.move();
                //game.chooseNextEdge(nearestAgent.getID(), nearestAgent.move());
//                nextNode=nearestAgent.move();
            }
        }

        for (CL_Agent agent : _ar.getAgents()) {
            if (nextAgentsNodes != null){
                List<node_data> agentPath = nextAgentsNodes.get(agent.getID());
                if (agentPath.size() != 0) {
                    game.chooseNextEdge(agent.getID(), CL_Agent.move(agentPath));
                }
            }
        }

        game.move();
        System.out.println(game.getAgents());
    }

    private static void init() {
        String g = game.getGraph();
        String ps = game.getPokemons();
        //directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used(); // needs to delete ?
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

        // Initiating Frame
        _win = new MyFrame("Pokemon Game");
        ImageIcon iconGraph = new ImageIcon("src/gameClient/pic/Graph.png");
        _win.setIconImage(iconGraph.getImage());
        _win.setSize((int) screenSize.getWidth(), (int) screenSize.getHeight());
        _win.setResizable(true); // suppose to work but doesn't
        _win.update(_ar);

        _win.setVisible(true);
        _win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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

                //game.addAgent(key_edge);
                game.addAgent(7);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initAgents();
    }
    private static void initAgents() {
        String getAgentsJson = game.getAgents();
        List<CL_Agent> agents = Arena.getAgents(getAgentsJson, _ar.getGraph());
        _ar.setAgents(agents); //update agents in the arena for the GUI
        _ar.initAgentsValues(_ar.getAgents().size());
    }
}
