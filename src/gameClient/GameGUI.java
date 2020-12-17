package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

public class GameGUI extends JFrame{
    private Arena _ar;
    private gameClient.util.Range2Range _w2f;
    private Timer timer;


    GameGUI(Arena _ar) {
        this._ar = _ar;
        ImageIcon iconGraph = new ImageIcon("src/gameClient/pic/Graph.png");
        this.setIconImage(iconGraph.getImage());
        this.setSize( 800 , 600 );
        this.setResizable(true);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateFrame();
            }

        });


        this.setVisible(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        refreshScreen();
    }

    private void updateFrame() {
        Range rx = new Range(20, this.getWidth() - 20);
        Range ry = new Range(this.getHeight() - 10, 150);
        Range2D frame = new Range2D(rx, ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g, frame);
    }

    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        //	updateFrame();

        Image tempPaintingImage;
        Graphics tempPaintingGraphics;
        tempPaintingImage = createImage(w, h);
        tempPaintingGraphics = tempPaintingImage.getGraphics();
        tempPaintingGraphics.clearRect(0, 0, w, h);
        drawGraph(tempPaintingGraphics);
        drawPokemons(tempPaintingGraphics);
        drawAgants(tempPaintingGraphics);
        drawInfo(tempPaintingGraphics);
        g.drawImage(tempPaintingImage, 0, 0, this);
    }

    private void drawInfo(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 18));
        g.setColor(Color.black);
        int totalValue = 0;
        for (CL_Agent agent : _ar.getAgents()) {
            totalValue += agent.getValue();
        }
        g.drawString("Time Left : " + _ar.getTime() / 1000,20, 50);
        g.drawString("Level : " + _ar.getLevel(), getWidth()/2-100, 50);
        g.drawString("Total Value : " + totalValue, getWidth()-180, 50);
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while (iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.blue);
            drawNode(n, 5, g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while (itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.gray);
                drawEdge(e, g);
            }
        }
    }

    private void drawPokemons(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 10));
        List<CL_Pokemon> fs = _ar.getPokemons(); // the Pokemon's are in the arena
        if (fs != null) {
            Iterator<CL_Pokemon> itr = fs.iterator();

            while (itr.hasNext()) {

                CL_Pokemon f = itr.next();
                Point3D c = f.getLocation();
                int r = 10;
                g.setColor(Color.green);
                if (f.getType() < 0) {
                    g.setColor(Color.orange);
                }
                if (c != null) {
                    geo_location fp = this._w2f.world2frame(c);
                    g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
                    //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);

                }
            }
        }
    }

    private void drawAgants(Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 10));
        List<CL_Agent> rs = _ar.getAgents();
        g.setColor(Color.red);// the color of the agents
        int i = 0; // runs on the agent amount
        while (rs != null && i < rs.size()) {
            geo_location c = rs.get(i).getLocation(); // getting the agent in location 'i'
            int r = 8;
            if (c != null) {
                geo_location fp = this._w2f.world2frame(c);
                g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
                g.drawString("" + rs.get(i).getValue(), (int) fp.x()-r, (int) fp.y() - 10);
            }
            i++;
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 10));
        geo_location pos = n.getLocation(); // the position of the node
        geo_location fp = this._w2f.world2frame(pos);
        g.fillOval((int) fp.x() - r, (int) fp.y() - r, 2 * r, 2 * r);
        g.drawString("" + n.getKey(), (int) fp.x(), (int) fp.y() - 4 * r);
    }

    private void drawEdge(edge_data e, Graphics g) {
        g.setFont(new Font(Font.SANS_SERIF,  Font.BOLD, 10));
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int) s0.x(), (int) s0.y(), (int) d0.x(), (int) d0.y());
        int type = e.getSrc() > e.getDest() ? 1 : -1;
        Point3D middlePoint = ((Point3D) (s0)).getMiddlePoint(d0);
        double eWeight = e.getWeight();
        g.setColor(type > 0 ? Color.green : Color.orange);

        eWeight = Double.parseDouble(new DecimalFormat("##.##").format(eWeight));
        g.drawString("" + eWeight, (int) middlePoint.x(), type > 0 ? (int) middlePoint.y() + 20 : (int) middlePoint.y() - 20);
        //	g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
    }

    public void refreshScreen() {
        timer = new Timer(0, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                repaint();
            }
        });
        timer.setRepeats(true);
        timer.setDelay(16);
        timer.start();
    }
}
