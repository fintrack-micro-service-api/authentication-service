pipeline {
    agent any

    tools {
        gradle 'Gradle'
        jdk 'JDK'
    }

    environment {
        DOCKER_REGISTRY = 'kimheang68'
        IMAGE_NAME = 'spring-boot-app'
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
                    echo "Testing Notifications !!!!"
                    echo "Hello Notifications !!!!"
                    sendTelegramMessage("🚀 Pipeline Started:\nJob Name: ${env.JOB_NAME}\nJob Description: ${env.JOB_DESCRIPTION}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        sh 'gradle clean build'
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        def errorMessage = "❌ Build stage <b> failed </b>:\n${e.getMessage()}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}\nConsole Output: ${env.BUILD_URL}console"
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
                            -Dsonar.projectKey=spring-boot-app \
                            -Dsonar.sources=src/main/java \
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
                        // Add your test commands here
                        echo "Running tests..."
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        sendTelegramMessage("❌ Test stage <b> failed </b>: ${e.message}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                        error("Test stage failed: ${e.message}")
                    }
                }
            }
        }

        stage('Check for Existing Container') {
            steps {
                script {
                    try {
                        // Add your container check and cleanup commands here
                        echo "Checking for existing containers..."
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
                        // Add your Docker build and push commands here
                        def buildNumber = currentBuild.number
                        def imageTag = "${DOCKER_REGISTRY}/${IMAGE_NAME}:${buildNumber}"
                        sh "docker build -t ${imageTag} ."

                        withCredentials([usernamePassword(credentialsId: 'docker-hub-cred',
                                passwordVariable: 'PASS', usernameVariable: 'USER')]) {
                            sh "echo \$PASS | docker login -u \$USER --password-stdin"
                            sh "docker push ${imageTag}"
                        }
                    } catch (Exception e) {
                        currentBuild.result = 'FAILURE'
                        sendTelegramMessage("❌ Build Image stage failed: ${e.message}\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
                        error("Build Image stage failed: ${e.message}")
                    }
                }
            }
        }

        // Add more stages as needed

    }

    post {
        always {
            emailext body: 'Check console output at $BUILD_URL to view the results.', subject: '${PROJECT_NAME} - Build #${BUILD_NUMBER} - $BUILD_STATUS', to: 'yan.sovanseyha@gmail.com'
        }
        success {
            sendTelegramMessage("✅ All stages succeeded\nVersion: ${BUILD_INFO}\nCommitter: ${COMMITTER}\nBranch: ${BRANCH}")
            emailext body: "<html><body><b>✅ All stages succeeded</b><br/>Version: ${BUILD_INFO}<br/>Committer: ${COMMITTER}<br/>Branch: ${BRANCH}<br/>Check console output at <a href='${BUILD_URL}'>${BUILD_URL}</a> to view the results.</body></html>",
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                to: 'yan.sovanseyha@gmail.com'  // Close the string here
        }
        failure {
            emailext body: "<html><body><b>❌ Pipeline failed</b><br/>Version: ${BUILD_INFO}<br/>Committer: ${COMMITTER}<br/>Branch: ${BRANCH}<br/>Check console output at <a href='${BUILD_URL}'>${BUILD_URL}</a> to view the results.</body></html>",
                subject: "${env.JOB_NAME} - Build #${env.BUILD_NUMBER} - ${currentBuild.currentResult}",
                to: 'yan.sovanseyha@gmail.com'  // Close the string here
        }
    }
}

