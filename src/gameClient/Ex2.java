package gameClient;

import Server.Game_Server_Ex2;
import api.DWGraph_Algo;
import api.DWGraph_DS;
import api.directed_weighted_graph;
import api.game_service;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Ex2 {

    private static MyFrame _win;
    private static Arena _ar;

    public static void main(String[] args) {

        int level_number = 7;
        game_service game = Game_Server_Ex2.getServer(level_number); // you have [0,23] games

        init(game);

    }

    private static void init(game_service game) {
        String g = game.getGraph();
        String fs = game.getPokemons();
        directed_weighted_graph gg = game.getJava_Graph_Not_to_be_used();
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
        _ar.setPokemons(Arena.json2Pokemons(fs));

        // Taking care of the frame
        _win = new MyFrame("Pokemon Game");
        _win.setSize(1000, 700);
        _win.update(_ar);

        _win.setVisible(true);
        _win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String info = game.toString(); // returns all the data on the game level
        JSONObject line;
        try {
            line = new JSONObject(info);// line holds the data on the game level as jsonObject
            JSONObject ttt = line.getJSONObject("GameServer");
            int rs = ttt.getInt("agents"); // how much agents there is in this level
            System.out.println(info);
            System.out.println(game.getPokemons());
            int src_node = 0;  // arbitrary node, you should start at one of the pokemon
            ArrayList<CL_Pokemon> cl_fs = Arena.json2Pokemons(game.getPokemons());

            // This loop going through all the Pokemon's in the game and set on which edge they present
            for (int a = 0; a < cl_fs.size(); a++) {
                Arena.updateEdge(cl_fs.get(a), dwgAlgo.getGraph());
            }
            // This loop going through all the Agent's in the game and set on which edge they present
            for (int a = 0; a < rs; a++) {
                int ind = a % cl_fs.size();
                CL_Pokemon c = cl_fs.get(ind);
                int nn = c.get_edge().getDest(); //the key of the dest node of the edge that the pokemon is present on
                if (c.getType() < 0) {
                    nn = c.get_edge().getSrc();  //the key of the src node of the edge that the pokemon is present on
                }

                game.addAgent(nn);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
