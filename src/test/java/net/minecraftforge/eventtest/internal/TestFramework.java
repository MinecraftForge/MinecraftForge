/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventtest.internal;

import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.GameShuttingDownEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.LoadingModList;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingRandomAccessFileAppender;
import org.apache.logging.log4j.core.appender.rolling.OnStartupTriggeringPolicy;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The backend of the "curletest" event testing framework.
 * All tests are registered to this class, and are set up according to the needs they declare.
 *
 * All bootstrap tests will be summed up into a log file when the main menu loads.
 * All gameplay events will be printed to the console when they fire, and written to the above log.
 *
 * To register a new event test, simply create the class in the events package and add it to the list here.
 */
@Mod(TestFramework.MODID)
public class TestFramework {
    public static final String MODID = "eventtests";

    final List<EventTest> tests;
    final static Map<EventTest, String> testNames = new HashMap<>();
    final static Logger LOGGER = LogManager.getLogger();
    final static Type CURLETEST_ANNOTATION = Type.getType(TestHolder.class);

    boolean bootstrapHandled = false;

    /**
     * Pre-initialize the framework and all events.
     */
    public TestFramework() {
        prepareLogger();

        LOGGER.info("Preparing all event tests.");

        tests = gatherEvents();
        if (FMLLoader.getDist().isDedicatedServer()) return; // This entire thing doesn't work on servers...

        // Let each event set up
        tests.forEach(EventTest::registerEvents);

        // Register the screen opened listener.
        // The main menu signals the end of the bootstrap tests.
        MinecraftForge.EVENT_BUS.addListener(this::collectBootstrapTests);

        // Register the game shutting down listener.
        // If there are any unhandled tests after shutdown, notify of an event not fired.
        MinecraftForge.EVENT_BUS.addListener(this::collectMissedTests);
    }

    /**
     * Gameplay events require to be able to notify the framework when their task is completed.
     * Bootstrap events are ignored when updates occur.
     */
    public static void testChangedState(EventTest test) {
        if(test.isBootstrap()) return;

        LOGGER.info("Test " + testNames.get(test) + ": " + test.getTestResult());
    }

    /**
     * Enumerate all tests that should be ran with the framework.
     * All classes with the @TestHolder annotation are resolved and instantiated as tests.
     *
     * @return all tests to run
     */
    private List<EventTest> gatherEvents() {
        var tests = new ArrayList<EventTest>();

        // Search all annotations in the current mod, filter for @CurleTest, and condense to a list.
        var anns = LoadingModList.get().getModFileById(MODID).getFile().getScanResult().getAnnotations()
                .stream()
                .filter(ann -> CURLETEST_ANNOTATION.equals(ann.annotationType()))
                .toList();

        // For each instance of the annotation, resolve the class it's attached to, create a new instance, and add it to the list.
        anns.forEach(annotationData -> {
            Class<?> clazz;
            try {
                clazz = Class.forName(annotationData.clazz().getClassName());
                EventTest instance = (EventTest) clazz.getDeclaredConstructor().newInstance();
                tests.add(instance);
                testNames.put(instance, (String) annotationData.annotationData().get("value"));
            } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        });

        return tests;
    }

    /**
     * At the mark of the end of the bootstrap period, collect and log all test results.
     * the ScreenOpenEvent fired by TitleScreen is the last event fired without user interaction, so it is the
     *  end of the "automatic" bootstrap time.
     */
    private void collectBootstrapTests(ScreenEvent.Opening event) {
        if(!(event.getScreen() instanceof TitleScreen) || bootstrapHandled)
            return;

        LOGGER.info("Preparing to run bootstrap tests:");
        tests.forEach(test -> {
            if(test.isBootstrap()) {
                LOGGER.info("  - " + testNames.get(test) + ": " + test.getTestResult());
            }
        });
        LOGGER.info("All bootstrap tests evaluated.");

        bootstrapHandled = true;
    }

    /**
     * Search for unfired events when the game is shutting down.
     * Summarise all test results.
     */
    private void collectMissedTests(GameShuttingDownEvent event) {
        LOGGER.info("Test summary processing..");
        tests.forEach(test -> {
            LOGGER.info("\tTest " + testNames.get(test) + ": ");
            LOGGER.info(test.testResult == EventTest.Result.NOT_PROCESSED ?
                    "\t\tMISSED - Not fired." :
                    "\t\t" + test.getTestResult()
            );
        });
        LOGGER.info("Event Test Framework finished.");
    }

    /**
     * Set the LOGGER instance in this class to only write to logs/curletest.log.
     * Log4J is annoying and requires manual initialization and preparation.
     */
    private void prepareLogger() {
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        final LoggerConfig loggerConfig = getLoggerConfiguration(config, LOGGER.getName());

        final RollingRandomAccessFileAppender appender = RollingRandomAccessFileAppender.newBuilder()
                .setName("EventTestLog")
                .withFileName("logs/curletest.log")
                .withFilePattern("logs/%d{yyyy-MM-dd}-%i.log.gz")
                .setLayout(PatternLayout.newBuilder()
                        .withPattern("[%d{ddMMMyyyy HH:mm:ss}] [%logger]: %minecraftFormatting{%msg}{strip}%n%xEx")
                        .build()
                )
                .withPolicy(
                        OnStartupTriggeringPolicy.createPolicy(1)
                )
                .build();

        appender.start();

        loggerConfig.setParent(null);
        loggerConfig.getAppenders().keySet().forEach(loggerConfig::removeAppender);
        loggerConfig.addAppender(
                appender,
                Level.INFO,
                null
        );
    }

    private static LoggerConfig getLoggerConfiguration(@NotNull final Configuration configuration, @NotNull final String loggerName)
    {
        final LoggerConfig lc = configuration.getLoggerConfig(loggerName);
        if (lc.getName().equals(loggerName))
        {
            return lc;
        }
        else
        {
            final LoggerConfig nlc = new LoggerConfig(loggerName, lc.getLevel(), lc.isAdditive());
            nlc.setParent(lc);
            configuration.addLogger(loggerName, nlc);
            configuration.getLoggerContext().updateLoggers();

            return nlc;
        }
    }
}