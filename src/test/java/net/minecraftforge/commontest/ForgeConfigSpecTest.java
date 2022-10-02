/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.commontest;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ForgeConfigSpecTest
{
    private final String TEST_CONFIG_PATH_TEMPLATE = "./tests/config/%s.toml";
    private final int TEST_SIZE = 10000;
    private static final Field useCachesField;

    static {
        try {
            useCachesField = ForgeConfigSpec.ConfigValue.class.getDeclaredField("USE_CACHES");
            useCachesField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setUseCachesField(boolean value) {
        try {
            useCachesField.setBoolean(null, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void simpleSpeedTest() throws IOException
    {
        executeSpeedTest("test", "someDefaultValue", "simpleSpeedTest");
    }

    @Test
    public void objectSpeedTest() throws IOException
    {
        executeSpeedTest("test", Lists.newArrayList(Lists.newArrayList("someValue")), "objectSpeedTest");
    }

    @Test
    public void deepKeySpeedTest() throws IOException
    {
        executeSpeedTest("test.test.test.test.test.test.test.test.test.test", "deepKeyValue", "deepKeySpeedTest");
    }

    @Test(expected = IllegalStateException.class)
    public void allEmptyCommentTest() throws IllegalStateException
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.ConfigValue<String> simpleValue = builder.comment("", "").define("allEmptyCommentTest", "someDefaultValue");
        final ForgeConfigSpec spec = builder.build();
    }

    @Test
    public void topPaddedCommentTest()
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.ConfigValue<String> simpleValue = builder.comment("", "Test").define("topPaddedCommentTest", "someDefaultValue");
        final ForgeConfigSpec spec = builder.build();
    }

    private <T> void executeSpeedTest(final String configKey, final T defaultKeyValue, final String testName) throws IOException
    {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        final ForgeConfigSpec.ConfigValue<T> simpleValue = builder.define(configKey, defaultKeyValue);
        final ForgeConfigSpec spec = builder.build();

        final String configPath = String.format(Locale.ROOT, TEST_CONFIG_PATH_TEMPLATE, testName);
        final File configFile = new File(configPath);
        configFile.getParentFile().mkdirs();
        configFile.createNewFile();
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                                                 .sync()
                                                 .preserveInsertionOrder()
                                                 .onFileNotFound((newfile, configFormat) -> {
                                                     Files.createFile(newfile);
                                                     return true;
                                                 })
                                                 .writingMode(WritingMode.REPLACE)
                                                 .build();
        spec.setConfig(configData);

        final List<TestResult> results = runTestHarness(defaultKeyValue, testName, simpleValue, spec, 3, 20);
        final double averageEnabled = results.stream().mapToLong(value -> value.enabledWatch.elapsed(TimeUnit.NANOSECONDS)).average().getAsDouble();
        final double averageDisabled = results.stream().mapToLong(value -> value.disabledWatch.elapsed(TimeUnit.NANOSECONDS)).average().getAsDouble();
        final double maxEnabledDeviation = results.stream().mapToLong(value -> value.enabledWatch.elapsed(TimeUnit.NANOSECONDS))
          .mapToDouble(e -> e - averageEnabled)
          .map(Math::abs)
          .max()
          .getAsDouble();
        final double maxDisabledDeviation = results.stream().mapToLong(value -> value.disabledWatch.elapsed(TimeUnit.NANOSECONDS))
                                             .mapToDouble(e -> e - averageDisabled)
                                             .map(Math::abs)
                                             .max()
                                             .getAsDouble();

        System.out.printf("Computed test results for: %s:%n", testName);
        System.out.printf("  > Enabled:  ~%10.2f ns.   +- %10.2f ns.%n", averageEnabled, maxEnabledDeviation);
        System.out.printf("  > Disabled: ~%10.2f ns.   +- %10.2f ns.%n", averageDisabled, maxDisabledDeviation);

        configFile.delete();
    }

    private <T> List<TestResult> runTestHarness(final T defaultKeyValue, final String testName, final ForgeConfigSpec.ConfigValue<T> simpleValue, final ForgeConfigSpec spec, final int warmupRounds, final int testRounds)
    {
        final List<TestResult> results = new ArrayList<>();

        for (int i = 0; i < warmupRounds; i++)
        {
            //Run a bunch of warm up rounds and ignore the results.
            runTestOnce(defaultKeyValue, testName, simpleValue, spec);
        }

        for (int i = 0; i < testRounds; i++)
        {
            results.add(runTestOnce(defaultKeyValue, testName, simpleValue, spec));
        }

        return results;
    }

    private <T> TestResult runTestOnce(final T defaultKeyValue, final String testName, final ForgeConfigSpec.ConfigValue<T> simpleValue, final ForgeConfigSpec spec)
    {
        spec.afterReload();
        setUseCachesField(true);
        final Stopwatch watchDisabled = Stopwatch.createStarted();
        for (int i = 0; i < TEST_SIZE; i++)
        {
            Assert.assertEquals(defaultKeyValue, simpleValue.get());
        }
        watchDisabled.stop();

        spec.afterReload();
        setUseCachesField(false);
        final Stopwatch watchEnabled = Stopwatch.createStarted();
        for (int i = 0; i < TEST_SIZE; i++)
        {
            Assert.assertEquals(defaultKeyValue, simpleValue.get());
        }
        watchEnabled.stop();

        final TestResult result = new TestResult(watchEnabled, watchDisabled);

        System.out.printf("Test result for: %s (Disabled: %s vs. Enabled: %s)%n", testName, watchDisabled.toString(), watchEnabled.toString());
        return result;
    }

    private final static class TestResult {

        private final Stopwatch enabledWatch;
        private final Stopwatch disabledWatch;

        public TestResult(final Stopwatch enabledWatch, final Stopwatch disabledWatch)
        {
            this.enabledWatch = enabledWatch;
            this.disabledWatch = disabledWatch;
        }
    }
}
