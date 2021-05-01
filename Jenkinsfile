@Library('forge-shared-library')_

pipeline {
    options {
        disableConcurrentBuilds()
    }
    agent {
        docker {
            image 'gradle:jdk8'
            args '-v forgegc:/home/gradle/.gradle/'
        }
    }
    environment {
        GRADLE_ARGS = '--no-daemon --console=plain' // No daemon for now as FG3 kinda derps. //'-Dorg.gradle.daemon.idletimeout=5000'
        DISCORD_WEBHOOK = credentials('forge-discord-jenkins-webhook')
        DISCORD_PREFIX = "Job: Forge Branch: ${BRANCH_NAME} Build: #${BUILD_NUMBER}"
        JENKINS_HEAD = 'https://wiki.jenkins-ci.org/download/attachments/2916393/headshot.png'
    }

    stages {
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
                withGradle {
                    sh './gradlew ${GRADLE_ARGS} --refresh-dependencies --continue setup'
                }
                script {
                    env.MYVERSION = sh(returnStdout: true, script: './gradlew :forge:properties -q | grep "^version:" | cut -d" " -f2').trim()
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
                CROWDIN = credentials('forge-crowdin')
                KEYSTORE = credentials('forge-jenkins-keystore-old')
                KEYSTORE_KEYPASS = credentials('forge-jenkins-keystore-old-keypass')
                KEYSTORE_STOREPASS = credentials('forge-jenkins-keystore-old-keypass')
            }
            steps {
                withCredentials([usernamePassword(credentialsId: 'maven-forge-user', usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASSWORD')]) {
                    withGradle {
                        sh './gradlew ${GRADLE_ARGS} :forge:publish -PkeystoreKeyPass=${KEYSTORE_KEYPASS} -PkeystoreStorePass=${KEYSTORE_STOREPASS} -Pkeystore=${KEYSTORE} -PcrowdinKey=${CROWDIN}'
                    }
                }
            }
            post {
                success {
                    build job: 'filegenerator', parameters: [string(name: 'COMMAND', value: "promote net.minecraftforge:forge ${env.MYVERSION} latest")], propagate: false, wait: false
                }
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
                withGradle {
                    sh './gradlew ${GRADLE_ARGS} :forge:publish -PcrowdinKey=${CROWDIN}'
                }
            }
        }
    }
    post {
        always {
            script {
                archiveArtifacts artifacts: 'projects/forge/build/libs/**/*.*', fingerprint: true, onlyIfSuccessful: true, allowEmptyArchive: true

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
