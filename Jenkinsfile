pipeline {
    agent any

    stages {
        stage('Clone') {
            steps {
                git credentialsId: 'codenear-butterfly-backend', branch: 'main', url: 'https://github.com/BUTTERFLY-CODE-NEAR/Butterfly_BE'
            }
        }

        stage('Prepare Configurations') {
            steps {
                script {
                    def configFiles = [
                        'application-build': 'application-build.properties',
                        'messages': 'messages.properties',
                        'application-secret': 'application-secret.properties',
                        'application-common': 'application-common.properties',
                        'application-test' : 'application-test.properties'
                    ]

                    configFiles.each { credId, fileName ->
                        withCredentials([file(credentialsId: credId, variable: 'FILE')]) {
                            sh "cp -f \$FILE src/main/resources/${fileName}"
                        }
                    }

                    sh "cp -rf /home/ubuntu/ignore/terms src/main/resources/static/"
                    sh "cp -rf /home/ubuntu/ignore/home src/main/resources/static/"
                    sh "cp -rf /home/ubuntu/ignore/sb-admin src/main/resources/static/"
                    sh "cp -rf /home/ubuntu/ignore/.well-known src/main/resources/static/"
                    sh "cp -rf /home/ubuntu/ignore/firebase src/main/resources/"
                }
                echo "파일 복사 완료"
            }

        }

        stage('Jar Build') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'JAVA_HOME', variable: 'javaHomePath')]) {
                        // 가져온 'javaHomePath' 변수를 사용하여 JAVA_HOME 환경 변수를 설정합니다.
                        withEnv(["JAVA_HOME=${javaHomePath}"]) {
                            sh './gradlew clean build -Dspring.profiles.active=build -Dspring.profiles.include=common,secret'
                        }
                    }
                }
            }
        }

        stage('Docker Operations') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                    // Docker Hub Login
                    sh 'echo "$DOCKER_PASSWORD" | docker login -u $DOCKER_USERNAME --password-stdin'

                    // Build Docker Image
                    sh "docker build -t ${DOCKER_USERNAME}/codenear-butterfly ."

                    // Push Docker Image
                    sh "docker push ${DOCKER_USERNAME}/codenear-butterfly"
                }
            }
        }

        stage('Deployment Blue Green') {
            steps {
                sshagent(['EC2_CREDENTIAL']) {
                    withCredentials([
                        string(credentialsId: 'AWS_IP', variable: 'AWS_IP'),
                        string(credentialsId: 'SSH_HOST', variable: 'SSH_HOST')
                    ]) {
                        sh '''
                            ssh -o StrictHostKeyChecking=no ${SSH_HOST}@${AWS_IP} ./deploy.sh
                            '''
                    }
                }
            }
        }
    }

    post {
        success {
            echo "Deployment success"
        }
        failure {
            echo "Deployment failed. Consider implementing rollback logic."
        }
    }
}