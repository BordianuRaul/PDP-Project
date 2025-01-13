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
        colorLocks = new ReentrantLock[graph.sizeOfNodes()];
        for (int i = 0; i < graph.sizeOfNodes(); i++) {
            colorLocks[i] = new ReentrantLock();
        }
        executor = Executors.newFixedThreadPool(graph.sizeOfNodes());
    }
    private void colorNode(int node, int nrColors, int[] colors) {
        List<Integer> neighbors = graph.getAdjencyList(node);

        lockColors(node, neighbors);
        try {
            for (int color = 1; color <= nrColors; color++) {
                if (isSafe(node, color, colors)) {
                    colors[node] = color;
                    break;
                }
            }
        } finally {
            unlockColors(node, neighbors);
        }
    }

    private void lockColors(int node, List<Integer> neighbors) {
        List<Integer> nodesToLock = neighbors.stream()
                .distinct()
                .collect(Collectors.toList());

        nodesToLock.add(node);
        nodesToLock.sort(Integer::compareTo);

        for (int current : nodesToLock) {
            colorLocks[current].lock();
        }
    }

    private void unlockColors(int node, List<Integer> neighbors) {
        List<Integer> nodesToUnlock = neighbors.stream()
                .distinct()
                .collect(Collectors.toList());

        nodesToUnlock.add(node);
        nodesToUnlock.sort((a, b) -> b - a);

        for (int current : nodesToUnlock) {
            colorLocks[current].unlock();
        }
    }

    private void shutdown() {
        try {
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

    public int[] parallelGraphColoringWithChunks(int nrColors) {
        int[] colors = new int[graph.sizeOfNodes()];
        int nrNodes = graph.sizeOfNodes();
        int nrThreads = 6;
        int chunkSize = (int) Math.ceil((double) nrNodes / nrThreads);

        CountDownLatch latch = new CountDownLatch(nrThreads);

        for (int threadId = 0; threadId < nrThreads; threadId++) {
            int startNode = threadId * chunkSize;
            int endNode = Math.min(startNode + chunkSize, nrNodes);

            executor.submit(() -> {
                try {
                    for (int node = startNode; node < endNode; node++) {
                        colorNode(node, nrColors, colors);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

//        System.out.println("\n ----PARALLEL---- \n");
//        printSolution(colors);

        shutdown();

        return colors;
    }
}
