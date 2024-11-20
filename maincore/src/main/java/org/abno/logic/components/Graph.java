package org.abno.logic.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Graph {
    ArrayList<Component> nodes;


    public void addNode(Component component) {
        if (!nodes.contains(component)) {
            nodes.add(component);
        }
    }

    public void removeNode(Component component) {
        nodes.remove(component);
    }

    public List<List<Component>> findSubgraphs() {
        List<List<Component>> subgraphs = new ArrayList<>();
        Set<Component> visited = new HashSet<>();

        for (Component node : nodes) {
            if (!visited.contains(node)) {
                List<Component> subgraph = new ArrayList<>();
                dfs(node, visited, subgraph);
                subgraphs.add(subgraph);
            }
        }
        return subgraphs;
    }

    private void dfs(Component node, Set<Component> visited, List<Component> subgraph) {
        visited.add(node);
        subgraph.add(node);

        for (Component neighbor : node.getConnections()) {
            if (!visited.contains(neighbor)) {
                dfs(neighbor, visited, subgraph);
            }
        }
    }


}
