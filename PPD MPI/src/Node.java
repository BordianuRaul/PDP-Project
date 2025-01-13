import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Node implements Comparable<Node> {  // Implement Comparable

    private final Integer id;
    private final Set<Node> adjacentNodes;

    public Node(Integer id) {
        this.id = id;
        this.adjacentNodes = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public Set<Node> getAdjacentNodes() {
        return adjacentNodes;
    }

    public boolean isAdjacent(Node node) {
        return adjacentNodes.contains(node);
    }

    public void setAdjacentNode(Node node) {
        this.adjacentNodes.add(node);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return getId().equals(node.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    // Implement compareTo to compare by node ID
    @Override
    public int compareTo(Node other) {
        return this.id.compareTo(other.id);  // Sort by ID
    }
}
