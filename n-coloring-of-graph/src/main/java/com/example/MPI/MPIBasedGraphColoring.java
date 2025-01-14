package com.example.MPI;

import com.example.domain.Graph;
import mpi.MPI;

import java.util.Arrays;

public class MPIBasedGraphColoring {

    private Graph graph;
    private int nrColors;
    private int nrProcesses;

    public MPIBasedGraphColoring(Graph graph, int nrColors, int nrProcesses) {
        this.graph = graph;
        this.nrColors = nrColors;
        this.nrProcesses = nrProcesses;
    }

    public void colorMain() throws InterruptedException {
        int[] solution = new int[graph.sizeOfNodes()];
        int[] initialSolution = new int[graph.sizeOfNodes() + 1];
        initialSolution[0] = -1;
        System.arraycopy(solution, 0, initialSolution, 1, solution.length);

        for (int rank = 1; rank < nrProcesses; rank++) {
            MPI.COMM_WORLD.Isend(initialSolution, 0, initialSolution.length, MPI.INT, rank, 0);
        }
        int[] result = graphColoringRecursive(-1, solution, 0);

        if (result[0] == -1) {
            throw new RuntimeException("No solution found!");
        } else {
            System.out.println("Final solution: " + Arrays.toString(result));
        }
    }

    public void handleColor(int mpiId) throws InterruptedException {
        int nodeCount = graph.sizeOfNodes();
        int[] initialSolution = new int[nodeCount + 1];

        MPI.COMM_WORLD.Recv(initialSolution, 0, nodeCount + 1, MPI.INT, 0, 0);

        int prevNode = initialSolution[0];
        int[] initialCodes = new int[nodeCount];
        System.arraycopy(initialSolution, 1, initialCodes, 0, initialCodes.length);

        int[] newCodes = graphColoringRecursive(prevNode, initialCodes, mpiId);

        int[] buf = new int[nodeCount + 1];
        buf[0] = nodeCount - 1;
        System.arraycopy(newCodes, 0, buf, 1, newCodes.length);

        MPI.COMM_WORLD.Isend(buf, 0, nodeCount + 1, MPI.INT, 0, 0);
    }

    private int[] graphColoringRecursive(int solutionNode, int[] solution, int mpiId) throws InterruptedException {
        int nodeCount = graph.sizeOfNodes();
        if (!isCodeValid(solutionNode, solution)) {
            return getInvalidSolution(nodeCount);
        }
        if (solutionNode + 1 == nodeCount) {
            return solution;
        }

        int changeNode = solutionNode + 1;
        for (int currentCode = 1; currentCode <= nrColors; currentCode++) {
            solution[changeNode] = currentCode;
            int[] buf = new int[nodeCount + 1];

            buf[0] = changeNode;
            System.arraycopy(solution, 0, buf, 1, nodeCount);
            MPI.COMM_WORLD.Isend(buf, 0, buf.length, MPI.INT, (mpiId + 1) % nrProcesses, 0);

            int[] result = graphColoringRecursive(changeNode, solution, mpiId);
            if (result[0] != -1) {
                return result;
            }
        }
        return getInvalidSolution(nodeCount);
    }

    private boolean isCodeValid(int node, int[] solution) {
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
}