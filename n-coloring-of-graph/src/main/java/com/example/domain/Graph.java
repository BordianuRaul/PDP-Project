package com.example.domain;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    List<Integer> nodes;
    List<List<Integer>> edges;

    public Graph() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
    }

    public void addEdge(int from, int to) {
        if(!edges.get(from).contains(to) && from != to) {
            edges.get(from).add(to);
        }
        if(!edges.get(to).contains(from) && from != to) {
            edges.get(to).add(from);
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
            stringBuilder.append("Node ").append(nodes.get(i)).append(" -> ");
            stringBuilder.append(edges.get(i).isEmpty() ? "No edges" : edges.get(i));
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public List<Integer> getAdjencyList(int node) {
        return edges.get(node);
    }

    public boolean isEdge(int vertex1, int vertex2)
    {
        return this.edges.get(vertex1).contains(vertex2);
    }
}
