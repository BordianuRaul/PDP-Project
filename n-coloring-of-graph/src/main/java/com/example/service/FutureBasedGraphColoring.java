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

    private static boolean flag = false;

    public static int[] colorGraph(Graph graph, int numColors) throws ExecutionException, InterruptedException {
        flag = false;
        int[] solution = new int[graph.sizeOfNodes()];
        return colorGraphRec(0, graph, solution, numColors);
    }

    public static int[] colorGraphRec(int node, Graph graph, int[] solution, int numColors) throws ExecutionException, InterruptedException {
        int total = graph.sizeOfNodes();
        if (node == total) {
            flag = true;
            return solution;
        }

        if(flag) {
            return invalidSolution(total);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        List<Future<int[]>> tasks = new ArrayList<>();

        for (int color = 1; color <= numColors; color++) {
            if (isValidColor(node, color, graph, solution)) {
                int[] newSolution = solution.clone();
                newSolution[node] = color;

                tasks.add(executorService.submit(() -> {
                    try {
                        return colorGraphRec(node + 1, graph, newSolution, numColors);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return invalidSolution(total);
                    }
                }));
            }
        }

        int[] result = null;
        for (Future<int[]> task : tasks) {
            int[] partialResult = task.get();

            if (isValidPartialSolution(partialResult)) {
                result = partialResult;
                break;
            }
        }

        executorService.shutdown(); // Proper shutdown after all tasks are processed
        return result != null ? result : invalidSolution(total);
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
