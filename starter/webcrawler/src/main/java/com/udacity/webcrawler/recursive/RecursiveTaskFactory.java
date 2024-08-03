package com.udacity.webcrawler.recursive;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

public interface RecursiveTaskFactory {

    RecursiveAction getTask(int maxDepth, Instant deadline, String url, Set<String> visitedUrls, Map<String, Integer> counts);
}
