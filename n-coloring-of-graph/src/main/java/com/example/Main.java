package com.example;

import com.example.MPI.MPIBasedGraphColoring;
import com.example.domain.Graph;
import com.example.service.FutureBasedGraphColoring;
import com.example.service.GraphColoring;
import com.example.service.LockBasedGraphColoring;
import mpi.MPI;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.utils.GraphUtils.*;

public class Main {
    //static int nrNodes = 20;
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        MPI.Init(args);

        int id = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        Graph graph = readGraphFromFile( "src/main/java/com/example/utils/data.txt");
        //System.out.println(graph);

        if (id == 0) {

            try {

                MPIBasedGraphColoring.graphColoringMainMPI(graph, graph.sizeOfNodes(), size);
            }
            catch (Exception gce) {
                gce.printStackTrace();
            }
        }
        else {

            MPIBasedGraphColoring.graphColoringWorkerMPI(id, graph, graph.sizeOfNodes());
        }

        MPI.Finalize();
    }
}