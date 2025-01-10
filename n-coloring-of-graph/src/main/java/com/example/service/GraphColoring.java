package com.example.service;

import com.example.domain.Graph;
import java.util.Arrays;

public class GraphColoring {
    Graph graph;
    int[] colors;

    public GraphColoring(Graph graph) {
        this.graph = graph;
        this.colors = new int[graph.sizeOfNodes()];
        Arrays.fill(colors, 0);
    }

    public boolean solveNColoring(int currentNode, int n){
        if(currentNode == graph.sizeOfNodes()){
            // We have coloured all nodes
            return true;
        }

        // Check all colors for current node
        for(int color = 1; color <= n; color++){
            // If there are no neighbors of this color
            if(isValidColorForNode(currentNode, color)){
                colors[currentNode] = color;
                if(solveNColoring(currentNode + 1, n)){
                    return true;
                }
                // Backtrack
                colors[currentNode] = 0;
            }
        }

        // If we have gone through all colors without finding a valid one for this node, return false
        return false;
    }

    private boolean isValidColorForNode(int node, int color) {
        // Parse through the current node's neighbors
        for(int neighboringNode : graph.getAdjencyList(node)){
            if(colors[neighboringNode] == color){
                // Return false if a neighbor is already of the desired color
                return false;
            }
        }
        return true;
    }

    public void printColoring(){
        System.out.println("-----Coloring of the graph:-----");
        for (int i = 0; i < colors.length; i++) {
            System.out.println("Node " + (i + 1) + " -> Color " + colors[i]);
        }
    }
}
