pipeline {

    agent any

    environment {
        RECIPIENT  = 'nandhinimadhu599@gmail.com'
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

        stage('Send Approval Mail') {
            steps {
                emailext(
                    to: "${RECIPIENT}",
                    subject: "Approval Required - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: """
                        <div style="border:1px solid #ccc; padding:15px; width:420px;">
                            <h2>Deployment Approval</h2>
                            <p><b>Build:</b> ${env.BUILD_NUMBER}</p>
                            <p><b>Status:</b> Selenium Tests Passed ✅</p>
                            <p>Jenkins Build:
                                <a href="${env.BUILD_URL}">Open Build</a>
                            </p>
                            <p>Report:
                                <a href="${env.BUILD_URL}Extent_Report/">View Extent Report</a>
                            </p>
                            <hr/>
                            <p>Open Jenkins and approve:</p>
                            <ul>
                                <li>Proceed → Deploy to AWS EC2</li>
                                <li>Abort → Stop Deployment</li>
                            </ul>
                        </div>
                    """,
                    attachLog: true
                )
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

        success {
            emailext(
                to: "${RECIPIENT}",
                subject: "✅ Deployment Successful - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <div style="border:1px solid #ccc; padding:15px; width:420px;">
                        <h2>✅ Deployment Successful</h2>
                        <p>Frontend deployed on AWS EC2 successfully.</p>
                        <p><b>Application URL:</b>
                            <a href="http://${EC2_IP}">http://${EC2_IP}</a>
                        </p>
                        <p><b>Jenkins Build:</b>
                            <a href="${env.BUILD_URL}">Open Build</a>
                        </p>
                        <p><b>Extent Report:</b>
                            <a href="${env.BUILD_URL}Extent_Report/">View Report</a>
                        </p>
                    </div>
                """
            )
        }

        failure {
            emailext(
                to: "${RECIPIENT}",
                subject: "❌ Pipeline Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <div style="border:1px solid #ccc; padding:15px; width:420px;">
                        <h2>❌ Pipeline Failed</h2>
                        <p>Check Jenkins for details.</p>
                        <p><b>Jenkins Build:</b>
                            <a href="${env.BUILD_URL}">Open Build</a>
                        </p>
                    </div>
                """
            )
        }

        aborted {
            emailext(
                to: "${RECIPIENT}",
                subject: "⚠️ Deployment Aborted - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body: """
                    <div style="border:1px solid #ccc; padding:15px; width:420px;">
                        <h2>⚠️ Deployment Aborted</h2>
                        <p>Deployment was aborted manually.</p>
                        <p><b>Jenkins Build:</b>
                            <a href="${env.BUILD_URL}">Open Build</a>
                        </p>
                    </div>
                """
            )
        }
    }
}
