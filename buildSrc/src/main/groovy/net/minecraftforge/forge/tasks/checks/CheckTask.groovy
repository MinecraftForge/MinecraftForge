package net.minecraftforge.forge.tasks.checks

import groovy.transform.CompileStatic
import net.minecraftforge.forge.tasks.Util
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.VerificationTask

@CompileStatic
abstract class CheckTask extends DefaultTask implements VerificationTask {
    @Input
    abstract Property<CheckMode> getMode()

    private boolean ignoreFailures = false

    @Input
    @Override
    boolean getIgnoreFailures() {
        return ignoreFailures
    }

    @Override
    void setIgnoreFailures(boolean ignoreFailures) {
        this.ignoreFailures = ignoreFailures
    }

    @TaskAction
    void run() {
        Util.init()

        final doFix = getMode().get() === CheckMode.FIX
        final Reporter reporter = new Reporter(doFix)
        check(reporter, doFix)

        if (reporter.messages) {
            if (getMode().get() === CheckMode.CHECK) {
                logger.error("Check task '{}' found errors:\n{}", name, reporter.messages.join('\n'))
                if (!ignoreFailures) {
                    throw new IllegalArgumentException("${reporter.messages.size()} errors were found!")
                }
            } else {
                if (logger.isEnabled(LogLevel.DEBUG)) {
                    logger.warn("Check task '{}' found {} errors and fixed {}:\n{}", name, reporter.messages.size(), reporter.fixed.size(), reporter.fixed.join('\n'))
                } else {
                    logger.warn("Check task '{}' found {} errors and fixed {}.", name, reporter.messages.size(), reporter.fixed.size())
                }

                if (reporter.notFixed) {
                    logger.error('{} errors could not be fixed:\n{}', reporter.notFixed.size(), reporter.notFixed.join('\n'))
                    if (!ignoreFailures) {
                        throw new IllegalArgumentException("${reporter.notFixed.size()} errors which cannot be fixed were found!")
                    }
                }
            }
        }
    }

    abstract void check(Reporter reporter, boolean fix)

    @CompileStatic
    static final class Reporter {
        final boolean trackFixed
        Reporter(boolean trackFixed) {
            this.trackFixed = trackFixed
        }

        public final List<String> messages = []

        public final List<String> fixed = []
        public final List<String> notFixed = []

        void report(String message, boolean canBeFixed = true) {
            messages.add(message)

            if (trackFixed) {
                if (canBeFixed) {
                    fixed.add(message)
                } else {
                    notFixed.add(message)
                }
            }
        }
    }

    static <T extends CheckTask> void registerTask(TaskContainer tasks, String taskName, @DelegatesTo.Target('type') Class<T> clazz,
                                                   @DelegatesTo(genericTypeIndex = 0, target = 'type') Closure configuration) {
        taskName = taskName.capitalize()
        tasks.register("check$taskName", clazz) { CheckTask task ->
            def castedTask = task as T
            configuration.setDelegate(castedTask)
            configuration.call(castedTask)
            castedTask.mode.set(CheckMode.CHECK)
            castedTask.group = 'checks'
        }
        tasks.named('checkAll').configure { it.dependsOn("check$taskName") }

        tasks.register("checkAndFix$taskName", clazz) { CheckTask task ->
            def castedTask = task as T
            configuration.setDelegate(castedTask)
            configuration.call(castedTask)
            castedTask.mode.set(CheckMode.FIX)
            castedTask.group = 'checks'
        }
        tasks.named('checkAllAndFix').configure { it.dependsOn("checkAndFix$taskName") }
    }
}
