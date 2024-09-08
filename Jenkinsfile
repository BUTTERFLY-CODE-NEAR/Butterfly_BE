pipeline {
    agent any

    environment {
        GREEN_SERVER_IP = credentials('GREEN_SERVER_IP')
        BLUE_SERVER_IP = credentials('BLUE_SERVER_IP')
        ELB_LISTENER_ARN = credentials('ELB_LISTENER_ARN')
        GREEN_TARGET_GROUP_ARN = credentials('GREEN_TARGET_GROUP_ARN')
    }

    stages {
        stage('Checkout') {
            steps {
                git credentialsId: 'github-credentials', branch: 'main', url: 'https://github.com/BUTTERFLY-CODE-NEAR/Butterfly_BE'
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
                sh 'scp build/libs/your-app.jar ubuntu@${GREEN_SERVER_IP}:/home/ubuntu/butterfly/'
                // Green 서버에서 Spring Boot 애플리케이션 실행
                sh 'ssh ubuntu@${GREEN_SERVER_IP} "sudo systemctl restart butterfly.service"'
            }
        }

        stage('Switch Traffic to Green') {
            steps {
                // 트래픽을 Green 서버로 전환 (AWS ELB 사용 시 예시)
                sh 'aws elbv2 modify-listener --listener-arn ${ELB_LISTENER_ARN} --default-actions Type=forward,TargetGroupArn=${GREEN_TARGET_GROUP_ARN}'
            }
        }

        stage('Monitor Green') {
            steps {
                // Green 서버 상태 확인
                sh 'curl -f http://$GREEN_SERVER_IP:8080/actuator/health'
            }
        }

        stage('Cleanup Blue') {
            steps {
                // Blue 서버에서 애플리케이션 종료
                sh 'ssh ubuntu@${BLUE_SERVER_IP} "sudo systemctl stop butterfly.service"'
            }
        }
    }
}