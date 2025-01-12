package com.example.service;

import com.example.domain.Graph;
import com.example.utils.GraphUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

class GraphColoringTest {
    @Test
    void testColoringOfGraph() {
        Graph graph = new Graph();
        GraphUtils.addNodes(graph, 1000);
        GraphUtils.addEdges(graph, 100000);

        GraphColoring graphColoring = new GraphColoring(graph);

        int[] colors = graphColoring.graphColoringForTest(5000);
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
}
