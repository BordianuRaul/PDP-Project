package com.example;

import com.example.domain.Graph;
import com.example.service.FutureBasedGraphColoring;
import com.example.service.GraphColoring;
import com.example.service.LockBasedGraphColoring;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.utils.GraphUtils.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Graph graph = new Graph();
        //generateCompleteGraph(graph, 15);
        addNodes(graph, 20);
        addEdges(graph, 150);
//        //System.out.println(graph);
//        GraphColoring graphColoring = new GraphColoring(graph);
//        graphColoring.graphColoring(10);
//
        LockBasedGraphColoring lockBasedGraphColoring = new LockBasedGraphColoring(graph);
        lockBasedGraphColoring.parallelGraphColoringWithChunks(graph.sizeOfNodes());

//        System.out.println("\nBKT");
//        int[] colors;
//        colors = FutureBasedGraphColoring.colorGraph(graph, graph.sizeOfNodes());
//        System.out.println(Arrays.toString(colors));


    }
}