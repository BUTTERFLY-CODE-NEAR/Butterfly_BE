pipeline {
    agent any

    environment {
        BLUE_PORT = '8081'
        GREEN_PORT = '8082'
        DEPLOY_DIR = '/home/ubuntu/butterfly'
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github_token', branch: 'main', url: 'https://github.com/BUTTERFLY-CODE-NEAR/Butterfly_BE'
            }
        }

        stage('Prepare Configurations') {
            steps {
                script {
                    def configFiles = [
                        'application-build': 'application-build.properties',
                        'messages': 'messages.properties',
                        'application-secret': 'application-secret.properties',
                        'application-common': 'application-common.properties'
                    ]

                    configFiles.each { credId, fileName ->
                        withCredentials([file(credentialsId: credId, variable: 'FILE')]) {
                            sh "cp -f \$FILE src/main/resources/${fileName}"
                            sh "cp -f \$FILE ${DEPLOY_DIR}/${fileName}"
                        }
                    }
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build -Dspring.profiles.active=build -Dspring.profiles.include=common,secret'
            }
        }

        stage('Deploy to Green') {
            steps {
                script {
                    sh "pkill -f 'java.*${GREEN_PORT}' || true"

                    sh "cp build/libs/butterfly.jar ${DEPLOY_DIR}/butterfly-green.jar"

                    withCredentials([string(credentialsId: 'SECURITY_WHITELIST', variable: 'SECURITY_WHITELIST')]) {
                        sh """
                        nohup java -jar ${DEPLOY_DIR}/butterfly-green.jar \
                        --server.port=${GREEN_PORT} \
                        --spring.profiles.active=build \
                        --spring.profiles.include=common,secret \
                        --SECURITY_WHITELIST='${SECURITY_WHITELIST}' \
                        > ${DEPLOY_DIR}/green.log 2>&1 &
                        """
                    }
                }
            }
        }

        stage('Monitor Green') {
            steps {
                script {
                    sh "sleep 30"

                    sh "curl -f http://localhost:${GREEN_PORT}/actuator/health"
                }
            }
        }

        stage('Update Nginx Configuration') {
            steps {
                script {
                    sh "sudo sed -i 's/proxy_pass http:\\/\\/localhost:${BLUE_PORT};/proxy_pass http:\\/\\/localhost:${GREEN_PORT};/' /etc/nginx/sites-available/default"
                    sh "sudo nginx -s reload"
                }
            }
        }

        stage('Deploy to Blue') {
            steps {
                script {
                    sh "pkill -f 'java.*${BLUE_PORT}' || true"

                    sh "cp build/libs/butterfly.jar ${DEPLOY_DIR}/butterfly-blue.jar"

                    withCredentials([string(credentialsId: 'SECURITY_WHITELIST', variable: 'SECURITY_WHITELIST')]) {
                        sh """
                        nohup java -jar ${DEPLOY_DIR}/butterfly-blue.jar \
                        --server.port=${BLUE_PORT} \
                        --spring.profiles.active=build \
                        --spring.profiles.include=common,secret \
                        --SECURITY_WHITELIST='${SECURITY_WHITELIST}' \
                        > ${DEPLOY_DIR}/blue.log 2>&1 &
                        """
                    }
                }
            }
        }

        stage('Monitor Blue') {
            steps {
                script {
                    sh "sleep 30"

                    sh "curl -f http://localhost:${BLUE_PORT}/actuator/health"
                }
            }
        }
    }

    post {
        failure {
            echo "Deployment failed. Consider implementing rollback logic."
        }
    }
}