package gameClient;

import api.*;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;
import gameClient.util.Range2Range;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents a multi Agents Arena which move on a graph - grabs Pokemons and avoid the Zombies.
 *
 * @author boaz.benmoshe
 */
public class Arena {
    public static final double EPS1 = 0.001, EPS2 = EPS1 * EPS1, EPS = EPS2;
    private directed_weighted_graph _gg;
    private dw_graph_algorithms _ggAlgo;
    private List<CL_Agent> _agents;
    private List<CL_Pokemon> _pokemons;
    private List<String> _info;
    private static Point3D MIN = new Point3D(0, 100, 0);
    private static Point3D MAX = new Point3D(0, 100, 0);
    private List<Double> agentsValues;
    private long time;
    private int level;

    public Arena() {
        ;
        _info = new ArrayList<String>();
        agentsValues = new ArrayList<>();
    }

    private Arena(directed_weighted_graph g, List<CL_Agent> r, List<CL_Pokemon> p) {
        _gg = g;
        this.setAgents(r);
        this.setPokemons(p);
    }

    public void setPokemons(List<CL_Pokemon> f) {
        this._pokemons = f;
    }

    public void setAgents(List<CL_Agent> f) {
        this._agents = f;
    }

    public void setGraph(directed_weighted_graph g) {
        this._gg = g;
    }//init();}

    private void init() {
        MIN = null;
        MAX = null;
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        Iterator<node_data> iter = _gg.getV().iterator();
        while (iter.hasNext()) {
            geo_location c = iter.next().getLocation();
            if (MIN == null) {
                x0 = c.x();
                y0 = c.y();
                x1 = x0;
                y1 = y0;
                MIN = new Point3D(x0, y0);
            }
            if (c.x() < x0) {
                x0 = c.x();
            }
            if (c.y() < y0) {
                y0 = c.y();
            }
            if (c.x() > x1) {
                x1 = c.x();
            }
            if (c.y() > y1) {
                y1 = c.y();
            }
        }
        double dx = x1 - x0, dy = y1 - y0;
        MIN = new Point3D(x0 - dx / 10, y0 - dy / 10);
        MAX = new Point3D(x1 + dx / 10, y1 + dy / 10);

    }

    public List<CL_Agent> getAgents() {
        return _agents;
    }

    public List<CL_Pokemon> getPokemons() {
        return _pokemons;
    }


    public directed_weighted_graph getGraph() {
        return _gg;
    }

    public List<String> get_info() {
        return _info;
    }

    public void set_info(List<String> _info) {
        this._info = _info;
    }

    ////////////////////////////////////////////////////
    public static List<CL_Agent> getAgents(String aa, directed_weighted_graph gg) {
        ArrayList<CL_Agent> ans = new ArrayList<CL_Agent>();
        try {
            JSONObject ttt = new JSONObject(aa);
            JSONArray ags = ttt.getJSONArray("Agents");
            for (int i = 0; i < ags.length(); i++) {
                CL_Agent c = new CL_Agent(gg, 0);
                c.update(ags.get(i).toString());
                ans.add(c);
            }
            //= getJSONArray("Agents");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static ArrayList<CL_Pokemon> json2Pokemons(String fs) {
        ArrayList<CL_Pokemon> ans = new ArrayList<CL_Pokemon>();
        try {
            JSONObject ttt = new JSONObject(fs);
            JSONArray ags = ttt.getJSONArray("Pokemons");
            for (int i = 0; i < ags.length(); i++) {
                JSONObject pp = ags.getJSONObject(i);
                JSONObject pk = pp.getJSONObject("Pokemon");
                int t = pk.getInt("type");
                double v = pk.getDouble("value");
                double s = 0;//pk.getDouble("speed");
                String p = pk.getString("pos");
                CL_Pokemon pok = new CL_Pokemon(new Point3D(p), t, v, null);
                ans.add(pok);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    //finds in which edge the pokemon exists and sets the pokemon's edge
    public static void updateEdge(CL_Pokemon ps, directed_weighted_graph g) {
        //	oop_edge_data ans = null;
        Iterator<node_data> itr = g.getV().iterator();
        // going through all the nodes, and on in each node checking if the Pokemon is on his edges
        while (itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
            while (iter.hasNext()) {
                edge_data e = iter.next();
                boolean f = isOnEdge(ps.getLocation(), e, ps.getType(), g);
                if (f) {
                    ps.set_edge(e);
                }
            }
        }
    }

    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest) {

        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if (dist > d1 - EPS2) {
            ans = true;
        }
        return ans;
    }

    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p, src, dest);
    }

    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if (type < 0 && dest > src) {
            return false;
        }
        if (type > 0 && src > dest) {
            return false;
        }
        return isOnEdge(p, src, dest, g);
    }

