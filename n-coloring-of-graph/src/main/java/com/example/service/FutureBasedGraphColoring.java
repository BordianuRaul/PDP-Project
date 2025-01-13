package com.example.service;

import com.example.domain.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FutureBasedGraphColoring {
    public static int[] colorGraph(Graph graph, int numColors) throws ExecutionException, InterruptedException {
        int[] solution = new int[graph.sizeOfNodes()];
        return colorGraphRec(0, graph, solution, numColors);
    }

    public static int[] colorGraphRec(int node, Graph graph, int[] solution, int numColors) throws ExecutionException, InterruptedException {
        int total = graph.sizeOfNodes();
        if(node == total) {
            return solution;
        }

        ExecutorService executorService = Executors.newFixedThreadPool(numColors);
        List<Future<int[]>> tasks = new ArrayList<>();

        for (int color = 1; color <= numColors; color++) {

            if (isValidColor(node, color, graph, solution)) {
                int[] newSolution = solution.clone();
                newSolution[node] = color;

                tasks.add(executorService.submit(() -> colorGraphRec(node + 1, graph, newSolution, numColors)));
            }
        }

        for (Future<int[]> task : tasks) {
            int[] result = task.get();

            if (isValidPartialSolution(result)) {
                executorService.shutdownNow();
                return result;
            }
        }

        executorService.shutdown();
        return invalidSolution(total);
    }

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
