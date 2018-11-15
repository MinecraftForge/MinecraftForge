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
    }

    stages {
        stage('fetch') {
            steps {
                checkout scm
            }
        }
        stage('setup') {
            steps {
                sh './gradlew ${GRADLE_ARGS} --refresh-dependencies --continue setup'
                script {
                    env.MYVERSION = sh(returnStdout: true, script: './gradlew :forge:properties -q | grep "version:" | awk \'{print $2}\'').trim()
                }
            }
            post {
                success {
                    writeChangelog(currentBuild, 'build/changelog.txt')
                }
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
                cache(maxCacheSize: 250/*MB*/, caches: [
                    [$class: 'ArbitraryFileCache', excludes: '', includes: 'output.txt', path: '${WORKSPACE}/projects/forge/build/extractRangeMap/'] //Cache the rangemap to help speed up builds
                ]){
                    sh './gradlew ${GRADLE_ARGS} :forge:publish -PforgeMavenUser=${FORGE_MAVEN_USR} -PforgeMavenPassword=${FORGE_MAVEN_PSW} -PkeystoreKeyPass=${KEYSTORE_KEYPASS} -PkeystoreStorePass=${KEYSTORE_STOREPASS} -Pkeystore=${KEYSTORE} -PcrowdinKey=${CROWDIN}'
                }
                //We're testing so use the test group
                sh 'curl --user ${FORGE_MAVEN} http://files.minecraftforge.net/maven/manage/promote/latest/net.minecraftforge.test.forge/${MYVERSION}'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'projects/forge/build/libs/**/*.*', fingerprint: true
            //junit 'build/test-results/*/*.xml'
            //jacoco sourcePattern: '**/src/*/java'
        }
    }
}
