package com.example.service;

import com.example.domain.Graph;
import com.example.utils.GraphUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class ParallelGraphColoringTest {
    @Test
    void testColoringOfGraph() {
        Graph graph = new Graph();
        GraphUtils.addNodes(graph, 5000);
        GraphUtils.addEdges(graph, 10000000);

        LockBasedGraphColoring graphColoring = new LockBasedGraphColoring(graph);

        int[] colors = graphColoring.graphColoringForTest(5000);
        if(colors == null) {
            fail("Graph coloring failed");
        }


        for(int node = 0; node < 100; node++) {
            for(int neighbor : graph.getAdjencyList(node)) {
                if(colors[neighbor] == colors[node]) {
                    System.out.println(node + " " + colors[node] + " " + neighbor + " " + colors[neighbor]);
                    fail();
                }
            }
        }
    }
}
