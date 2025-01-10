package com.example;

import com.example.domain.Graph;
import com.example.service.GraphColoring;
import com.example.utils.GraphUtils;

import static com.example.utils.GraphUtils.*;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        addNodes(graph,4);
        addEdges(graph,5);

        System.out.println(graph);
        GraphColoring graphColoring = new GraphColoring(graph);
        graphColoring.graphColoring(4);
    }
}