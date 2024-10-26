package k;

import java.io.*;
import java.util.*;

public class CoOccurrence {

    private Map<Integer, Integer> heroCount = new HashMap<>();
    private List<Map.Entry<Integer, Integer>> sortedHeroes = new ArrayList<>();

    public static void main(String[] args) {
        CoOccurrence coOccurrence = new CoOccurrence();

        coOccurrence.processFile("hero-network.csv", "hero-network-nodup.csv");

        coOccurrence.countAndSort();
        coOccurrence.saveSortedResults("sorted_heroes.csv");
        coOccurrence.getRank(1);
        coOccurrence.getRank(18);
        coOccurrence.getRank(20);
        coOccurrence.getRank(100);
    }

    public void processFile(String filename, String fileNameToSave) {
        Set<Pair<Integer, Integer>> uniquePairs = new HashSet<>();
//        System.out.println("runnning");

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
//            total count = 574467 , id1 == id2 is 2232 total unique id = 572235
            while ((line = br.readLine()) != null) {
                String[] ids = line.split(",");
                int id1 = Integer.parseInt(ids[0]);
                int id2 = Integer.parseInt(ids[1]);
//              
//                if (id1 != id2) {
//                    Pair<Integer, Integer> pair = new Pair<>(Math.min(id1, id2), Math.max(id1, id2));
//                    uniquePairs.add(pair);
//                }
//                if(id1 == id2 ){
//                    Pair<Integer, Integer> pair = new Pair<>(Math.min(id1, id2), Math.max(id1, id2));
//                    uniquePairs.add(pair);
//                    System.out.println(id1 + " " + id2);
//                }
                Pair<Integer, Integer> pair = new Pair<>(Math.min(id1, id2), Math.max(id1, id2));
                    uniquePairs.add(pair);
            }

            for (Pair<Integer, Integer> pair : uniquePairs) {
                this.heroCount.put(pair.getKey(), this.heroCount.getOrDefault(pair.getKey(), 0) + 1);
                this.heroCount.put(pair.getValue(), this.heroCount.getOrDefault(pair.getValue(), 0) + 1);

            }

        } catch (IOException e) {

        }

        System.out.println(heroCount.size() + " herosize");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileNameToSave))) {
            for (Pair<Integer, Integer> pair : uniquePairs) {
                bw.write(pair.getKey() + "," + pair.getValue());
                bw.newLine();
            }
            System.out.println("Unique pairs saved to " + fileNameToSave);
        } catch (IOException e) {
            System.out.println("error occured: file cannot be written");
        }
        System.out.println(uniquePairs.size());
    }

    public void countAndSort() {
        sortedHeroes.addAll(heroCount.entrySet());
        sortedHeroes.sort((e1, e2) -> {
            int cmp = Integer.compare(e2.getValue(), e1.getValue());
            return cmp != 0 ? cmp : Integer.compare(e1.getKey(), e2.getKey());
        });

    }

    public void saveSortedResults(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Map.Entry<Integer, Integer> entry : sortedHeroes) {
                bw.write(entry.getKey() + "," + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {

        }
    }

//    public void getRank(int n) {
//        if (n - 1 < sortedHeroes.size()) {
//            Map.Entry<Integer, Integer> entry = sortedHeroes.get(n - 1);
//            List<Integer> heroIds = new ArrayList<>();
//            int frequency = entry.getValue();
//            for (Map.Entry<Integer, Integer> e : sortedHeroes) {
//                if (e.getValue() == frequency) {
//                    heroIds.add(e.getKey());
//                }
//            }
//            System.out.printf("Rank %d: %d hero(es) with %d co-occurrence(s).\n", n, heroIds.size(), frequency);
//            System.out.println("Hero(es) include: " + heroIds);
//        } else {
//            System.out.println("Rank not available.");
//        }
//    }
    public void getRank(int n) {
    if (n <= 0 || n > sortedHeroes.size()) {
        System.out.println("Rank not available.");
        return;
    }
    
    int frequency = sortedHeroes.get(0).getValue();
    int count = 1;
    List<Integer> heroIds = new ArrayList<>();

    // Collect all hero IDs with the same frequency as the nth rank
    for (Map.Entry<Integer, Integer> e : sortedHeroes) {
//        System.out.println(frequency + " " + count);
        if( frequency == e.getValue()){
//            count = count + 1;
            
        }else {
            frequency = e.getValue();
            count = count + 1;
        }
        if(count == n ){
            heroIds.add(e.getKey());
        }
        if(count > n ){
            break;
        }
        
        
        
        
    }

    System.out.printf("Rank %d: %d hero(es) with %d co-occurrence(s).\n", n, heroIds.size(), frequency);
    System.out.println("Hero(es) include: " + heroIds);
}

}

class Pair<K, V> {

    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return (Objects.equals(key, pair.key) && Objects.equals(value, pair.value))
                || (Objects.equals(key, pair.value) && Objects.equals(value, pair.key));
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

}
