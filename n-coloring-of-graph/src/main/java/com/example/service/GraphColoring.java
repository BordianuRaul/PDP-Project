package com.example.service;

import com.example.domain.Graph;

import java.util.List;

public class GraphColoring {
    Graph graph;


    public GraphColoring(Graph graph) {
        this.graph = graph;
    }

    public boolean isSafe(int node, int color, int[] colors) {
        List<Integer> adjency = graph.getAdjencyList(node);

        for(Integer currentNode : adjency) {
            if(colors[currentNode] == color) {
                return false;
            }
        }
        return true;
    }

    public boolean graphColoringUtil(int nrColors, int[] colors, int node) {
        if(node == graph.sizeOfNodes()) {
            return true;
        }

        for(int currentColor = 1; currentColor <= nrColors; currentColor++) {
            if(isSafe(node, currentColor, colors)) {
                colors[node] = currentColor;
                if(graphColoringUtil(nrColors, colors, node + 1)) {
                    return true;
                }
                colors[node] = 0;
            }
        }
        return false;
    }

    public boolean graphColoring(int nrColors) {
        int[] colors = new int[graph.sizeOfNodes()];

        if(!graphColoringUtil(nrColors, colors, 0)) {
            System.out.println("Graph coloring failed");
            return false;
        }

        printSolution(colors);
        return true;
    }

    public void printSolution(int[] colors) {
        System.out.println("Solution exists\n");

        for(int node = 0; node < graph.sizeOfNodes(); node++) {
            System.out.println("node: " + node + " " + colors[node] + " ");
        }
    }

    public int[] graphColoringForTest(int nrColors) {
        int[] colors = new int[graph.sizeOfNodes()];

        if(!graphColoringUtil(nrColors, colors, 0)) {
            return null;
        }

        return colors;
    }

}