    private static Range2D GraphRange(directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        double x0 = 0, x1 = 0, y0 = 0, y1 = 0;
        boolean first = true;
        while (itr.hasNext()) {
            geo_location p = itr.next().getLocation();
            if (first) {
                x0 = p.x();
                x1 = x0;
                y0 = p.y();
                y1 = y0;
                first = false;
            } else {
                if (p.x() < x0) {
                    x0 = p.x();
                }
                if (p.x() > x1) {
                    x1 = p.x();
                }
                if (p.y() < y0) {
                    y0 = p.y();
                }
                if (p.y() > y1) {
                    y1 = p.y();
                }
            }
        }
        Range xr = new Range(x0, x1);
        Range yr = new Range(y0, y1);
        return new Range2D(xr, yr);
    }

    public static Range2Range w2f(directed_weighted_graph g, Range2D frame) {
        Range2D world = GraphRange(g);
        Range2Range ans = new Range2Range(world, frame);
        return ans;
    }

    public boolean isPokemonCaught() {
        boolean isCaught = false;
        for (int i = 0; i < this.agentsValues.size(); i++) {
            if (_agents.get(i).getValue() != agentsValues.get(i)) {
                isCaught = true;
                agentsValues.set(i, _agents.get(i).getValue());
            }
        }
        return isCaught;
    }

    private CL_Pokemon[] getMostAndLeastRarePokemons() {
        CL_Pokemon[] MostAndLeastRarePokemons = new CL_Pokemon[2]; //index 0 is the most rare and index 1 is the least rare
        double[] MostAndLeastRarePokemonsValues = new double[2]; //index 0 is the most rare and index 1 is the least rare
        MostAndLeastRarePokemonsValues[0] = Double.MIN_VALUE;
        MostAndLeastRarePokemonsValues[1] = Double.MAX_VALUE;
        for (CL_Pokemon pokemon : _pokemons) {
            if (pokemon.getValue() > MostAndLeastRarePokemonsValues[0]) {
                MostAndLeastRarePokemons[0] = pokemon;
                MostAndLeastRarePokemonsValues[0] = pokemon.getValue();
            }
            if (pokemon.getValue() < MostAndLeastRarePokemonsValues[1]) {
                MostAndLeastRarePokemons[1] = pokemon;
                MostAndLeastRarePokemonsValues[1] = pokemon.getValue();
            }
        }
        return MostAndLeastRarePokemons; //return a pokemons array when index 0 is the most rare and index 1 is the least rare
    }


    //returns the rarest pokemon if there is one. null if there is no rare pokemon
    public CL_Pokemon getRarestPokemon() {
        final double rarePokemonThreshold = _pokemons.size() * 1.5;
        CL_Pokemon[] mostAndLeastRarePokemons = getMostAndLeastRarePokemons();
        CL_Pokemon rarestPokemon = mostAndLeastRarePokemons[0];
        CL_Pokemon leastRarePokemon = mostAndLeastRarePokemons[1];
        if (rarestPokemon.sameValueAs(leastRarePokemon))
            return null; //If the most rare and least rare has the same values - there is no rare pokemon
        return rarestPokemon.getValue() / leastRarePokemon.getValue() >= rarePokemonThreshold ? rarestPokemon : null;
    }

    public void initAgentsValues(int numOfAgents) {
        for (int i = 0; i < numOfAgents; i++) {
            agentsValues.add(0.0);
        }
    }

    //Calculates all agents paths to a pokemon and saves them. Returns the agent that can reach the pokemon fastest
    public CL_Agent searchForNearestAgent(CL_Pokemon pokemon) {

        edge_data pokemonEdge = pokemon.get_edge();
        int pokemonEdgeType = pokemon.getType();

        double minDistance = Double.MAX_VALUE;
        List<node_data> path = new ArrayList<>();
        CL_Agent nearestAgent = null;
        for (CL_Agent agent : this.getAgents()) {
            int fromNode;
            fromNode = agent.get_curr_edge() != null ? agent.get_curr_edge().getDest() : agent.getSrcNode();
            if (fromNode != pokemonEdge.getSrc()) { //no need to do shortest path from node to self, so we just use a empty path
                path = this._ggAlgo.shortestPath(fromNode, pokemonEdge.getSrc());
            }
            double distance = path.size() != 0 ? path.get(path.size() - 1).getWeight() : pokemonEdge.getWeight();
            //distance /= agent.getSpeed(); //TODO: check this out, Theoretically this line should be good, but it's not. that way only 1 agent is eating all the pokemons and the other agents staying slow.
            if (distance < minDistance) {
                minDistance = distance;
                nearestAgent = agent;
            }
            if (path.size() >= 1) {
                path.remove(0); //remove the first node //TODO: when an agent is idle, i don't think we need to remove the first node.
            }
            node_data lastNode = ((DWGraph_DS) (_gg)).copyNode(_gg.getNode(_gg.getNode(pokemonEdge.getDest()).getKey())); //copy the last node because we want to add weight to it and we don't want to do that on the original node
            lastNode.setWeight(minDistance + pokemonEdge.getWeight());
            path.add(lastNode);

            agent.setPath(pokemon, path);
        }
        return nearestAgent;

    }

    public void setGraphAlgo(DWGraph_Algo dwgAlgo) {
        this._ggAlgo=dwgAlgo;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return this.time;
    }

    public String getLevel() {
        return "" + this.level;
    }

    public void setLevel(int levelNumber) {
        this.level = levelNumber;
    }
}
