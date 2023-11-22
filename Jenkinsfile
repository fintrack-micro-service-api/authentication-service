pipeline {
    agent any
    tools {
        gradle 'gradle'
        jdk 'jdk'
    }
    environment {
        DOCKER_REGISTRY = 'kimheang68'
        IMAGE_NAME = 'fintrack-authentication-service'
        CONTAINER_NAME = 'spring-boot-container'
        TELEGRAM_BOT_TOKEN = credentials('telegram-token')
        TELEGRAM_CHAT_ID = credentials('chat-id')
        BUILD_INFO = "${currentBuild.number}"
        COMMITTER = sh(script: 'git log -1 --pretty=format:%an', returnStdout: true).trim()
        BRANCH = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
        SONARQUBE_TOKEN = credentials('sonarqube-token')
    }

    stages {
        stage('Notify Start') {
            steps {
                script {
                    echo "🚀 Pipeline Started: ${env.JOB_NAME}"
                    sendTelegramMessage("🚀 Pipeline Started:\nJob Name: ${env.JOB_NAME}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        sh './gradlew clean build'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        def errorMessage = "❌ Build stage failed:\n${e.getMessage()}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}\nConsole Output: ${env.BUILD_URL}console"
                        sendTelegramMessage(errorMessage)
                        error(errorMessage)
                    }
                }
            }
        }

        stage('Code Quality Check via SonarQube') {
            steps {
                script {
                    def scannerHome = tool 'sonarqube-scanner'
                    withSonarQubeEnv("sonarqube-server") {
                        def scannerCommand = """
                            ${scannerHome}/bin/sonar-scanner \
                            -Dsonar.projectKey=fintrack-authentication-service \
                            -Dsonar.sources=src \
                            -Dsonar.java.binaries=build/classes \
                            -Dsonar.host.url=http://8.219.131.180:9000 \
                            -Dsonar.login=${env.SONARQUBE_TOKEN}
                        """
                        def codeQualityLogs = sh script: scannerCommand, returnStatus: true

                        if (codeQualityLogs != 0) {
                            sendTelegramMessage("❌ Code Quality Check via SonarQube failed")
                            currentBuild.result = 'FAILURE'
                            error("Code Quality Check via SonarQube failed")
                        } else {
                            echo "✅ Code Quality Check via SonarQube succeeded"
                        }
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    try {
                        sh './gradlew test'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        sendTelegramMessage("❌ Test stage failed: ${e.message}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                        error("Test stage failed: ${e.message}")
                    }
                }
            }
        }

        stage('Check for Existing Container') {
            steps {
                script {
                    try {
                        def containerId = sh(script: "docker ps -a --filter name=${env.CONTAINER_NAME} -q", returnStdout: true).trim()
                        echo "Container ID is ${containerId}"
                        if (containerId) {
                            sh "docker stop ${containerId}"
                            sh "docker rm ${containerId}"
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        sendTelegramMessage("❌ Check for Existing Container stage failed: ${e.message}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                        error("Check for Existing Container stage failed: ${e.message}")
                    }
                }
            }
        }

        stage('Build Image') {
            steps {
                script {
                    try {
                        def buildNumber = currentBuild.number
                        def imageTag = "${DOCKER_REGISTRY}/${IMAGE_NAME}:${buildNumber}"
                        sh "docker build -t ${imageTag} ."
                        sh "docker push ${imageTag}"
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        sendTelegramMessage("❌ Build Image stage failed: ${e.message}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                        error("Build Image stage failed: ${e.message}")
                    }
                }
            }
        }

        stage('Trigger ManifestUpdate') {
            steps {
                script {
                    try {
                        build job: 'spring-boot-pipeline-2', parameters: [string(name: 'DOCKERTAG', value: env.BUILD_NUMBER)]
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        sendTelegramMessage("❌ Trigger ManifestUpdate stage failed: ${e.message}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                        error("Trigger ManifestUpdate stage failed: ${e.message}")
                    }
                }
            }
        }
    }

    post {
        success {
            sendTelegramMessage("✅ All stages succeeded\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
            emailext body: "<html><body><b>✅ All stages succeeded</b><br/>Version: ${BUILD_INFO}<br/>Committer: ${COMMITTER}<br/>Branch: ${BRANCH}<br/>Check console output at <a href='${BUILD_URL}'>${BUILD_URL}</a> to view the results.</body></html>",
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                to: "yan.sovanseyha@gmail.com, kimheangken68@gmail.com",
                mimeType: 'text/html'
        }
        failure {
            emailext body: "<html><body><b>❌ Pipeline failed</b><br/>Version: ${BUILD_INFO}<br/>Committer: ${COMMITTER}<br/>Branch: ${BRANCH}<br/>Check console output at <a href='${BUILD_URL}'>${BUILD_URL}</a> to view the results.</body></html>",
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                to: "yan.sovanseyha@gmail.com, kimheangken68@gmail.com",
                mimeType: 'text/html'
        }
    }
}

def sendTelegramMessage(message) {
    script {
        sh """
            curl -s -X POST https://api.telegram.org/bot\${TELEGRAM_BOT_TOKEN}/sendMessage -d chat_id=\${TELEGRAM_CHAT_ID} -d parse_mode="HTML" -d text="${message}"
        """
    }
}
