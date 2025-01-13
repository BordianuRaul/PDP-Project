package com.example.service;

import com.example.domain.Graph;
import com.example.utils.GraphUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.example.utils.GraphUtils.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class ParallelGraphColoringTest {
    @Test
    void testColoringOfGraph() throws ExecutionException, InterruptedException {
        Graph graph = new Graph();
        GraphUtils.addNodes(graph, 15);
        GraphUtils.addEdges(graph, 10);

        //LockBasedGraphColoring graphColoring = new LockBasedGraphColoring(graph);

        //int[] colors = graphColoring.graphColoringForTest(5000);
        FutureBasedGraphColoring coloring = new FutureBasedGraphColoring(graph);
        int[] colors = coloring.colorGraph(graph.sizeOfNodes());
        if(colors == null) {
            fail("Graph coloring failed");
        }


        for(int node = 0; node < graph.sizeOfNodes(); node++) {
            for(int neighbor : graph.getAdjencyList(node)) {
                if(colors[neighbor] == colors[node]) {
                    System.out.println(node + " " + colors[node] + " " + neighbor + " " + colors[neighbor]);
                    fail();
                }
            }
        }
    }

    @Test
    void testColoringOfCompleteGraph() {

        int nrNodes = 1000;
        Graph graph = new Graph();
        GraphUtils.generateCompleteGraph(graph, nrNodes);

        // Measure time for sequential graph coloring
        long startTimeSequential = System.nanoTime();
        GraphColoring sequentialColoring = new GraphColoring(graph);
        int[] sequentialColors = sequentialColoring.graphColoringForTest(nrNodes);
        long endTimeSequential = System.nanoTime();
        long durationSequential = endTimeSequential - startTimeSequential;

        System.out.println("Time for sequential graph coloring: " + durationSequential / 1_000_000 + " ms");

        // Measure time for lock-based parallel graph coloring
        int[] parallelColors;
        long startTimeParallel = System.nanoTime();
        LockBasedGraphColoring parallelColoring = new LockBasedGraphColoring(graph);
        parallelColors = parallelColoring.parallelGraphColoringWithChunks(nrNodes);
        long endTimeParallel = System.nanoTime();
        long durationParallel = endTimeParallel - startTimeParallel;

        // Assert that the graph coloring was successful for both algorithms
        assertNotNull(sequentialColors, "Sequential graph coloring failed");
        assertNotNull(parallelColors, "Parallel graph coloring failed");

        // Print the results

        System.out.println("Time for parallel (lock-based) graph coloring: " + durationParallel / 1_000_000 + " ms");

        for (int node = 0; node < nrNodes; node++) {
            for (int neighbor : graph.getAdjencyList(node)) {
                if (sequentialColors[neighbor] == sequentialColors[node]) {
                    System.out.println(node + " " + sequentialColors[node] + " " + neighbor + " " + sequentialColors[neighbor]);
                    fail("Sequential coloring failed for nodes: " + node + " and " + neighbor);
                }
                if (parallelColors[neighbor] == parallelColors[node]) {
                    System.out.println(node + " " + parallelColors[node] + " " + neighbor + " " + parallelColors[neighbor]);
                    fail("Parallel coloring failed for nodes: " + node + " and " + neighbor);
                }
            }
        }
    }

    @Test
    void printParallel() {
        Graph graph = new Graph();
        addNodes(graph, 100);
        addEdges(graph, 100);
//        //System.out.println(graph);
//        GraphColoring graphColoring = new GraphColoring(graph);
//        graphColoring.graphColoring(10);
//
        LockBasedGraphColoring lockBasedGraphColoring = new LockBasedGraphColoring(graph);
        int[] colors = lockBasedGraphColoring.parallelGraphColoringWithChunks(graph.sizeOfNodes());
        System.out.println(Arrays.toString(colors));
    }

    @Test
    void addToFile() throws IOException {
        Graph graph = new Graph();
        addNodes(graph, 50);
        addEdges(graph, 1500);

        writeGraphToFile(graph, "src/main/java/com/example/utils/data.txt");
    }
}
