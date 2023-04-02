package net.minecraftforge.forge.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.TaskAction

import java.nio.file.Files

abstract class FixPatchImports extends DefaultTask {

    @InputDirectory abstract DirectoryProperty getClean()
    @InputDirectory abstract DirectoryProperty getPatched()

    @TaskAction
    void run() {
        final patchedPath = getPatched().get().asFile.toPath()
        final cleanPath = getClean().get().asFile.toPath()

        try (final var stream = Files.find(patchedPath, Integer.MAX_VALUE, (path, attr) -> path.toString().endsWith('.java'))) {
            final itr = stream.iterator()
            while (itr.hasNext()) {
                final next = itr.next()
                final clean = cleanPath.resolve(patchedPath.relativize(next).toString())

                final patchedLines = Files.readAllLines(next)
                final cleanLines = Files.readAllLines(clean)

                final patchedImport = patchedLines.findLastIndexOf(2) { it.startsWith('import ') }
                final cleanImport = cleanLines.findLastIndexOf(2) { it.startsWith('import ') }

                if (cleanImport !== patchedImport) {
                    patchedLines.removeIf {  it.startsWith('import ') }
                    patchedLines.addAll(2, cleanLines.subList(2, cleanImport + 1))
                    Files.write(next, patchedLines)
                }
            }
        }
    }
}
