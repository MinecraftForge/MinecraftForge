/*
 * Copyright (c) Forge Development LLC and contributors
 * SPDX-License-Identifier: LGPL-2.1-only
 */

package net.minecraftforge.forge.tasks

import net.minecraftforge.srgutils.MinecraftVersion
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode

abstract class ValidateDeprecations extends DefaultTask {
    @InputFile
    abstract RegularFileProperty getInput()

    @Input
    abstract Property<String> getMcVersion()

    ValidateDeprecations() {
        this.onlyIf { !System.env.TEAMCITY_VERSION }
    }

    @TaskAction
    protected void exec() {
        def mcVer = MinecraftVersion.from(mcVersion.get())
        def errors = []

        Util.processClassNodes(input.get().asFile) {
            processNode(mcVer, errors, it)
        }

        if (!errors.isEmpty()) {
            errors.forEach {
                this.logger.error("Deprecated ${it[0]} is marked for removal in ${it[1]} but is not yet removed")
            }
            throw new IllegalStateException("Found deprecated members marked for removal but not yet removed in ${mcVer}; see log for details")
        }
    }

    protected processNode(MinecraftVersion mcVer, List<String> errors, ClassNode node) {
        node.visibleAnnotations?.each { annotation ->
            ValidateDeprecations.processAnnotations(annotation, mcVer, errors) {
                "class ${node.name}"
            }
        }
        node.fields?.each { field ->
            field.visibleAnnotations?.each { annotation ->
                ValidateDeprecations.processAnnotations(annotation, mcVer, errors) {
                    "field ${node.name}#${field.name}"
                }
            }
        }
        node.methods?.each { method ->
            method.visibleAnnotations?.each { annotation ->
                ValidateDeprecations.processAnnotations(annotation, mcVer, errors) {
                    "method ${node.name}#${method.name}${method.desc}"
                }
            }
        }
    }

    private static void processAnnotations(AnnotationNode annotation, MinecraftVersion mcVer, List<String> errors, Closure context) {
        def values = annotation.values
        if (values === null)
            return
        int forRemoval = values.indexOf('forRemoval')
        int since = values.indexOf('since')
        if (annotation.desc == 'Ljava/lang/Deprecated;' && forRemoval !== -1 && since !== -1 && values.size() >= 4 && values[forRemoval + 1] === true) {
            def oldVersion = MinecraftVersion.from(values[since + 1])
            def split = ValidateDeprecations.splitDots(oldVersion.toString())
            if (split.length < 2)
                return
            def removeVersion = MinecraftVersion.from("${split[0]}.${split[1] + 1}")
            if (removeVersion <= mcVer)
                errors.add([context(), removeVersion])
        }
    }

    private static int[] splitDots(String version) {
        String[] pts = version.split('\\.')
        int[] values = new int[pts.length]
        for (int x = 0; x < pts.length; x++)
            values[x] = Integer.parseInt(pts[x])
        return values
    }
}
