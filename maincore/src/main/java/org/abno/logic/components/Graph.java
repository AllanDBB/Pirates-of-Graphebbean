package org.abno.logic.components;

import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable {
    private List<Component> nodes;
    private Map<Component, List<Component>> adjacencyList;

    public Graph() {
        nodes = new ArrayList<>();
        adjacencyList = new HashMap<>();
    }

    public void addNode(Component node) {
        System.out.println(Arrays.toString(nodes.toArray()));
        if (!nodes.contains(node)) {
            nodes.add(node);
            System.out.println("agregue");
            adjacencyList.putIfAbsent(node, new ArrayList<>());
        }
    }

    public void addEdge(Component from, Component to) {
        // Verificar que ambos nodos estén en el grafo
        if (!nodes.contains(from) || !nodes.contains(to)) {
            throw new IllegalArgumentException("Ambos nodos deben estar en el grafo.");
        }

        // Evitar agregar conexiones duplicadas
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

    public List<Component> getNodes() {
        return nodes;
    }

    private Component getComponentById(int id){
        for (Component c : nodes){
            if (c.getId() == id){
                return c;
            }
        }
        return null;
    }

    public boolean isDisconnectedSubgraph(int id) {

        if (!containsNode(getComponentById(id))) {
            System.out.println("mierda");
            return false;
        }

        Component node = getComponentById(id);

        Set<Component> visited = new HashSet<>();
        Queue<Component> queue = new LinkedList<>();
        queue.add(node);
        visited.add(node);

        while (!queue.isEmpty()) {
            Component current = queue.poll();
            for (Component neighbor : getConnections(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        if (visited.size() < nodes.size()){
            System.out.println("yes");
        } else{
            System.out.println("no");
        }
        return visited.size() < nodes.size();
    }



}
