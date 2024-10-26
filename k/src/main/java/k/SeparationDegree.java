package k;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SeparationDegree {
    // Use a Map to represent the graph
    private final Map<Integer, List<Integer>> graph = new HashMap<>();
    private final String csvFile = "hero-network-nodup.csv";

    public static void main(String[] args) {
        SeparationDegree separation = new SeparationDegree();
        separation.constructGraph(separation.csvFile);

        separation.printDegreeOfSeparation(5, 102);
        separation.printDegreeOfSeparation(1, 0);
        separation.printDegreeOfSeparation(4, 53);
        separation.printDegreeOfSeparation(4, 533);
        separation.printDegreeOfSeparation(4, 5553);

        separation.printHeroesOfDegree(4, 2);
        separation.printHeroesOfDegree(400, 3);

        separation.getHeroesOfDegree(1, 4);
        separation.getHeroesOfDegree(5, 4);
        separation.getHeroesOfDegree(100, 5);
    }

    // Read the CSV file and construct a graph
    public void constructGraph(String csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] ids = line.split(",");
                int id1 = Integer.parseInt(ids[0]);
                int id2 = Integer.parseInt(ids[1]);

                graph.computeIfAbsent(id1, k -> new ArrayList<>()).add(id2);
                graph.computeIfAbsent(id2, k -> new ArrayList<>()).add(id1); // Undirected graph
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    // Print the Degree of Separation between `id1` and `id2`.
    public void printDegreeOfSeparation(int id1, int id2) {
        if (id1 == id2) {
            System.out.printf("The degree of separation between #%d and #%d is: 0\n", id1, id2);
            return;
        }

        int degree = bfs(id1, id2, true);
        System.out.printf("The degree of separation between #%d and #%d is: %d\n", id1, id2, degree);
    }

    // Generalized BFS method
    private int bfs(int startId, int targetId, boolean findDegree) {
        Queue<int[]> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();
        queue.offer(new int[]{startId, 0}); // {currentId, currentDegree}
        visited.add(startId);

        int count = 0;

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentId = current[0];
            int currentDegree = current[1];

            if (findDegree && currentId == targetId) {
                return currentDegree;
            }

            if (!findDegree && currentDegree == targetId) {
                count++;
            }

            if (!findDegree && currentDegree < targetId) {
                for (int neighbor : graph.getOrDefault(currentId, Collections.emptyList())) {
                    if (visited.add(neighbor)) { // Add and check in one go
                        queue.offer(new int[]{neighbor, currentDegree + 1});
                    }
                }
            } else if (findDegree) {
                for (int neighbor : graph.getOrDefault(currentId, Collections.emptyList())) {
                    if (visited.add(neighbor)) {
                        queue.offer(new int[]{neighbor, currentDegree + 1});
                    }
                }
            }
        }

        return findDegree ? -1 : count; // Return -1 for unreachable in degree search
    }

    // Print the total number of heroes that are exactly `degree` separation away from `id`.
    public void printHeroesOfDegree(int id, int degree) {
        int count = bfs(id, degree, false);
        System.out.printf("The number of heroes that are %d separation away from #%d is: %d\n", degree, id, count);
    }

    public void getHeroesOfDegree(int id, int degree) {
        int count = bfs(id, degree, false);
        System.out.printf("Total number of nodes with %d degree of separation from #%d: %d\n", degree, id, count);
    }
}
