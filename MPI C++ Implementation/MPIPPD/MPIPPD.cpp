#include <mpi.h>
#include <iostream>
#include <vector>
#include <algorithm>

using namespace std;

class Graph {
public:
    vector<vector<int>> adjList;
    int numNodes;

    Graph(int n) : numNodes(n) {
        adjList.resize(n);
    }

    void addEdge(int u, int v) {
        adjList[u].push_back(v);
        adjList[v].push_back(u);
    }

    void printGraph(int rank) {
        if (rank == 0) {
            for (int i = 0; i < numNodes; ++i) {
                cout << "Node " << i << ": ";
                for (int neighbor : adjList[i]) {
                    cout << neighbor << " ";
                }
                cout << endl;
            }
        }
    }
};

namespace GraphUtils {
    static void initRandom() {
        srand(time(0));
    }

    // Add edges to the graph
    static void AddEdges(Graph& graph, int nrEdges) {
        int numNodes = graph.numNodes;
        for (int i = 0; i < nrEdges; ++i) {
            int from = rand() % numNodes;
            int to = rand() % numNodes;
            while (from == to) {
                to = rand() % numNodes;
            }
            graph.addEdge(from, to);
        }
    }
};

bool isSafe(int node, int color, const vector<int>& colors, const Graph& graph) {
    for (int neighbor : graph.adjList[node]) {
        if (colors[neighbor] == color) {
            return false;
        }
    }
    return true;
}

void parallelGraphColoring(MPI_Comm comm, Graph& graph, int numColors, vector<int>& globalColors) {
    int rank, size;
    MPI_Comm_rank(comm, &rank);
    MPI_Comm_size(comm, &size);

    int nodesPerProcess = graph.numNodes / size;
    int leftoverNodes = graph.numNodes % size;

    // Calculate the starting and ending nodes for each process
    int startNode = rank * nodesPerProcess + min(rank, leftoverNodes);
    int endNode = (rank + 1) * nodesPerProcess + min(rank + 1, leftoverNodes);

    vector<int> localColors(graph.numNodes, 0);
    bool coloringSucceeded = false;
    while (!coloringSucceeded) {
        for (int node = startNode; node < endNode; ++node) {
            if (localColors[node] == 0) { 
                for (int color = 1; color <= numColors; ++color) {
                    if (isSafe(node, color, localColors, graph)) {
                        localColors[node] = color;
                        break;
                    }
                }
            }
        }

        MPI_Gather(localColors.data() + startNode, nodesPerProcess + (rank < leftoverNodes ? 1 : 0), MPI_INT,
            globalColors.data(), nodesPerProcess + (rank < leftoverNodes ? 1 : 0), MPI_INT, 0, comm);
        if (rank == 0) {
            coloringSucceeded = all_of(globalColors.begin(), globalColors.end(), [](int color) { return color != 0; });
        }
        MPI_Bcast(&coloringSucceeded, 1, MPI_C_BOOL, 0, comm);
        MPI_Barrier(comm);
    }
}

int main(int argc, char** argv) {
    MPI_Init(&argc, &argv);

    int numNodes = 1000;
    int numEdges = 1000000;
    Graph graph(numNodes);
    GraphUtils::AddEdges(graph, numEdges);
    int numColors = 1000;

    int rank;
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);

    //graph.printGraph(rank);
    vector<int> globalColors(numNodes, 0);
    double startTime = MPI_Wtime();
    parallelGraphColoring(MPI_COMM_WORLD, graph, numColors, globalColors);
    double endTime = MPI_Wtime();

    if (rank == 0) {
        cout << "----- Final Coloring MPI-----" << endl;
        /*
        *         for (int i = 0; i < graph.numNodes; ++i) {
            cout << "Node " << i << ": Color " << globalColors[i] << endl;
        }
        */
        double elapsedTimeMs = (endTime - startTime) * 1000;
        cout << "Elapsed Time: " << elapsedTimeMs << " ms" << endl;
    }

    MPI_Finalize();
    return 0;
}
