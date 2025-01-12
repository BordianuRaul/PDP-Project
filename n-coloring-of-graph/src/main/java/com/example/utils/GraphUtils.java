package com.example.utils;

import com.example.domain.Graph;

import java.io.*;
import java.util.Random;

public class GraphUtils {

    public static void addNodes(Graph graph, int nrNodes) {
        while(nrNodes > 0) {
            graph.addNode();
            nrNodes--;
        }
    }

    public static void addEdges(Graph graph, int nrEdges) {
        Random random = new Random();
        for(int i = 0; i < nrEdges; i++) {
            int from = random.nextInt(graph.sizeOfNodes());
            int to = random.nextInt(graph.sizeOfNodes());

            graph.addEdge(from, to);
        }
    }

    public static void writeGraphToFile(Graph graph, String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the number of nodes
            writer.write(graph.sizeOfNodes() + "\n");

            // Write each edge (from, to)
            for (int i = 0; i < graph.sizeOfNodes(); i++) {
                for (Integer neighbor : graph.getAdjencyList(i)) {
                    writer.write(i + " " + neighbor + "\n");
                }
            }
        }
    }

    // Method to read the graph from a file
    public static Graph readGraphFromFile(String filePath) throws IOException {
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int nrNodes = Integer.parseInt(reader.readLine().trim()); // Read number of nodes
            addNodes(graph, nrNodes); // Add nodes to the graph

            String line;
            while ((line = reader.readLine()) != null) {
                String[] edgeData = line.trim().split(" ");
                int from = Integer.parseInt(edgeData[0]);
                int to = Integer.parseInt(edgeData[1]);

                graph.addEdge(from, to); // Add the edge to the graph
            }
        }
        return graph;
    }

    public static void generateCompleteGraph(Graph graph, int nrNodes) {
        addNodes(graph, nrNodes); // First, add the nodes to the graph

        // Create edges between all pairs of nodes
        for (int i = 0; i < nrNodes; i++) {
            for (int j = i + 1; j < nrNodes; j++) {
                graph.addEdge(i, j); // Add edge from node i to node j
                graph.addEdge(j, i); // Add edge from node j to node i (since it's undirected)
            }
        }
    }

}
