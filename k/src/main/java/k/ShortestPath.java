package k;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class ShortestPath {
    // Use a 2D array to represent the weighted graph
    int[][] weightedGraph = new int[6500][6500]; // Adjust size based on the max number of heroes
    int[] degrees = new int[176100]; // To store the degree of each node
    String csvFile = "hero-network-nodup.csv";

    public static void main(String[] args) {
        // Check the shortest path between two given ids
        ShortestPath shortestPath = new ShortestPath();

        shortestPath.constructGraph(shortestPath.csvFile);

//        shortestPath.getShortestPath(5, 102);
//        shortestPath.getShortestPath(1, 0);
        shortestPath.getShortestPath(4, 53);
        shortestPath.getShortestPath(4, 573);
        shortestPath.getShortestPath(4, 5553);
    }

    // Read the csv file and construct a weighted graph for it
    public void constructGraph(String csvFile) {
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] ids = line.split(",");
                int id1 = Integer.parseInt(ids[0]);
                int id2 = Integer.parseInt(ids[1]);

                // Mark the connection in the graph
                weightedGraph[id1][id2] = Math.min(weightedGraph[id1][id2] == 0 ? Integer.MAX_VALUE : weightedGraph[id1][id2], Math.min(degrees[id1], degrees[id2]));
                weightedGraph[id2][id1] = Math.min(weightedGraph[id2][id1] == 0 ? Integer.MAX_VALUE : weightedGraph[id2][id1], Math.min(degrees[id1], degrees[id2]));
                
                degrees[id1]++;
                degrees[id2]++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Print the shortest path between `id1` and `id2`
    public void getShortestPath(int id1, int id2) {
        if (id1 == id2) {
            System.out.printf("Shortest Path between #%d and #%d has the total weight 0, with 0 hops.\n", id1, id2);
            System.out.println("Path: [" + id1 + "]");
            return;
        }

        // Dijkstra's algorithm
        int[] distances = new int[weightedGraph.length];
        Arrays.fill(distances, Integer.MAX_VALUE);
        distances[id1] = 0;

        boolean[] visited = new boolean[weightedGraph.length];
        int[] previous = new int[weightedGraph.length];
        Arrays.fill(previous, -1);

        PriorityQueue<int[]> queue = new PriorityQueue<>(Comparator.comparingInt(arr -> arr[1])); // {node, distance}
        queue.offer(new int[]{id1, 0});

        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int currentNode = current[0];
            int currentDistance = current[1];

            if (visited[currentNode]) continue;
            visited[currentNode] = true;

            for (int neighbor = 0; neighbor < weightedGraph.length; neighbor++) {
                if (weightedGraph[currentNode][neighbor] > 0) {
                    int newDist = currentDistance + weightedGraph[currentNode][neighbor];
                    if (newDist < distances[neighbor]) {
                        distances[neighbor] = newDist;
                        previous[neighbor] = currentNode;
                        queue.offer(new int[]{neighbor, newDist});
                    }
                }
            }
        }

        // Backtrack to find the path
        if (distances[id2] == Integer.MAX_VALUE) {
            System.out.printf("No path exists between #%d and #%d.\n", id1, id2);
        } else {
            List<Integer> path = new ArrayList<>();
            for (int at = id2; at != -1; at = previous[at]) {
                path.add(at);
            }
            Collections.reverse(path);

            System.out.printf("Shortest Path between #%d and #%d has the total weight %d, with %d hops.\n", id1, id2, distances[id2], path.size() - 1);
            System.out.println("Path: " + path);
        }
    }
}
