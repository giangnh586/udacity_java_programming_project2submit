package com.udacity.webcrawler.recursive;

import com.udacity.webcrawler.parser.PageParser;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CrawlTask extends RecursiveAction {

    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final int maxDepth;
    private final List<Pattern> ignoredUrls;
    private final Instant deadline;
    private final String url;
    private final Set<String> visitedUrls;
    private final Map<String, Integer> counts;
    private final RecursiveTaskFactory taskFactory;

    public CrawlTask(Clock clock, PageParserFactory parserFactory, int maxDepth, List<Pattern> ignoredUrls, Instant deadline, String url, Set<String> visitedUrls, Map<String, Integer> counts, RecursiveTaskFactory taskFactory) {
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.maxDepth = maxDepth;
        this.ignoredUrls = ignoredUrls;
        this.deadline = deadline;
        this.url = url;
        this.visitedUrls = visitedUrls;
        this.counts = counts;
        this.taskFactory = taskFactory;
    }

    @Override
    protected void compute() {
        if (checkValidUrl(url)) return;
        PageParser.Result result = parserFactory.get(url).parse();
        result.getWordCounts().entrySet().forEach(this::countWords);
        List<RecursiveAction> subTask = result.getLinks().stream()
                .map(link -> taskFactory.getTask(maxDepth - 1, deadline, link, visitedUrls, counts))
                .collect(Collectors.toList());
        invokeAll(subTask);
    }

    private boolean checkValidUrl(String url) {
        if (maxDepth == 0 || clock.instant().isAfter(deadline)) {
            return true;
        }
        for (Pattern pattern : ignoredUrls) {
            if (pattern.matcher(url).matches()) {
                return true;
            }
        }
        if (!visitedUrls.add(url)) {
            return true;
        }
        return false;
    }

    private void countWords(Map.Entry<String, Integer> e) {
        counts.compute(e.getKey(), (key, value) -> (value == null) ? e.getValue() : e.getValue() + value);
    }
}
