package com.example;

import com.example.domain.Graph;
import com.example.utils.GraphUtils;

import static com.example.utils.GraphUtils.*;

public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();
        addNodes(graph,10);
        addEdges(graph,20);

        System.out.println(graph);
    }
}