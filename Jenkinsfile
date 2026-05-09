pipeline {

    agent any

    environment {
        EC2_IP     = '43.204.249.251'
        IMAGE_NAME = 'frontend-app'
        CONTAINER  = 'frontend-container'
    }

    stages {

        stage('Run Selenium Tests') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('Approval') {
            steps {
                timeout(time: 24, unit: 'HOURS') {
                    input(
                        message: 'Approve Deployment To AWS EC2?',
                        ok: 'Deploy Now'
                    )
                }
            }
        }

        stage('Deploy To AWS EC2') {
            steps {
                sshagent(credentials: ['ec2-key']) {
                    sh """
                        scp -o StrictHostKeyChecking=no Dockerfile form.html ec2-user@${EC2_IP}:/home/ec2-user/
                        ssh -o StrictHostKeyChecking=no ec2-user@${EC2_IP} '
                            sudo docker stop ${CONTAINER} || true &&
                            sudo docker rm ${CONTAINER} || true &&
                            sudo docker build -t ${IMAGE_NAME} /home/ec2-user &&
                            sudo docker run -d -p 80:80 --name ${CONTAINER} ${IMAGE_NAME}
                        '
                    """
                }
            }
        }
    }

    post {
        always {
            publishHTML([
                reportDir: 'reports',
                reportFiles: 'extent-report.html',
                reportName: 'Extent Report',
                keepAll: true,
                alwaysLinkToLastBuild: true,
                allowMissing: true
            ])
        }
    }
}
