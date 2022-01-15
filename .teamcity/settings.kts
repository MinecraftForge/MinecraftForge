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
        text("docker_jdk_version", "17", label = "Gradle version", description = "The version of the JDK to use during execution of tasks in a JDK.", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("docker_gradle_version", "7.3", label = "Gradle version", description = "The version of Gradle to use during execution of Gradle tasks.", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("git_main_branch", "1.18.x", label = "Git Main Branch", description = "The git main or default branch to use in VCS operations.", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("git_branch_spec", """
                +:refs/heads/(main*)
                +:refs/heads/(master*)
                +:refs/heads/(develop|release|staging|main|master)
                +:refs/heads/(1.*)
            """.trimIndent(), label = "The branch specification of the repository", description = "By default all main branches are build by the configuration. Modify this value to adapt the branches build.", display = ParameterDisplay.HIDDEN, allowEmpty = true)
        text("github_repository_name", "MinecraftForge", label = "The github repository name. Used to connect to it in VCS Roots.", description = "This is the repository slug on github. So for example `MinecraftForge` or `MinecraftForge`. It is interpolated into the global VCS Roots.", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("env.PUBLISHED_JAVA_ARTIFACT_ID", "forge", label = "Published artifact id", description = "The maven coordinate artifact id that has been published by this build. Can not be empty.", allowEmpty = false)
        text("env.PUBLISHED_JAVA_FML_ARTIFACT_ID", "fmlonly", label = "Published fmlonly artifact id", description = "The maven coordinate artifact id for fml only that has been published by this build. Can not be empty.", allowEmpty = false)
        text("env.PUBLISHED_JAVA_FML_ARTIFACT_VERSION", "0.0.0-SNAPSHOT", label = "Published fmlonly artifact version", description = "The version for fml only that has been published by this build. Can not be empty.", allowEmpty = false)
        text("env.PUBLISHED_JAVA_GROUP", "net.minecraftforge", label = "Published group", description = "The maven coordinate group that has been published by this build. Can not be empty.", allowEmpty = false)
        text("additional_publishing_gradle_parameters", "-PcrowdingKey=%env.CROWDIN_KEY%", label = "Additional gradle parameters for publish", description = "Contains the additional gradle parameters used during publishing.", display = ParameterDisplay.HIDDEN, allowEmpty = true)

        checkbox("should_execute_build", "false", label = "Should build", description = "Indicates if the build task should be executed.", display = ParameterDisplay.HIDDEN,
            checked = "true", unchecked = "false")
        checkbox("should_execute_test", "false", label = "Should build", description = "Indicates if the build task should be executed.", display = ParameterDisplay.HIDDEN,
            checked = "true", unchecked = "false")
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
    templates(AbsoluteId("MinecraftForge_SetupGradleUtilsCiEnvironmen"), AbsoluteId("MinecraftForge_BuildWithDiscordNotifications"), AbsoluteId("MinecraftForge_BuildMainBranches"), AbsoluteId("MinecraftForge_SetupProjectUsingGradle"), AbsoluteId("MinecraftForge_BuildUsingGradle"), AbsoluteId("MinecraftForge_PublishProjectUsingGradle"), AbsoluteId("MinecraftForge_TriggersStaticFilesWebpageGenerator"))
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
    templates(AbsoluteId("MinecraftForge_ExcludesBuildingDefaultBranch"), AbsoluteId("MinecraftForge_SetupGradleUtilsCiEnvironmen"), AbsoluteId("MinecraftForge_BuildWithDiscordNotifications"), AbsoluteId("MinecraftForge_BuildMainBranches"), AbsoluteId("MinecraftForge_SetupProjectUsingGradle"), AbsoluteId("MinecraftForge_BuildUsingGradle"))
    id("MinecraftForge_MinecraftForge__BuildSecondaryBranches")
    name = "Build - Secondary Branches"
    description = "Builds and Publishes the secondary branches of the project."

    vcs {
        branchFilter = """
            +:*             
            -:refs/heads/(develop|release|staging|main|master)
            -:<default>
            -:refs/heads/%git_main_branch%
            -:refs/heads/main*
            -:refs/heads/master*
            -:refs/heads/1.*
            -:(develop|release|staging|main|master)
            -:%git_main_branch%
            -:main*
            -:master*
            -:1.*            
        """.trimIndent()
    }
})

object PullRequests : BuildType({
    templates(AbsoluteId("MinecraftForge_BuildPullRequests"), AbsoluteId("MinecraftForge_SetupGradleUtilsCiEnvironmen"), AbsoluteId("MinecraftForge_BuildWithDiscordNotifications"), AbsoluteId("MinecraftForge_SetupProjectUsingGradle"), AbsoluteId("MinecraftForge_BuildUsingGradle"))
    id("MinecraftForge_MinecraftForge__PullRequests")
    name = "Pull Requests"
    description = "Builds pull requests for the project"

    params {
        checkbox("should_execute_build", "true", label = "Should build", description = "Indicates if the build task should be executed.", display = ParameterDisplay.HIDDEN,
            checked = "true", unchecked = "false")
        text(
            "gradle_build_task",
            "assemble",
            label = "Gradle build task to execute during build",
            description = "Determines the build task that is executed to build the project.",
            display = ParameterDisplay.HIDDEN,
            allowEmpty = false
        )
    }
})
