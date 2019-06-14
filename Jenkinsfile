@Library('forge-shared-library')_

pipeline {
    options {
        disableConcurrentBuilds()
    }
    agent {
        docker {
            image 'gradlewrapper:latest'
            args '-v gradlecache:/gradlecache'
        }
    }
    environment {
        GRADLE_ARGS = '--no-daemon --console=plain' // No daemon for now as FG3 kinda derps. //'-Dorg.gradle.daemon.idletimeout=5000'
        DISCORD_WEBHOOK = credentials('forge-discord-jenkins-webhook')
        DISCORD_PREFIX = "Job: Forge Branch: ${BRANCH_NAME} Build: #${BUILD_NUMBER}"
        JENKINS_HEAD = 'https://wiki.jenkins-ci.org/download/attachments/2916393/headshot.png'
    }

    stages {
        stage('fetch') {
            steps {
                checkout scm
            }
        }
        stage('notify_start') {
            when {
                not {
                    changeRequest()
                }
            }
            steps {
                discordSend(
                    title: "${DISCORD_PREFIX} Started",
                    successful: true,
                    result: 'ABORTED', //White border
                    thumbnail: JENKINS_HEAD,
                    webhookURL: DISCORD_WEBHOOK
                )
            }
        }
        stage('setup') {
            steps {
                sh './gradlew ${GRADLE_ARGS} --refresh-dependencies --continue setup'
                script {
                    env.MYVERSION = sh(returnStdout: true, script: './gradlew :forge:properties -q | grep "version:" | awk \'{print $2}\'').trim()
                }
            }
        }
        stage('changelog') {
            when {
                not {
                    changeRequest()
                }
            }
            steps {
                writeChangelog(currentBuild, 'build/changelog.txt')
            }
        }
        stage('publish') {
            when {
                not {
                    changeRequest()
                }
            }
            environment {
                FORGE_MAVEN = credentials('forge-maven-forge-user')
                CROWDIN = credentials('forge-crowdin')
                KEYSTORE = credentials('forge-jenkins-keystore-old')
                KEYSTORE_KEYPASS = credentials('forge-jenkins-keystore-old-keypass')
                KEYSTORE_STOREPASS = credentials('forge-jenkins-keystore-old-keypass')
            }
            steps {
                sh './gradlew ${GRADLE_ARGS} :forge:publish -PforgeMavenUser=${FORGE_MAVEN_USR} -PforgeMavenPassword=${FORGE_MAVEN_PSW} -PkeystoreKeyPass=${KEYSTORE_KEYPASS} -PkeystoreStorePass=${KEYSTORE_STOREPASS} -Pkeystore=${KEYSTORE} -PcrowdinKey=${CROWDIN}'
                sh 'curl --user ${FORGE_MAVEN} http://files.minecraftforge.net/maven/manage/promote/latest/net.minecraftforge.forge/${MYVERSION}'
            }
        }
        stage('test_publish_pr') { //Publish to local repo to test full process, but don't include credentials so it can't sign/publish to maven
            when {
                changeRequest()
            }
            environment {
                CROWDIN = credentials('forge-crowdin')
            }
            steps {
                sh './gradlew ${GRADLE_ARGS} :forge:publish -PcrowdinKey=${CROWDIN}'
            }
        }
    }
    post {
        always {
            script {
                archiveArtifacts artifacts: 'projects/forge/build/libs/**/*.*', fingerprint: true, onlyIfSuccessful: true, allowEmptyArchive: true
                //junit 'build/test-results/*/*.xml'
                //jacoco sourcePattern: '**/src/*/java'
                
                if (env.CHANGE_ID == null) { // This is unset for non-PRs
                    discordSend(
                        title: "${DISCORD_PREFIX} Finished ${currentBuild.currentResult}",
                        description: '```\n' + getChanges(currentBuild) + '\n```',
                        successful: currentBuild.resultIsBetterOrEqualTo("SUCCESS"),
                        result: currentBuild.currentResult,
                        thumbnail: JENKINS_HEAD,
                        webhookURL: DISCORD_WEBHOOK
                    )
                }
            }
        }
    }
}
