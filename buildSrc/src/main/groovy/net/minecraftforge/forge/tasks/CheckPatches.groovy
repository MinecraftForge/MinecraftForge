package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files
import java.nio.file.Paths
import java.util.regex.Pattern

abstract class CheckPatches extends DefaultTask {
    @InputDirectory abstract DirectoryProperty getPatchDir()
    @Input boolean autoFix = false

    @TaskAction
    protected void exec() {
        def hasS2SArtifact = [
                Paths.get("patches/minecraft/net/minecraft/client/renderer/ViewArea.java.patch"),
                Paths.get("patches/minecraft/net/minecraft/data/models/blockstates/Variant.java.patch")
        ]

        def verified = true;
        patchDir.get().asFileTree.each { patch ->
            def patchPath = project.rootDir.toPath().relativize(patch.toPath())
            verified &= verifyPatch(patch, autoFix, patchPath.toString(), hasS2SArtifact.contains(patchPath))
        }

        if (!verified)
            throw new RuntimeException('One or more patches failed verification. Check the log for errors.')
    }

    def verifyPatch(patch, fix, patchPath, hasS2SArtifact) {
        def hunk_start_pattern = Pattern.compile('^@@ -[0-9,]* \\+[0-9,_]* @@$')
        def white_space_pattern = Pattern.compile('^[+\\-]\\s*$')
        def import_pattern = Pattern.compile('^[+\\-]\\s*import.*')
        def field_pattern = Pattern.compile('^[+\\-][\\s]*((public|protected|private)[\\s]*)?(static[\\s]*)?(final)?([^=;]*)(=.*)?;\\s*$')
        def method_pattern = Pattern.compile('^[+\\-][\\s]*((public|protected|private)[\\s]*)?(static[\\s]*)?(final)?([^(]*)[(]([^)]*)?[)]\\s*[{]\\s*$')
        def class_pattern = Pattern.compile('^[+\\-][\\s]*((public|protected|private)[\\s]*)?(static[\\s]*)?(final[\\s]*)?(class|interface)([^{]*)[{]\\s*$')

        def accessMap = [("private"):0, (null):1, ("protected"):2, ("public"):3]

        def hasProblem = false;
        def lines = patch.readLines()

        def hunksStart = 0
        def onlyWhiteSpace = false

        def didFix = false
        def newLines = []
        def whiteSpaceErrors = []

        // First two lines are file name ++/-- and we do not care
        newLines.add(lines[0] + '\n')
        newLines.add(lines[1] + '\n')

        int i = 2
        for (; i < lines.size(); ++i) {
            def line = lines[i]
            newLines.add(line + '\n')

            if (hunk_start_pattern.matcher(line).find()) {
                if (onlyWhiteSpace) {
                    if (fix || hasS2SArtifact) {
                        logger.lifecycle("Removing white space hunk starting at line {}, file: {}", hunksStart + 1, patchPath)
                        def toRemove = i - hunksStart;
                        while (toRemove-- > 0)
                            newLines.remove(newLines.size() - 1)
                        didFix = true
                    }
                    else {
                        logger.lifecycle("Patch contains only white space hunk starting at line {}, file: {}", hunksStart + 1, patchPath)
                        hasProblem = true
                    }
                }
                else {
                    if (!whiteSpaceErrors.empty)
                        hasProblem = true
                    whiteSpaceErrors.each { error -> logger.lifecycle(error) }
                    whiteSpaceErrors.clear()
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

                    if (prevTrim.equals(currTrim)) {
                        prefixChange = true
                    }

                    def pMatcher = field_pattern.matcher(prevLine)
                    def cMatcher = field_pattern.matcher(line)

                    if (pMatcher.find() && cMatcher.find() &&
                            pMatcher.group(6) == cMatcher.group(6) && // = ...
                            pMatcher.group(5) == cMatcher.group(5) && // field name
                            pMatcher.group(3) == cMatcher.group(3) && // static
                            (accessMap[pMatcher.group(2)] < accessMap[cMatcher.group(2)] || pMatcher.group(4) != cMatcher.group(4))) {
                        logger.lifecycle("Patch contains access changes or final removal at line {}, file: {}", i + 1, patchPath)
                        hasProblem = true
                    }

                    pMatcher = method_pattern.matcher(prevLine)
                    cMatcher = method_pattern.matcher(line)

                    if (pMatcher.find() && cMatcher.find() &&
                            pMatcher.group(6) == cMatcher.group(6) && // params
                            pMatcher.group(5) == cMatcher.group(5) && // <T> void name
                            pMatcher.group(3) == cMatcher.group(3) && // static
                            (accessMap[pMatcher.group(2)] < accessMap[cMatcher.group(2)] || pMatcher.group(4) != cMatcher.group(4))) {
                        logger.lifecycle("Patch contains access changes or final removal at line {}, file: {}", i + 1, patchPath)
                        hasProblem = true
                    }

                    pMatcher = class_pattern.matcher(prevLine)
                    cMatcher = class_pattern.matcher(line)

                    if (pMatcher.find() && cMatcher.find() &&
                            pMatcher.group(6) == cMatcher.group(6) && // ClassName<> extends ...
                            pMatcher.group(5) == cMatcher.group(5) && // class | interface
                            pMatcher.group(3) == cMatcher.group(3) && // static
                            (accessMap[pMatcher.group(2)] < accessMap[cMatcher.group(2)] || pMatcher.group(4) != cMatcher.group(4))) {
                        logger.lifecycle("Patch contains access changes or final removal at line {}, file: {}", i + 1, patchPath)
                        hasProblem = true
                    }
                }

                if (line.charAt(0) == (char)'-' && i + 1 < lines.size()) {
                    def nextLine = lines[i + 1]
                    if (nextLine.charAt(0) == (char)'+') {
                        def nextTrim = nextLine.substring(1).replaceAll("\\s", "")
                        def currTrim = line.substring(1).replaceAll("\\s", "")

                        if (nextTrim.equals(currTrim)) {
                            prefixChange = true
                        }
                    }
                }

                def isWhiteSpaceChange = white_space_pattern.matcher(line).find()

                if (!prefixChange && !isWhiteSpaceChange) {
                    onlyWhiteSpace = hasS2SArtifact && import_pattern.matcher(line).find()
                }
                else if (isWhiteSpaceChange) {
                    def prevLineChange = prevLine.startsWithAny('+','-')
                    def nextLineChange = i + 1 < lines.size() && lines[i + 1].startsWithAny('+','-')

                    if (!prevLineChange && !nextLineChange) {
                        whiteSpaceErrors.add(String.format("Patch contains white space change in valid hunk at line %d (cannot auto fix), file: %s", i + 1, patchPath))
                    }
                }

                if (line.contains("\t")) {
                    if (!fix) {
                        logger.lifecycle("Patch contains tabs on line {}, file: {}", i + 1, patchPath)
                        hasProblem = true
                    }
                    else {
                        logger.lifecycle("Fixing tabs on line {}, file: {}", i + 1, patchPath)
                        line = line.replaceAll('\t', '    ')
                        newLines.remove(newLines.size() - 1)
                        newLines.add(line + '\n')
                        didFix = true
                    }
                }

                if (import_pattern.matcher(line).find() && !hasS2SArtifact) {
                    logger.lifecycle("Patch contains import change on line {}, file: {}", i + 1, patchPath)
                    hasProblem = true
                }
            }
        }

        if (onlyWhiteSpace) {
            if (fix || hasS2SArtifact) {
                logger.lifecycle("Removing white space hunk starting at line {}, file: {}", hunksStart + 1, patchPath)
                def toRemove = i - hunksStart;
                while (toRemove-- > 0)
                    newLines.remove(newLines.size() - 1)
                didFix = true
            }
            else {
                logger.lifecycle("Patch contains only white space hunk starting at line {}, file: {}", hunksStart + 1, patchPath)
                hasProblem = true
            }
        }
        else {
            if (!whiteSpaceErrors.empty)
                hasProblem = true
            whiteSpaceErrors.each { error -> logger.lifecycle(error) }
        }

        if (didFix) {
            if (newLines.size() <= 2) {
                logger.lifecycle("Patch is now empty removing, file: {}", patchPath)
                Files.delete(patch.toPath())
            }
            else {
                if (!hasS2SArtifact)
                    logger.lifecycle("*** Updating patch file. Please run setup then genPatches again. ***")
                patch.withWriter('UTF-8') { writer ->
                    newLines.each { l -> writer.write(l) }
                }
            }
        }

        return !hasProblem
    }
}
