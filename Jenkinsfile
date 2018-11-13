@Library('forge-shared-library')_

pipeline {
    agent {
        docker {
            image 'gradlewrapper:latest'
            args '-v gradlecache:/gradlecache'
        }
    }
    environment {
        GRADLE_ARGS = '--no-daemon' // No daemon for now as FG3 kinda derps. //'-Dorg.gradle.daemon.idletimeout=5000'
    }

    stages {
        stage('fetch') {
            steps {
                git(url: 'https://github.com/MinecraftForge/MinecraftForge.git', changelog: true)
            }
        }
        stage('buildandtest') {
            cache(maxCacheSize: 250/*MB*/, caches: [
                [$class: 'ArbitraryFileCache', excludes: 'log.txt', includes: '**/*', path: '${WORKSPACE}/projects/forge/build/extractRangeMap'] //Cache the rangemap to help speed up builds
            ]){
                steps {
                    sh './gradlew ${GRADLE_ARGS} --refresh-dependencies --continue build test'
                    script {
                        env.MYVERSION = sh(returnStdout: true, script: './gradlew properties -q | grep "version:" | awk \'{print $2}\'').trim()
                    }
                }
                post {
                    success {
                        writeChangelog(currentBuild, 'build/changelog.txt')
                        archiveArtifacts artifacts: 'build/changelog.txt', fingerprint: false
                    }
                }
            }
        }
        stage('publish') {
            cache(maxCacheSize: 250/*MB*/, caches: [
                [$class: 'ArbitraryFileCache', excludes: 'log.txt', includes: '**/*', path: '${WORKSPACE}/projects/forge/build/extractRangeMap'] //Cache the rangemap to help speed up builds
            ]){
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
                    sh './gradlew ${GRADLE_ARGS} forge:publish -PforgeMavenUser=${FORGE_MAVEN_USR} -PforgeMavenPassword=${FORGE_MAVEN_PSW} -PkeystoreKeyPass=${KEYSTORE_KEYPASS} -PkeystoreStorePass=${KEYSTORE_STOREPASS} -Pkeystore=${KEYSTORE} -PcrowdinKey=${CROWDIN}'
                    //We're testing so use the test group
                    sh 'curl --user ${FORGE_MAVEN} http://files.minecraftforge.net/maven/manage/promote/latest/net.minecraftforge.test.forge/${MYVERSION}'
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: 'build/libs/**/*.jar', fingerprint: true
            junit 'build/test-results/*/*.xml'
            jacoco sourcePattern: '**/src/*/java'
        }
    }
}
