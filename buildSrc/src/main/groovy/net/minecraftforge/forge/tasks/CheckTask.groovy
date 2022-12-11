package net.minecraftforge.forge.tasks

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.DefaultTask
import org.gradle.api.logging.LogLevel
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskContainer

@CompileStatic
abstract class CheckTask extends DefaultTask {
    @Input
    abstract Property<CheckType> getType()

    @TaskAction
    void run() {
        Util.init()

        final doFix = getType().get() === CheckType.FIX
        final Reporter reporter = new Reporter(doFix)
        check(reporter, doFix)
        
        if (reporter.messages) {
            if (getType().get() === CheckType.CHECK) {
                logger.error("Check task '{}' found errors:\n{}", name, reporter.messages.join('\n'))
                throw new IllegalArgumentException("${reporter.messages.size()} errors were found!")
            } else {
                if (logger.isEnabled(LogLevel.DEBUG)) {
                    logger.error("Check task '{}' found {} errors and fixed {}:\n{}", name, reporter.messages.size(), reporter.fixed.size(), reporter.fixed.join('\n'))
                } else {
                    logger.error("Check task '{}' found {} errors and fixed {}.", name, reporter.messages.size(), reporter.fixed.size())
                }

                if (reporter.notFixed) {
                    logger.error('{} errors could not be fixed:\n{}', reporter.notFixed.size(), reporter.notFixed.join('\n'))
                    throw new IllegalArgumentException("${reporter.notFixed.size()} errors which cannot be fixed were found!")
                }
            }
        }
    }

    abstract void check(Reporter reporter, boolean fix)

    @CompileStatic
    protected static final class Reporter {
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

    static <T extends CheckTask> void registerTask(TaskContainer tasks, String taskName, Class<T> clazz,
                                                   Action<T> configuration) {
        taskName = taskName.capitalize()
        tasks.register("check$taskName", clazz) { CheckTask task ->
            configuration.execute((T)task)
            task.type.set(CheckType.CHECK)
        }
        tasks.register("checkAndFix$taskName", clazz) { CheckTask task ->
            configuration.execute((T)task)
            task.type.set(CheckType.FIX)
        }
    }
}
