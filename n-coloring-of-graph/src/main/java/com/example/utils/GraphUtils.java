package com.example.utils;

import com.example.domain.Graph;

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

}
