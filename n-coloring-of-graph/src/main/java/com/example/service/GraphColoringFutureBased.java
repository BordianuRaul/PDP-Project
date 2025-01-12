package com.example.service;

import com.example.domain.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GraphColoringFutureBased {
    public static int[] colorGraph(Graph graph, int numColors) throws ExecutionException, InterruptedException {
        int[] solution = new int[graph.sizeOfNodes()];
        return colorGraphRec(0, graph, solution, numColors);
    }

    public static int[] colorGraphRec(int node, Graph graph, int[] solution, int numColors) throws ExecutionException, InterruptedException {
        int total = graph.sizeOfNodes();
        if(node == total) {
            return solution;  // return solution when all nodes are colored
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numColors);
        List<Future<int[]>> tasks = new ArrayList<>();

        for (int color = 1; color <= numColors; color++) {
            // Validate color
            if (isValidColor(node, color, graph, solution)) {
                int[] newSolution = solution.clone();
                newSolution[node] = color;

                // Submit the recursive coloring task
                tasks.add(executorService.submit(() -> colorGraphRec(node + 1, graph, newSolution, numColors)));
            }
        }

        for (Future<int[]> task : tasks) {
            int[] result = task.get();

            if (isValidPartialSolution(result)) {
                executorService.shutdownNow();
                return result;  // Return the valid solution found
            }
        }

        executorService.shutdown();
        return invalidSolution(total);  // If no valid solution found
    }

    /// Returns true if all the neighbors of a node are different color from it; false otherwise
    private static boolean isValidColor(int node, int color, Graph graph, int[] solution)
    {
        for(int neighbor : graph.getAdjencyList(node))
        {
            if(solution[neighbor] == color)
                return false;
        }
        return true;
    }

    public static boolean isValidPartialSolution(int[] result)
    {
        return result[0] != -1;
    }

    private static int[] invalidSolution(int length)
    {
        int[] array = new int[length];
        Arrays.fill(array, -1);
        return array;
    }

}
