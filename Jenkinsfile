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

        stage('Approval: Deploy to AWS EC2') {
            steps {
                mail(
                    to:      'nandhinimadhu599@gmail.com',
                    subject: "Jenkins Approval Required: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    body:    """
Hi Team,

Build #${env.BUILD_NUMBER} of *${env.JOB_NAME}* has passed Selenium tests and is awaiting your approval to deploy.

Please review and approve/reject the deployment:
${env.BUILD_URL}input

Build URL : ${env.BUILD_URL}
Target EC2 : ${EC2_IP}
Image Name : ${IMAGE_NAME}
Container  : ${CONTAINER}

Thanks,
Jenkins
                    """
                )
                timeout(time: 30, unit: 'MINUTES') {
                    input(
                        message: 'Deploy to AWS EC2?',
                        ok:      'Approve & Deploy',
                        submitter: 'admin,dev-team'
                    )
                }
            }
        }

        stage('Deploy To AWS EC2') {
            steps {
                sshagent(credentials: ['ec2_key']) {
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
                reportDir:             'reports',
                reportFiles:           'extent-report.html',
                reportName:            'Extent Report',
                keepAll:               true,
                alwaysLinkToLastBuild: true,
                allowMissing:          true
            ])
        }
        success {
            mail(
                to:      'nandhinimadhu599@gmail.com',
                subject: "✅ Deployment Successful: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body:    """
Hi Team,

Build #${env.BUILD_NUMBER} of *${env.JOB_NAME}* was deployed successfully to EC2 (${EC2_IP}).

Build URL: ${env.BUILD_URL}

Thanks,
Jenkins
                """
            )
        }
        failure {
            mail(
                to:      'nandhinimadhu599@gmail.com',
                subject: "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body:    """
Hi Team,

Build #${env.BUILD_NUMBER} of *${env.JOB_NAME}* has FAILED or was rejected.

Check the console output for details:
${env.BUILD_URL}console

Thanks,
Jenkins
                """
            )
        }
    }
}
