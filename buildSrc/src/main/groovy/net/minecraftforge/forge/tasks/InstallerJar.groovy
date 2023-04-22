package net.minecraftforge.forge.tasks

import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.*
import org.gradle.api.provider.SetProperty

abstract class InstallerJar extends Zip {
    @Input @Optional abstract SetProperty<String> getPackedDependencies()

    InstallerJar() {
        archiveClassifier.set('installer')
        archiveExtension.set('jar') // Needs to be Zip task to not override Manifest, so set extension
        destinationDirectory.set(project.layout.buildDirectory.dir('libs'))

        def installerJson = project.tasks.installerJson
        def launcherJson = project.tasks.launcherJson
        def downloadInstaller = project.tasks.downloadInstaller

        dependsOn(installerJson, launcherJson, downloadInstaller)
        from(installerJson, launcherJson)

        from(project.rootProject.file('/src/main/resources/url.png'))
        project.afterEvaluate {
            from(project.zipTree(downloadInstaller.output)) {
                duplicatesStrategy = 'exclude'
            }

            if (!System.env.TEAMCITY_VERSION) {
                dependsOn(project.universalJar)
                from(project.universalJar) {
                    into '/maven/' + project.group.replace('.', '/') + '/' + project.universalJar.archiveBaseName.get() + '/' + project.version + '/'
                }

                packedDependencies.get().forEach {
                    def jarTask = project.rootProject.getTasks().findByPath(it)
                    dependsOn(jarTask)
                    from(jarTask) {
                        into '/maven/' + jarTask.project.group.replace('.', '/') + '/' + jarTask.archiveBaseName.get() + '/' + jarTask.project.version + '/'
                    }
                }
            }
        }
    }
}
