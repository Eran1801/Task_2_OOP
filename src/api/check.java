package api;

import java.util.List;

public class check {

    public static void main(String[] args) {

        dw_graph_algorithms ga = new DWGraph_Algo();

        ga.load("src/api/json_files/G_10_80_0.json");

        List<node_data> list_ans = ga.shortestPath(0,5);

        System.out.println(list_ans);

    }
}
