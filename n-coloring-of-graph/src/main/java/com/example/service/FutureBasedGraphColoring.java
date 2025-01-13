package com.example.service;

import com.example.domain.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

public class FutureBasedGraphColoring {

    private final Graph graph;
    private boolean flag = false;

    public FutureBasedGraphColoring(Graph graph) {
        this.graph = graph;
    }

    public int[] colorGraph(int numColors) throws ExecutionException, InterruptedException {
        flag = false;
        int[] solution = new int[graph.sizeOfNodes()];
        return colorGraphRecursive(0, solution, numColors);
    }

    private int[] colorGraphRecursive(int node, int[] solution, int numColors) throws ExecutionException, InterruptedException {
        int total = graph.sizeOfNodes();
        if (node == total) {
            flag = true;
            return solution;
        }

        if (flag) {
            return createInvalidSolution(total);
        }

        ExecutorService executorService = Executors.newFixedThreadPool(6);
        List<Future<int[]>> tasks = new ArrayList<>();

        for (int color = 1; color <= numColors; color++) {
            if (isValidColor(node, color, solution)) {
                int[] newSolution = solution.clone();
                newSolution[node] = color;

                tasks.add(executorService.submit(() -> {
                    try {
                        return colorGraphRecursive(node + 1, newSolution, numColors);
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                        return createInvalidSolution(total);
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
        return result != null ? result : createInvalidSolution(total);
    }

    private boolean isValidColor(int node, int color, int[] solution) {
        for (int neighbor : graph.getAdjencyList(node)) {
            if (solution[neighbor] == color) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidPartialSolution(int[] result) {
        return result[0] != -1;
    }

    private int[] createInvalidSolution(int length) {
        int[] array = new int[length];
        Arrays.fill(array, -1);
        return array;
    }
}
