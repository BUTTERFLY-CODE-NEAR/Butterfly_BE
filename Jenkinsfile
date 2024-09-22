pipeline {
    agent any

    environment {
        BLUE_SERVER_IP = credentials('BLUE_SERVER_IP')
        GREEN_SERVER_IP = credentials('GREEN_SERVER_IP')
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github-token', branch: 'main', url: 'https://github.com/BUTTERFLY-CODE-NEAR/Butterfly_BE'
            }
        }

        stage('Prepare Configurations') {
            steps {
                // Secret files 불러오기
                withCredentials([
                    file(credentialsId: 'application-build', variable: 'APPLICATION_BUILD'),
                    file(credentialsId: 'messages', variable: 'MESSAGES'),
                    file(credentialsId: 'application-secret', variable: 'APPLICATION_SECRET'),
                    file(credentialsId: 'application-common', variable: 'APPLICATION_COMMON')
                ]) {
                    // 서버에 Secret 파일 복사
                    sh 'scp $APPLICATION_BUILD ubuntu@${GREEN_SERVER_IP}/home/ubuntu/butterfly/application-build.properties'
                    sh 'scp $MESSAGES ubuntu@${GREEN_SERVER_IP}/home/ubuntu/butterfly/messages.properties'
                    sh 'scp $APPLICATION_SECRET ubuntu@${GREEN_SERVER_IP}/home/ubuntu/butterfly/application-secret.properties'
                    sh 'scp $APPLICATION_COMMON ubuntu@${GREEN_SERVER_IP}/home/ubuntu/butterfly/application-common.properties'

                    // Blue 서버에도 동일한 방식으로 파일 복사
                    sh 'scp $APPLICATION_BUILD ubuntu@${BLUE_SERVER_IP}/home/ubuntu/butterfly/application-build.properties'
                    sh 'scp $MESSAGES ubuntu@${BLUE_SERVER_IP}/home/ubuntu/butterfly/messages.properties'
                    sh 'scp $APPLICATION_SECRET ubuntu@${BLUE_SERVER_IP}/home/ubuntu/butterfly/application-secret.properties'
                    sh 'scp $APPLICATION_COMMON ubuntu@${BLUE_SERVER_IP}/home/ubuntu/butterfly/application-common.properties'
                }
            }
        }

        stage('Build') {
            steps {
                sh './gradlew clean build'
            }
        }

        stage('Deploy to Green') {
            steps {
                // Green 서버로 JAR 파일 복사
                sh 'scp build/libs/butterfly.jar ubuntu@${GREEN_SERVER_IP}/home/ubuntu/butterfly/'
                // Green 서버에서 Spring Boot 애플리케이션 실행 (8082 포트)
                sh 'ssh ubuntu@${GREEN_SERVER_IP} "sudo systemctl stop butterfly.service; nohup java -jar /home/ubuntu/butterfly/butterfly.jar --server.port=8082 --spring.profiles.active=build > /dev/null 2>&1 &"'
            }
        }

        stage('Monitor Green') {
            steps {
                // Green 서버 상태 확인 (8082 포트)
                sh 'curl -f http://${GREEN_SERVER_IP}/actuator/health'
            }
        }

        stage('Deploy to Blue') {
            steps {
                // Blue 서버로 JAR 파일 복사
                sh 'scp build/libs/butterfly.jar ubuntu@${BLUE_SERVER_IP}/home/ubuntu/butterfly/'
                // Blue 서버에서 Spring Boot 애플리케이션 실행 (8081 포트)
                sh 'ssh ubuntu@${BLUE_SERVER_IP} "sudo systemctl stop butterfly.service; nohup java -jar /home/ubuntu/butterfly/butterfly.jar --server.port=8081 --spring.profiles.active=build > /dev/null 2>&1 &"'
            }
        }

        stage('Monitor Blue') {
            steps {
                // Blue 서버 상태 확인 (8081 포트)
                sh 'curl -f http://${BLUE_SERVER_IP}/actuator/health'
            }
        }

        stage('Update Nginx and Cleanup') {
            steps {
                // Nginx 설정에서 Green 서버를 활성화하고 Blue 서버를 비활성화
                sh 'ssh ubuntu@${GREEN_SERVER_IP} "sudo nginx -s reload"'
                sh 'ssh ubuntu@${BLUE_SERVER_IP} "sudo nginx -s reload"'
            }
        }
    }
}