package com.example;

import com.example.domain.Graph;
import com.example.service.GraphColoring;
import com.example.service.GraphColoringFutureBased;
import com.example.service.LockBasedGraphColoring;
import com.example.utils.GraphUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.utils.GraphUtils.*;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Graph graph = new Graph();
        generateCompleteGraph(graph, 1000);

        //System.out.println(graph);
        GraphColoring graphColoring = new GraphColoring(graph);
        graphColoring.graphColoring(100);

        LockBasedGraphColoring lockBasedGraphColoring = new LockBasedGraphColoring(graph);
        lockBasedGraphColoring.parallelGraphColoring(100);

//        int[] colors;
//        colors = GraphColoringFutureBased.colorGraph(graph, graph.sizeOfNodes());
//        System.out.println(Arrays.toString(colors));

    }
}