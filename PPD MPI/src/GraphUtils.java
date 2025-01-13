import java.io.*;
import java.util.Random;

public class GraphUtils {
    public static void addEdges(Graph graph, int nrEdges) {
        Random random = new Random();
        for (int i = 0; i < nrEdges; i++) {
            int from = random.nextInt(graph.getNoNodes());
            int to = random.nextInt(graph.getNoNodes());

            graph.setEdge(from, to);
        }
    }

    public static void writeToFile(Graph graph, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

        // Write the number of nodes
        writer.write(String.valueOf(graph.getNoNodes()));
        writer.newLine();

        // Write each edge
        for (Node node : graph.getNodes()) {
            for (Node adjacentNode : node.getAdjacentNodes()) {
                if (node.getId() < adjacentNode.getId()) { // Avoid duplicating edges
                    writer.write(node.getId() + " " + adjacentNode.getId());
                    writer.newLine();
                }
            }
        }

        writer.close();
    }

    // Reads the graph from a file
    public static Graph readFromFile(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        int nodeCount = Integer.parseInt(reader.readLine());
        Graph graph = new Graph(nodeCount);

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            int node1Id = Integer.parseInt(parts[0]);
            int node2Id = Integer.parseInt(parts[1]);
            graph.setEdge(node1Id, node2Id);
        }

        reader.close();
        return graph;
    }
}