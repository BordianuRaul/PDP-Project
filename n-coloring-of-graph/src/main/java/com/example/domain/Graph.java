package com.example.domain;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    List<Integer> nodes;
    public List<List<Integer>> edges;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addEdge(int from, int to) {
        if(!edges.get(from).contains(to)) {
            edges.get(from).add(to);
        }
    }

    public void addNode() {
        nodes.add(nodes.size());
        edges.add(new ArrayList<>());
    }

    public int sizeOfNodes() {
        return nodes.size();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Graph:\n");
        for (int i = 0; i < nodes.size(); i++) {
            // Print as 1-indexed
            stringBuilder.append("Node ").append(i + 1).append(" -> ");

            List<Integer> adjacencyList = edges.get(i);
            if (adjacencyList.isEmpty()) {
                stringBuilder.append("No edges");
            } else {
                List<Integer> oneBasedAdjacencyList = new ArrayList<>();
                for (Integer neighbor : adjacencyList) {
                    oneBasedAdjacencyList.add(neighbor + 1);
                }
                stringBuilder.append(oneBasedAdjacencyList);
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public List<Integer> getAdjencyList(int node) {
        return edges.get(node);
    }
}
