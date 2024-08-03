package com.udacity.webcrawler;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.LinkedList;
import java.util.stream.Collectors;
/**
 * Utility class that sorts the map of word counts.
 *
 * <p>TODO: Reimplement the sort() method using only the Stream API and lambdas and/or method
 *          references.
 */
final class WordCounts {

  /**
   * Given an unsorted map of word counts, returns a new map whose word counts are sorted according
   * to the provided {@link WordCountComparator}, and includes only the top
   * {@param popluarWordCount} words and counts.
   *
   * <p>TODO: Reimplement this method using only the Stream API and lambdas and/or method
   *          references.
   *
   * @param wordCounts       the unsorted map of word counts.
   * @param popularWordCount the number of popular words to include in the result map.
   * @return a map containing the top {@param popularWordCount} words and counts in the right order.
   */
 static Map<String, Integer> sort(Map<String, Integer> wordCounts, int popularWordCount) {

     Queue<Map.Entry<String, Integer>> sortedCounts =
             new LinkedList<>(wordCounts.entrySet());

     // Sort the list explicitly
     sortedCounts = sortedCounts.stream()
             .sorted(new WordCountComparator())
             .collect(Collectors.toCollection(LinkedList::new));

     return sortedCounts.stream()
             .limit(popularWordCount)
             .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
  }

  /**
   * A {@link Comparator} that sorts word count pairs correctly:
   *
   * <p>
   * <ol>
   *   <li>First sorting by word count, ranking more frequent words higher.</li>
   *   <li>Then sorting by word length, ranking longer words higher.</li>
   *   <li>Finally, breaking ties using alphabetical order.</li>
   * </ol>
   */
  private static final class WordCountComparator implements Comparator<Map.Entry<String, Integer>> {
    public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
      Integer aValue = a.getValue();
      Integer bValue = b.getValue();
      if (aValue.equals(bValue)) {
        String aKey = a.getKey();
        String bKey = b.getKey();
        int lengthComparison = bKey.length() - aKey.length();
        if (lengthComparison == 0) {
          return aKey.compareTo(bKey);
        } else {
          return lengthComparison;
        }
      } else {
        return bValue.intValue() - aValue.intValue();
      }
    }
  }

  private WordCounts() {
    // This class cannot be instantiated
  }
}