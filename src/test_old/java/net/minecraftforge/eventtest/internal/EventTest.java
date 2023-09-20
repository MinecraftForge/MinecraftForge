/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.eventtest.internal;

/**
 * The basis of the Event regression testing framework.
 * Allows each bootstrap event to be queried in turn to see whether the event passed.
 * For gameplay events, a callback is set for when the respective event is fired.
 *
 * Test cases should be in a subclass of this, annotated with TestHolder.
 * If the test is for an event that fires in Bootstrap, set the field in constructor.
 * Register the event handler in the registerEvents method, which will be fired automatically.
 *
 * Use the pass / fail / error methods in this class to tell the Framework what the result of the test is.
 *
 * @author Curle
 */
public abstract class EventTest {
    protected Result testResult = Result.NOT_PROCESSED;
    protected String errorDetail = "Event not processed";
    protected boolean bootstrap;  // true for events that fire when the game is loading to the main menu. false for events that fire after the game has loaded.

    /**
     * A summarised view of the test result.
     * For passes: "PASS"
     * For failures: "FAIL - ($errorDetail)"
     * @return test result
     */
    public String getTestResult() {
        return (testResult.passed() ? "PASS" : "FAIL; ") + (testResult.failed() ? getEnhancedDetail() : "");
    }

    /**
     * Provide extra detail about the error.
     * Pass-through of errorDetail is default, but some events require something more robust.
     * @return information to be shown in case of a test failure
     */
    protected String getEnhancedDetail() { return errorDetail; }

    /**
     * @return whether or not this event should be monitored while the game is loading to the main menu.
     */
    public boolean isBootstrap() { return bootstrap; }

    /**
     * Register event handlers for this event.
     * Trade-off between "magic annotations" and verbosity should be evaluated.
     */
    public abstract void registerEvents();

    /**
     * Helper method - combines setting failure state with detail message.
     * @param reason extra detail about why the failure occurred
     */
    protected void fail(String reason) {
        testResult = Result.FAIL;
        errorDetail = reason;

        TestFramework.testChangedState(this);
    }

    /**
     * Helper method - sets the test pass flag.
     */
    protected void pass() {
        testResult = Result.PASS;

        TestFramework.testChangedState(this);
    }

    /**
     * Helper method - combines setting error state with detail message.
     * @param reason extra detail about why the error occured.
     */
    protected void error(String reason) {
        testResult = Result.TESTFAIL;
        errorDetail = reason;

        TestFramework.testChangedState(this);
    }

    /**
     * Encodes information about how the test progressed.
     */
    public enum Result {
        PASS,           // The test passed with no errors
        FAIL,           // The code being tested failed to meet the requirements
        TESTFAIL,       // The test threw an error when trying to evaluate the requirements
        NOT_PROCESSED;  // The test was not fired, or did not return a result.

        /**
         * @return whether the test passed.
         */
        public boolean passed() {
            return this == PASS;
        }

        /**
         * @return whether the test or the code being tested failed.
         */
        public boolean failed() {
            return this != PASS;
        }

        /**
         * @return whether the test encountered an error
         */
        public boolean errored() {
            return this == TESTFAIL;
        }
    };

}