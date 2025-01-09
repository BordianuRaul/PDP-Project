package com.example.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GraphTest {
    @Test
    void testGraphCorrectness() {
        // Create a new Graph instance
        Graph graph = new Graph();

        // Add nodes
        graph.addNode(); // Node 1
        graph.addNode(); // Node 2
        graph.addNode(); // Node 3

        // Verify node count
        assertEquals(3, graph.sizeOfNodes(), "Number of nodes should be 3");

        // Add edges
        graph.addEdge(0, 1); // Edge from Node 1 -> Node 2
        graph.addEdge(1, 2); // Edge from Node 2 -> Node 3
        graph.addEdge(0, 2); // Edge from Node 1 -> Node 3

        // Verify the structure of edges
        assertEquals(List.of(1, 2), graph.edges.get(0), "Edges for Node 1 are incorrect");
        assertEquals(List.of(2), graph.edges.get(1), "Edges for Node 2 are incorrect");
        assertEquals(List.of(), graph.edges.get(2), "Edges for Node 3 are incorrect");

        // Verify the graph's string representation
        String expectedToString = """
                Graph:
                Node 1 -> [1, 2]
                Node 2 -> [2]
                Node 3 -> No edges
                """;
        assertEquals(expectedToString.trim(), graph.toString().trim(), "Graph string representation is incorrect");

        // Add duplicate edge and verify no duplication
        graph.addEdge(0, 2); // Adding Node 1 -> Node 3 again
        assertEquals(List.of(1, 2), graph.edges.get(0), "Duplicate edges should not be added");

        // Add an edge to a non-existing node and verify no exception is thrown (optional based on requirements)
        assertThrows(IndexOutOfBoundsException.class, () -> graph.addEdge(3, 4), "Adding an edge to a non-existing node should throw an exception");
    }
}
