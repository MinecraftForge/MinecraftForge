import jetbrains.buildServer.configs.kotlin.v2019_2.*
import jetbrains.buildServer.configs.kotlin.v2019_2.projectFeatures.githubIssues

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2021.2"

project {

    buildType(Build)
    buildType(BuildSecondaryBranches)
    buildType(PullRequests)

    params {
        text("git_main_branch", "main", label = "Git Main Branch", description = "The git main or default branch to use in VCS operations.", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("github_repository_name", "MinecraftForge", label = "The github repository name. Used to connect to it in VCS Roots.", description = "This is the repository slug on github. So for example `MinecraftForge` or `MinecraftForge`. It is interpolated into the global VCS Roots.", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("env.PUBLISHED_JAVA_ARTIFACT_ID", "forge", label = "Published artifact id", description = "The maven coordinate artifact id that has been published by this build. Can not be empty.", allowEmpty = false)
        text("env.PUBLISHED_JAVA_FML_ARTIFACT_ID", "fmlonly", label = "Published fmlonly artifact id", description = "The maven coordinate artifact id for fml only that has been published by this build. Can not be empty.", allowEmpty = false)
        text("env.PUBLISHED_JAVA_FML_ARTIFACT_VERSION", "0.0.0-SNAPSHOT", label = "Published fmlonly artifact version", description = "The version for fml only that has been published by this build. Can not be empty.", allowEmpty = false)
        text("env.PUBLISHED_JAVA_GROUP", "net.minecraftforge", label = "Published group", description = "The maven coordinate group that has been published by this build. Can not be empty.", allowEmpty = false)
    }

    features {
        githubIssues {
            id = "MinecraftForge_MinecraftForge__IssueTracker"
            displayName = "MinecraftForge/MinecraftForge"
            repositoryURL = "https://github.com/MinecraftForge/MinecraftForge"
        }
    }
}

object Build : BuildType({
    templates(AbsoluteId("MinecraftForge_SetupGradleUtilsCiEnvironmen"), AbsoluteId("MinecraftForge_BuildWithDiscordNotifications"), AbsoluteId("MinecraftForge_BuildMainBranches"), AbsoluteId("MinecraftForge_BuildUsingGradle"), AbsoluteId("MinecraftForge_PublishProjectUsingGradle"), AbsoluteId("MinecraftForge_TriggersStaticFilesWebpageGenerator"))
    id("MinecraftForge_MinecraftForge__Build")
    name = "Build"
    description = "Builds and Publishes the main branches of the project."

    features {
        feature {
            id = "trigger_fml_only_files_generator"
            type = "triggerBuildFeature"
            param("triggers", "MinecraftForge_FilesGenerator_GeneratePages")
            param("parameters", """
                env.PUBLISHED_JAVA_FML_ARTIFACT_ID~env.PUBLISHED_JAVA_ARTIFACT_ID
                env.PUBLISHED_JAVA_FML_ARTIFACT_VERSION~env.PUBLISHED_JAVA_ARTIFACT_VERSION
                env.PUBLISHED_JAVA_GROUP
            """.trimIndent())
        }
    }
})

object BuildSecondaryBranches : BuildType({
    templates(AbsoluteId("MinecraftForge_ExcludesBuildingDefaultBranch"), AbsoluteId("MinecraftForge_SetupGradleUtilsCiEnvironmen"), AbsoluteId("MinecraftForge_BuildWithDiscordNotifications"), AbsoluteId("MinecraftForge_BuildMainBranches"), AbsoluteId("MinecraftForge_BuildUsingGradle"))
    id("MinecraftForge_MinecraftForge__BuildSecondaryBranches")
    name = "Build - Secondary Branches"
    description = "Builds and Publishes the secondary branches of the project."
})

object PullRequests : BuildType({
    templates(AbsoluteId("MinecraftForge_BuildPullRequests"), AbsoluteId("MinecraftForge_SetupGradleUtilsCiEnvironmen"), AbsoluteId("MinecraftForge_BuildWithDiscordNotifications"), AbsoluteId("MinecraftForge_BuildUsingGradle"))
    id("MinecraftForge_MinecraftForge__PullRequests")
    name = "Pull Requests"
    description = "Builds pull requests for the project"
})
