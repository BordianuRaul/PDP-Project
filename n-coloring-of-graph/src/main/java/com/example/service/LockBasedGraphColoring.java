package com.example.service;

import com.example.domain.Graph;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class LockBasedGraphColoring extends GraphColoring {

    private final ReentrantLock[] colorLocks;
    private final ExecutorService executor;

    public LockBasedGraphColoring(Graph graph) {
        super(graph);
        // Create locks for each color index
        colorLocks = new ReentrantLock[graph.sizeOfNodes()];
        for (int i = 0; i < graph.sizeOfNodes(); i++) {
            colorLocks[i] = new ReentrantLock();
        }
        executor = Executors.newFixedThreadPool(4);
    }

    public boolean parallelGraphColoring(int nrColors) {
        int[] colors = new int[graph.sizeOfNodes()];
        CountDownLatch latch = new CountDownLatch(graph.sizeOfNodes());

        for (int node = 0; node < graph.sizeOfNodes(); node++) {
            int currentNode = node;
            executor.submit(() -> {
                try {
                    colorNode(currentNode, nrColors, colors);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        System.out.println("\n ----PARALLEL---- \n");
        printSolution(colors);
        return true;
    }

    private void colorNode(int node, int nrColors, int[] colors) {
        List<Integer> neighbors = graph.getAdjencyList(node);

        // Lock the node's color and its neighbors' colors
        lockColors(node, neighbors);
        try {
            // Try coloring the node
            for (int color = 1; color <= nrColors; color++) {
                if (isSafe(node, color, colors)) {
                    colors[node] = color;
                    break; // Assign the first valid color
                }
            }
        } finally {
            // Unlock the node's color and its neighbors' colors
            unlockColors(node, neighbors);
        }
    }

    private void lockColors(int node, List<Integer> neighbors) {
        // Lock the colors in a consistent order to avoid deadlocks
        List<Integer> nodesToLock = neighbors.stream()
                .sorted() // Ensure order
                .toList();
        for (int neighbor : nodesToLock) {
            colorLocks[neighbor].lock();
        }
        colorLocks[node].lock();
    }

    private void unlockColors(int node, List<Integer> neighbors) {
        // Unlock the colors in reverse order
        colorLocks[node].unlock();
        List<Integer> nodesToUnlock = neighbors.stream()
                .sorted((a, b) -> b - a) // Reverse order
                .toList();
        for (int neighbor : nodesToUnlock) {
            colorLocks[neighbor].unlock();
        }
    }

    public void shutdown() {
        executor.shutdown();
    }
}
