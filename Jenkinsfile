pipeline {
    agent any
    options {
        disableConcurrentBuilds()
    }
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
                script {
                    def approveUrl = "http://${EC2_IP}:8080/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/input/Approval/proceedEmpty"
                    def rejectUrl  = "http://${EC2_IP}:8080/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/input/Approval/abort"

                    mail(
                        to:      'nandhinimadhu599@gmail.com',
                        subject: "Approval Needed: ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                        body:    """\
Hi Nandhini,

Build #${env.BUILD_NUMBER} of ${env.JOB_NAME} has passed all Selenium tests and is ready to deploy.

Job       : ${env.JOB_NAME}
Build     : #${env.BUILD_NUMBER}
Target EC2: ${EC2_IP}
Image     : ${IMAGE_NAME}
Container : ${CONTAINER}

To APPROVE and deploy, open this link:
${approveUrl}

To REJECT and stop, open this link:
${rejectUrl}

This approval will expire in 30 minutes.

Thanks,
Jenkins
                        """
                    )

                    timeout(time: 30, unit: 'MINUTES') {
                        input(
                            id:      'Approval',
                            message: 'Deploy to AWS EC2?',
                            ok:      'Approve & Deploy'
                        )
                    }
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
                            sudo docker rm   ${CONTAINER} || true &&
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
                subject: "SUCCESS: ${env.JOB_NAME} Build #${env.BUILD_NUMBER} Deployed",
                body:    """\
Hi Nandhini,

Build #${env.BUILD_NUMBER} of ${env.JOB_NAME} was deployed successfully.

EC2 IP  : ${EC2_IP}
App URL : http://${EC2_IP}

Thanks,
Jenkins
                """
            )
        }
        failure {
            mail(
                to:      'nandhinimadhu599@gmail.com',
                subject: "FAILED: ${env.JOB_NAME} Build #${env.BUILD_NUMBER}",
                body:    """\
Hi Nandhini,

Build #${env.BUILD_NUMBER} of ${env.JOB_NAME} has FAILED or was rejected.

Console log: http://${EC2_IP}:8080/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/console

Thanks,
Jenkins
                """
            )
        }
    }
}
