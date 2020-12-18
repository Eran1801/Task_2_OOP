package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class CL_Agent {
	public static final double EPS = 0.0001;
	private static int _count = 0;
	private static int _seed = 3331;
	private int _id;
	private geo_location _pos;
	private double _speed;
	private edge_data _curr_edge;
	private node_data _curr_node;
	private directed_weighted_graph _gg;
	private CL_Pokemon _curr_pokemon;
	private long _sg_dt;
	private double _value;
	private double distanceFromNode;
	private List<node_data> path;
	private HashMap<CL_Pokemon, List<node_data>> pokemonsPaths;


	public CL_Agent(directed_weighted_graph g, int start_node) {
		_gg = g;
		setMoney(0);
		this._curr_node = _gg.getNode(start_node);
		_pos = _curr_node.getLocation();
		_id = -1;
		setSpeed(0);
		pokemonsPaths = new HashMap<>();
	}
	public void update(String json) {
		JSONObject line;
		try {
			// "GameServer":{"graph":"A0","pokemons":3,"agents":1}}
			line = new JSONObject(json);
			JSONObject ttt = line.getJSONObject("Agent");
			int id = ttt.getInt("id");
			if(id==this.getID() || this.getID() == -1) {
				if(this.getID() == -1) {_id = id;}
				double speed = ttt.getDouble("speed");
				String p = ttt.getString("pos");
				Point3D pp = new Point3D(p);
				int src = ttt.getInt("src");
				int dest = ttt.getInt("dest");
				double value = ttt.getDouble("value");
				this._pos = pp;
				this.setCurrNode(src);
				this.setSpeed(speed);
				this.setNextNode(dest);
				this.setMoney(value);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	//@Override
	public int getSrcNode() {return this._curr_node.getKey();}
	public String toJSON() {
		int d = this.getNextNode();
		String ans = "{\"Agent\":{"
				+ "\"id\":"+this._id+","
				+ "\"value\":"+this._value+","
				+ "\"src\":"+this._curr_node.getKey()+","
				+ "\"dest\":"+d+","
				+ "\"speed\":"+this.getSpeed()+","
				+ "\"pos\":\""+_pos.toString()+"\""
				+ "}"
				+ "}";
		return ans;
	}
	private void setMoney(double v) {_value = v;}

	public boolean setNextNode(int dest) {
		boolean ans = false;
		int src = this._curr_node.getKey();
		this._curr_edge = _gg.getEdge(src, dest);
		if(_curr_edge!=null) {
			ans=true;
		}
		else {_curr_edge = null;}
		return ans;
	}
	public void setCurrNode(int src) {
		this._curr_node = _gg.getNode(src);
	}
	public boolean isMoving() {
		return this._curr_edge!=null;
	}
	public String toString() {
		return toJSON();
	}
	public String toString1() {
		String ans=""+this.getID()+","+_pos+", "+isMoving()+","+this.getValue();
		return ans;
	}
	public int getID() {
		return this._id;
	}

	public geo_location getLocation() {
		return _pos;
	}


	public double getValue() {
		return this._value;
	}



	public int getNextNode() {
		int ans = -2;
		if(this._curr_edge==null) {
			ans = -1;}
		else {
			ans = this._curr_edge.getDest();
		}
		return ans;
	}

	public double getSpeed() {
		return this._speed;
	}

	public void setSpeed(double v) {
		this._speed = v;
	}
	public CL_Pokemon get_curr_pokemon() {
		return _curr_pokemon;
	}
	public void set_curr_pokemon(CL_Pokemon curr_pokemon) {
		this._curr_pokemon = curr_pokemon;
	}
	public void set_SDT(long ddtt) {
		long ddt = ddtt;
		if(this._curr_edge!=null) {
			double w = get_curr_edge().getWeight();
			geo_location dest = _gg.getNode(get_curr_edge().getDest()).getLocation();
			geo_location src = _gg.getNode(get_curr_edge().getSrc()).getLocation();
			double de = src.distance(dest);
			double dist = _pos.distance(dest);
			if(this.get_curr_pokemon().get_edge()==this.get_curr_edge()) {
				 dist = _curr_pokemon.getLocation().distance(this._pos);
			}
			double norm = dist/de;
			double dt = w*norm / this.getSpeed();
			ddt = (long)(1000.0*dt);
		}
		this.set_sg_dt(ddt);
	}

	public edge_data get_curr_edge() {
		return this._curr_edge;
	}
	public long get_sg_dt() {
		return _sg_dt;
	}
	public void set_sg_dt(long _sg_dt) {
		this._sg_dt = _sg_dt;
	}

	public void setPath(CL_Pokemon pokemon, List<node_data> path) {
		this.pokemonsPaths.put(pokemon, path);
	}

	public List<node_data> getPath(CL_Pokemon pokemon) {
		return this.pokemonsPaths.get(pokemon);
	}

	public static int move(List<node_data> path) {
		return path.remove(0).getKey();
	}

	/*public int removeFirstNode() {
		return this.path.remove(0).getKey();
	}*/


}
