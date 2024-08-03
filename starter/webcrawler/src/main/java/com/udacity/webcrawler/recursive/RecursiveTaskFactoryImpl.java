package com.udacity.webcrawler.recursive;

import com.google.inject.Inject;
import com.udacity.webcrawler.IgnoredUrls;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RecursiveAction;
import java.util.regex.Pattern;

public class RecursiveTaskFactoryImpl implements RecursiveTaskFactory {
    private final Clock clock;
    private final PageParserFactory parserFactory;
    private final List<Pattern> ignoredUrls;
    public RecursiveTaskFactoryImpl(Clock clock,
                                    PageParserFactory parserFactory,
                                    List<Pattern> ignoredUrls){
        this.clock = clock;
        this.parserFactory = parserFactory;
        this.ignoredUrls = ignoredUrls;
    }

    @Override
    public RecursiveAction getTask(int maxDepth, Instant deadline, String url, Set<String> visitedUrls, Map<String, Integer> counts) {
        return new CrawlTask(clock, parserFactory, maxDepth, ignoredUrls, deadline, url, visitedUrls, counts, this);
    }
}
