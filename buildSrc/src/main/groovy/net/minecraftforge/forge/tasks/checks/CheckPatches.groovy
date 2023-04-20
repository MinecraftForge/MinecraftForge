package net.minecraftforge.forge.tasks.checks

import groovy.transform.CompileStatic
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Optional

import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

@CompileStatic
abstract class CheckPatches extends CheckTask {

    private static final Pattern HUNK_START_PATTERN = Pattern.compile('^@@ -[0-9,]* \\+[0-9,_]* @@$')
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile('^[+\\-]\\s*$')
    private static final Pattern IMPORT_PATTERN = Pattern.compile('^[+\\-]\\s*import.*')
    private static final Pattern FIELD_PATTERN = Pattern.compile('^[+\\-][\\s]*((public|protected|private)[\\s]*)?(static[\\s]*)?(final)?([^=;]*)(=.*)?;\\s*$')
    private static final Pattern METHOD_PATTERN = Pattern.compile('^[+\\-][\\s]*((public|protected|private)[\\s]*)?(static[\\s]*)?(final)?([^(]*)[(]([^)]*)?[)]\\s*[{]\\s*$')
    private static final Pattern CLASS_PATTERN = Pattern.compile('^[+\\-][\\s]*((public|protected|private)[\\s]*)?(static[\\s]*)?(final[\\s]*)?(class|interface)([^{]*)[{]\\s*$')
    private static final Map<String, Integer> ACCESS_MAP = [private: 0, protected: 2, public: 3].tap { it.put(null, 1) }

    @InputDirectory abstract DirectoryProperty getPatchDir()
    @Input @Optional abstract ListProperty<String> getPatchesWithS2SArtifact()

    @Override
    void check(Reporter reporter, boolean fix) {
        final patchDir = getPatchDir().get().asFile.toPath()
        Files.walk(patchDir).withCloseable {
            it.filter(Files.&isRegularFile).forEach { path ->
                final String relativeName = patchDir.relativize(path).toString()
                verifyPatch(path, reporter, fix, relativeName, patchesWithS2SArtifact.get().contains(relativeName.replace('\\', '/')))
            }
        }
    }
    
    def accessChange(previous, current) {
        //return ACCESS_MAP[previous] < ACCESS_MAP[current]
        return previous != current
    }

