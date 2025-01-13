import mpi.MPI;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Initialize MPI
        MPI.Init(args);

        // Get MPI rank and size (number of processes)
        int mpiId = MPI.COMM_WORLD.Rank();
        int mpiSize = MPI.COMM_WORLD.Size();

        // Set the number of colors for coloring
        int noColors = 4;  // For example, let's use 4 colors
        Colors.setNoColors(noColors);

        // Set color names (optional)
        Colors.setColorName(1, "Red");
        Colors.setColorName(2, "Green");
        Colors.setColorName(3, "Blue");
        Colors.setColorName(4, "Yellow");

        // Create a graph with a specified number of nodes (e.g., 10 nodes)
        Graph graph = new Graph(10);

        // Add some random edges to the graph (let's add 15 edges as an example)
        GraphUtils.addEdges(graph, 15);

        // Write the graph to a file
        String filename = "graph.txt";
        GraphUtils.writeToFile(graph, filename);
        System.out.println("Graph written to file: " + filename);

        // Read the graph back from the file (optional, to verify the graph)
        Graph readGraph = GraphUtils.readFromFile(filename);
        System.out.println("Graph read from file: " + readGraph.getNoNodes() + " nodes");

        // Perform graph coloring using MPI
        if (mpiId == 0) {
            // Main process initiates the coloring process
            System.out.println("Starting graph coloring...");
            GraphColoring.graphColoringMain(readGraph);
        } else {
            // Worker processes handle partial solutions
            GraphColoring.graphColoringWorker(mpiId, readGraph);
        }

        // Finalize MPI
        MPI.Finalize();
    }
}
