package com.example.service;

import com.example.domain.Graph;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

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
        executor = Executors.newFixedThreadPool(6); // You could dynamically adjust this based on the graph size
    }

    public boolean parallelGraphColoring(int nrColors) {
        int[] colors = new int[graph.sizeOfNodes()];
        CountDownLatch latch = new CountDownLatch(graph.sizeOfNodes());

        // Submit a task to color each node
        for (int node = 0; node < graph.sizeOfNodes(); node++) {
            int currentNode = node;
            executor.submit(() -> {
                try {
                    colorNode(currentNode, nrColors, colors);
                } finally {
                    latch.countDown(); // Ensure latch countdown after task completes
                }
            });
        }

        try {
            latch.await(); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

//        System.out.println("\n ----PARALLEL---- \n");
//        printSolution(colors);

        // Properly shut down the executor service after all tasks are finished
        shutdown();

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
        // Combine the current node with its neighbors
        List<Integer> nodesToLock = neighbors.stream()
                .distinct() // Avoid duplicates
                .collect(Collectors.toList());

        nodesToLock.add(node); // Add the current node
        nodesToLock.sort(Integer::compareTo); // Sort all locks in a consistent order

        // Lock all nodes in the sorted order
        for (int current : nodesToLock) {
            colorLocks[current].lock();
        }
    }

    private void unlockColors(int node, List<Integer> neighbors) {
        // Combine the current node with its neighbors
        List<Integer> nodesToUnlock = neighbors.stream()
                .distinct()
                .collect(Collectors.toList());

        nodesToUnlock.add(node); // Add the current node
        nodesToUnlock.sort((a, b) -> b - a); // Sort all nodes in reverse order

        // Unlock all nodes in the reverse sorted order
        for (int current : nodesToUnlock) {
            colorLocks[current].unlock();
        }
    }

    // Properly shut down the executor
    private void shutdown() {
        try {
            // Shutdown the executor and await its termination
            executor.shutdown();
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate in time.");
                }
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public int[] parallelGraphColoringForTest(int nrColors) {
        int[] colors = new int[graph.sizeOfNodes()];
        int nrNodes = graph.sizeOfNodes();
        int nrThreads = 6; // Number of threads; can be adjusted dynamically
        int chunkSize = (int) Math.ceil((double) nrNodes / nrThreads);

        CountDownLatch latch = new CountDownLatch(nrThreads);

        // Submit tasks for each chunk
        for (int threadId = 0; threadId < nrThreads; threadId++) {
            int startNode = threadId * chunkSize;
            int endNode = Math.min(startNode + chunkSize, nrNodes); // Ensure not exceeding the graph size

            executor.submit(() -> {
                try {
                    // Process the assigned chunk
                    for (int node = startNode; node < endNode; node++) {
                        colorNode(node, nrColors, colors);
                    }
                } finally {
                    latch.countDown(); // Ensure latch countdown after task completes
                }
            });
        }

        try {
            latch.await(); // Wait for all tasks to complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

//        System.out.println("\n ----PARALLEL---- \n");
//        printSolution(colors);

        // Properly shut down the executor service after all tasks are finished
        shutdown();

        return colors;
    }
}
