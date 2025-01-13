package com.example.MPI;

import com.example.domain.Graph;
import mpi.MPI;

import java.util.Arrays;

public class MPIBasedGraphColoring {

    private final Graph graph;
    private final int mpiSize;

    public MPIBasedGraphColoring(Graph graph, int mpiSize) {
        this.graph = graph;
        this.mpiSize = mpiSize;
    }

    public void colorMain(int numColors) throws InterruptedException {
        int[] solution = new int[graph.sizeOfNodes()];

        int[] initialSolution = new int[graph.sizeOfNodes() + 1]; // Node + Solution
        initialSolution[0] = -1; // No node selected yet
        System.arraycopy(solution, 0, initialSolution, 1, solution.length);

        // Distribute the work to worker processes
        for (int rank = 1; rank < mpiSize; rank++) {
            MPI.COMM_WORLD.Isend(initialSolution, 0, initialSolution.length, MPI.INT, rank, 0);
        }

        // Call the recursive graph coloring function for the main process
        int[] result = graphColoringRecursive(-1, numColors, solution, 0);

        if (result[0] == -1) {
            throw new RuntimeException("No solution found!");
        } else {
            System.out.println("Is solution correct: " + isSolutionCorrect(solution));
            System.out.println("Final solution: " + Arrays.toString(result));
        }
    }

    public void handleColor(int mpiId, int numColors) throws InterruptedException {
        int nodeCount = graph.sizeOfNodes();
        int[] initialSolution = new int[nodeCount + 1];

        // Receive initial solution from the main process
        MPI.COMM_WORLD.Recv(initialSolution, 0, nodeCount + 1, MPI.INT, 0, 0);

        // Extract the previous node and the current coloring codes
        int prevNode = initialSolution[0];
        int[] initialCodes = new int[nodeCount];
        System.arraycopy(initialSolution, 1, initialCodes, 0, initialCodes.length);

        // Apply graph coloring algorithm recursively
        int[] newCodes = graphColoringRecursive(prevNode, numColors, initialCodes, mpiId);

        // Send updated coloring solution back to the main process
        int[] buf = new int[nodeCount + 1];
        buf[0] = nodeCount - 1;
        System.arraycopy(newCodes, 0, buf, 1, newCodes.length);

        MPI.COMM_WORLD.Isend(buf, 0, nodeCount + 1, MPI.INT, 0, 0);
    }

    private int[] graphColoringRecursive(int solutionNode, int numColors, int[] solution, int mpiId) throws InterruptedException {
        int nodeCount = graph.sizeOfNodes();

        // Check if the current solution is valid with the isCodeValid function
        if (!isCodeValid(solutionNode, solution)) {
            return getInvalidSolution(nodeCount);
        }

        // Solution complete => return it (sort of a base condition)
        if (solutionNode + 1 == nodeCount) {
            return solution;
        }
        int changeNode = solutionNode + 1;

        // The next node shall be tried to be colored in all the ways possible
        for (int currentCode = 1; currentCode <= numColors; currentCode++) {
            solution[changeNode] = currentCode;

            // Send the updated solution to other processes
            int[] buf = new int[nodeCount + 1];
            buf[0] = changeNode;
            System.arraycopy(solution, 0, buf, 1, nodeCount);

            MPI.COMM_WORLD.Isend(buf, 0, buf.length, MPI.INT, (mpiId + 1) % mpiSize, 0);

            // Recursive call for the next node
            int[] result = graphColoringRecursive(changeNode, numColors, solution, mpiId);
            if (result[0] != -1) {
                return result;  // Solution found, return it
            }
        }

        // No valid solution, return an invalid solution
        return getInvalidSolution(nodeCount);
    }

    private boolean isCodeValid(int node, int[] solution) {
        // Check for conflicts with adjacent nodes
        for (int currentNode = 0; currentNode < node; currentNode++) {
            if (graph.isEdge(node, currentNode) && solution[node] == solution[currentNode]) {
                return false;
            }
        }
        return true;
    }

    private int[] getInvalidSolution(int length) {
        int[] array = new int[length];
        Arrays.fill(array, -1);
        return array;
    }

    private boolean isSolutionCorrect(int[] solution) {
        for (int node = 0; node < graph.sizeOfNodes(); node++) {
            for (int neighbor : graph.getAdjencyList(node)) {
                if (solution[neighbor] == solution[node]) {
                    return false;
                }
            }
        }
        return true;
    }
}