    void verifyPatch(Path patch, Reporter reporter, boolean fix, String patchPath, boolean hasS2SArtifact) {
        final oldFixedErrors = reporter.fixed.size()

        final lines = Files.readAllLines(patch)

        int hunksStart = 0
        boolean onlyWhiteSpace = false

        final List<String> newLines = []

        // First two lines are file name ++/-- and we do not care
        newLines.add(lines[0] + '\n')
        newLines.add(lines[1] + '\n')

        int i
        for (i = 2; i < lines.size(); ++i) {
            def line = lines[i]
            newLines.add(line + '\n')

            if (HUNK_START_PATTERN.matcher(line).find()) {
                if (onlyWhiteSpace) {
                    if (!hasS2SArtifact)
                        reporter.report("Patch contains only white space hunk starting at line ${hunksStart + 1}, file: $patchPath")
                    int toRemove = i - hunksStart
                    while (toRemove-- > 0)
                        newLines.remove(newLines.size() - 1)
                }
                hunksStart = i
                onlyWhiteSpace = true
                continue
            }

            if (line.startsWithAny('+','-')) {
                def prefixChange = false
                def prevLine = lines[i - 1]

                if (line.charAt(0) == (char)'+' && prevLine.charAt(0) == (char)'-') {
                    def prevTrim = prevLine.substring(1).replaceAll("\\s", "")
                    def currTrim = line.substring(1).replaceAll("\\s", "")

                    if (prevTrim == currTrim) {
                        prefixChange = true
                    }

                    def pMatcher = FIELD_PATTERN.matcher(prevLine)
                    def cMatcher = FIELD_PATTERN.matcher(line)

                    if (pMatcher.find() && cMatcher.find() &&
                            pMatcher.group(6) == cMatcher.group(6) && // = ...
                            pMatcher.group(5) == cMatcher.group(5) && // field name
                            pMatcher.group(3) == cMatcher.group(3) && // static
                            (accessChange(pMatcher.group(2), cMatcher.group(2)) || pMatcher.group(4) != cMatcher.group(4))) {
                        reporter.report("Patch contains access changes or final removal at line ${i + 1}, file: $patchPath", false)
                    }

                    pMatcher = METHOD_PATTERN.matcher(prevLine)
                    cMatcher = METHOD_PATTERN.matcher(line)

                    if (pMatcher.find() && cMatcher.find() &&
                            pMatcher.group(6) == cMatcher.group(6) && // params
                            pMatcher.group(5) == cMatcher.group(5) && // <T> void name
                            pMatcher.group(3) == cMatcher.group(3) && // static
                            (accessChange(pMatcher.group(2), cMatcher.group(2)) || pMatcher.group(4) != cMatcher.group(4))) {
                        reporter.report("Patch contains access changes or final removal at line ${i + 1}, file: $patchPath", false)
                    }

                    pMatcher = CLASS_PATTERN.matcher(prevLine)
                    cMatcher = CLASS_PATTERN.matcher(line)

                    if (pMatcher.find() && cMatcher.find() &&
                            pMatcher.group(6) == cMatcher.group(6) && // ClassName<> extends ...
                            pMatcher.group(5) == cMatcher.group(5) && // class | interface
                            pMatcher.group(3) == cMatcher.group(3) && // static
                            (accessChange(pMatcher.group(2), cMatcher.group(2)) || pMatcher.group(4) != cMatcher.group(4))) {
                        reporter.report("Patch contains access changes or final removal at line ${i + 1}, file: $patchPath", false)
                    }
                }

                if (line.charAt(0) == (char)'-' && i + 1 < lines.size()) {
                    final nextLine = lines[i + 1]
                    if (nextLine.charAt(0) == (char)'+') {
                        final nextTrim = nextLine.substring(1).replaceAll("\\s", "")
                        final currTrim = line.substring(1).replaceAll("\\s", "")

                        if (nextTrim == currTrim) {
                            prefixChange = true
                        }
                    }
                }

                final isWhiteSpaceChange = WHITESPACE_PATTERN.matcher(line).find()

                if (!prefixChange && !isWhiteSpaceChange) {
                    onlyWhiteSpace = hasS2SArtifact && IMPORT_PATTERN.matcher(line).find()
                } else if (isWhiteSpaceChange) {
                    final prevLineChange = prevLine.startsWithAny('+','-')
                    final nextLineChange = i + 1 < lines.size() && lines[i + 1].startsWithAny('+','-')

                    if (!prevLineChange && !nextLineChange) {
                        reporter.report("Patch contains white space change in valid hunk at line ${i + 1}, file: $patchPath", false)
                    }
                }

                if (line.contains('\t')) {
                    reporter.report("Patch contains tabs on line ${i + 1}, file: $patchPath")
                    line = line.replaceAll('\t', '    ')
                    newLines.remove(newLines.size() - 1)
                    newLines.add(line + '\n')
                }

                if (IMPORT_PATTERN.matcher(line).find() && !hasS2SArtifact) {
                    reporter.report("Patch contains import change on line ${i + 1}, file: $patchPath", false)
                }
            }
        }

        if (onlyWhiteSpace) {
            if (!hasS2SArtifact)
                reporter.report("Patch contains only white space hunk starting at line ${hunksStart + 1}, file: $patchPath")
            def toRemove = i - hunksStart;
            while (toRemove-- > 0)
                newLines.remove(newLines.size() - 1)
        }

        if ((reporter.fixed.size() > oldFixedErrors && fix) || hasS2SArtifact) {
            if (newLines.size() <= 2) {
                logger.lifecycle("Patch is now empty removing, file: {}", patchPath)
                Files.delete(patch)
            }
            else {
                if (!hasS2SArtifact)
                    logger.lifecycle("*** Updating patch file. Please run setup then genPatches again. ***")
                Files.newBufferedWriter(patch).withCloseable {
                    newLines.each { l -> it.write(l) }
                }
            }
        }
    }
}
