package com.udacity.webcrawler.recursive;

import com.google.inject.*;
import com.udacity.webcrawler.IgnoredUrls;
import com.udacity.webcrawler.parser.PageParserFactory;

import java.time.Clock;
import java.util.List;
import java.util.regex.Pattern;

public class RecursiveModule extends AbstractModule {
    @Provides
    @Singleton
    @Inject
    RecursiveTaskFactory provideRecursiveTaskFactory(Clock clock, PageParserFactory parserFactory,@IgnoredUrls List<Pattern> ignoredUrls) {
        return new RecursiveTaskFactoryImpl(clock, parserFactory, ignoredUrls);
    }
}
