package com.taskmanager.util;

import java.util.*;

public class CycleDetector {

    public static boolean hasCycle(Map<String, List<String>> graph) {
        Set<String> visited = new HashSet<>();
        Set<String> recursionStack = new HashSet<>();

        for (String node : graph.keySet()) {
            if (dfs(node, graph, visited, recursionStack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean dfs(String node,
                               Map<String, List<String>> graph,
                               Set<String> visited,
                               Set<String> stack) {

        if (stack.contains(node)) return true;
        if (visited.contains(node)) return false;

        visited.add(node);
        stack.add(node);

        for (String neighbor : graph.getOrDefault(node, List.of())) {
            if (dfs(neighbor, graph, visited, stack)) {
                return true;
            }
        }

        stack.remove(node);
        return false;
    }
}
