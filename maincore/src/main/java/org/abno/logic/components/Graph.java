package org.abno.logic.components;

import java.util.*;

public class Graph {
    private List<Component> nodes;
    private Map<Component, List<Component>> adjacencyList;

    public Graph() {
        nodes = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    public void addNode(Component node) {
        if (!nodes.contains(node)) {
            nodes.add(node);
            adjacencyList.putIfAbsent(node, new ArrayList<>());
        }
    }

    public void addEdge(Component from, Component to) {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new IllegalArgumentException("Ambos nodos deben estar en el grafo.");
        }
        if (!adjacencyList.get(from).contains(to)) {
            adjacencyList.get(from).add(to);
        }
        if (!adjacencyList.get(to).contains(from)) {
            adjacencyList.get(to).add(from);
        }
    }

    public void removeNode(Component node) {
        List<Component> connections = adjacencyList.get(node);
        if (connections != null) {
            for (Component connectedNode : connections) {
                adjacencyList.get(connectedNode).remove(node);
            }
        }
        adjacencyList.remove(node);
        nodes.remove(node);
    }

    public void removeEdge(Component from, Component to) {
        if (adjacencyList.containsKey(from)) {
            adjacencyList.get(from).remove(to);
        }
        if (adjacencyList.containsKey(to)) {
            adjacencyList.get(to).remove(from);
        }
    }

    public List<Component> getConnections(Component node) {
        return adjacencyList.getOrDefault(node, new ArrayList<>());
    }

    public boolean containsNode(Component node) {
        return nodes.contains(node);
    }

    public boolean areConnected(Component from, Component to) {
        return adjacencyList.getOrDefault(from, new ArrayList<>()).contains(to);
    }
}
