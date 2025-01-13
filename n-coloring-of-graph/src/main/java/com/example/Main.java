package com.example;

import com.example.MPI.MPIBasedGraphColoring;
import com.example.domain.Graph;
import mpi.MPI;

import java.io.IOException;

import static com.example.utils.GraphUtils.*;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        MPI.Init(args);

        int id = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        Graph graph = readGraphFromFile( "src/main/java/com/example/utils/data.txt");
        MPIBasedGraphColoring mpiBasedGraphColoring = new MPIBasedGraphColoring(graph, size);

        if (id == 0) {
            try {

                mpiBasedGraphColoring.colorMain(graph.sizeOfNodes());
            }
            catch (Exception gce) {
                gce.printStackTrace();
            }
        }
        else {

            mpiBasedGraphColoring.handleColor(id, graph.sizeOfNodes());
        }

        MPI.Finalize();
    }
}