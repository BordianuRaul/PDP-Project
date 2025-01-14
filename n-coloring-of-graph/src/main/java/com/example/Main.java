package com.example;

import com.example.MPI.MPIBasedGraphColoring;
import com.example.domain.Graph;
import com.example.utils.GraphUtils;
import mpi.MPI;


public class Main {
    public static void main(String[] args) throws Exception {
        MPI.Init(args);

        int id = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();
        Graph graph = GraphUtils.readGraphFromFile("n-coloring-of-graph/src/main/java/com/example/utils/data.txt");
        int nrColors = 3;
        MPIBasedGraphColoring graphColoring = new MPIBasedGraphColoring(graph, nrColors, size);

        if (id == 0) {
            try {
                graphColoring.colorMain();
            } catch (Exception gce) {
                gce.printStackTrace();
            }
        } else {
            graphColoring.handleColor(id);
        }

        MPI.Finalize();
    }
}