package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.directed_weighted_graph;
import api.game_service;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Ex2 {

    private static MyFrame _win;
    private static Arena _ar;
    private static game_service game;
    private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    public static void main(String[] args) {

        LoginGui start = new LoginGui();
        start.GUI();

        // THE IDEA :
        // To set a Thread that will wait to a call from the LoginGui when the "start game" button will press
        // and then will enter all of this ( start game and .. .. )
        // because first of all, the LOGIN needs to appear when the Ex2 main runs
        // and after pressing the start game button then the graph GUI needs to start.
        int level_number = 7;
        game = Game_Server_Ex2.getServer(level_number); // you have [0,23] games

        init(game);
        game.startGame();
        //game.chooseNextEdge(0, 13);
        //game.move();
        //System.out.println(game.getAgents());

        while (game.isRunning()) {
            updateGameBoard();
            try {
                _win.repaint();
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String res = game.toString();

        System.out.println(res);
        System.exit(0);

    }

    private static void updateGameBoard() {

        String getAgentsJson = game.getAgents();
        List<CL_Agent> agents = Arena.getAgents(getAgentsJson, _ar.getGraph());
        _ar.setAgents(agents);
        String getPokemonsJson = game.getPokemons();
        List<CL_Pokemon> pokemons = Arena.json2Pokemons(getPokemonsJson);
        _ar.setPokemons(pokemons);
        boolean needToMove = false;
        for (int i = 0; i < agents.size(); i++) {
            CL_Agent agent = agents.get(i);
            int id = agent.getID();
            int dest = agent.getNextNode();
            int src = agent.getSrcNode();
            double v = agent.getValue();
            if (dest == -1) { //TODO: we stopped here, dest is always != -1 for some reason. need to check why!
                needToMove = true;
                dest = 13; //TODO: calculate what the next dest should be
                game.chooseNextEdge(agent.getID(), dest);
                System.out.println("Agent: " + id + ", val: " + v + "   turned to node: " + dest);
            }
        }
        if (needToMove) game.move();
    }

    private static void init(game_service game) {
        String g = game.getGraph();
        String ps = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used(); // needs to delete ?
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
        // Taking care of the arena
        _ar = new Arena();
        _ar.setGraph(dwgAlgo.getGraph());
        _ar.setPokemons(Arena.json2Pokemons(ps));

        // Taking care of the frame
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
            ArrayList<CL_Pokemon> cl_ps = Arena.json2Pokemons(game.getPokemons());

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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
