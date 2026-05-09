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
                script {
                    def approvalUrl = "${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/input/Approval/proceedEmpty"
                    def rejectUrl   = "${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/input/Approval/abort"

                    emailext(
                        to:       'nandhinimadhu599@gmail.com',
                        subject:  "⏳ Approval Needed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        mimeType: 'text/html',
                        body:     """
<html>
<body style="font-family:Arial,sans-serif; background:#f4f4f4; padding:20px;">
  <div style="max-width:500px; margin:auto; background:#fff; border-radius:10px;
              padding:30px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">

    <h2 style="color:#333;">🚀 Deployment Approval Required</h2>
    <p style="color:#555;">
      Build <strong>#${env.BUILD_NUMBER}</strong> of <strong>${env.JOB_NAME}</strong>
      has passed Selenium tests and is ready to deploy.
    </p>

    <table style="width:100%; margin:20px 0; background:#f9f9f9;
                  border-radius:8px; padding:12px; border:1px solid #eee;">
      <tr><td style="color:#888; padding:4px 8px;">Target EC2</td>  <td><strong>${EC2_IP}</strong></td></tr>
      <tr><td style="color:#888; padding:4px 8px;">Image</td>       <td><strong>${IMAGE_NAME}</strong></td></tr>
      <tr><td style="color:#888; padding:4px 8px;">Container</td>   <td><strong>${CONTAINER}</strong></td></tr>
    </table>

    <div style="text-align:center; margin:30px 0;">
      <a href="${approvalUrl}"
         style="background:#28a745; color:#fff; padding:14px 36px;
                border-radius:8px; text-decoration:none;
                font-size:16px; font-weight:bold; margin-right:12px;">
        ✅ Approve &amp; Deploy
      </a>
      &nbsp;
      <a href="${rejectUrl}"
         style="background:#dc3545; color:#fff; padding:14px 36px;
                border-radius:8px; text-decoration:none;
                font-size:16px; font-weight:bold;">
        ❌ Reject
      </a>
    </div>

    <p style="color:#aaa; font-size:12px; text-align:center;">
      Approval expires in <strong>30 minutes</strong>. No action = auto-aborted.
    </p>
  </div>
</body>
</html>
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
            emailext(
                to:       'nandhinimadhu599@gmail.com',
                subject:  "✅ Deployed Successfully: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body:     """
<html><body style="font-family:Arial,sans-serif; padding:20px;">
  <div style="max-width:500px; margin:auto; background:#fff; border-radius:10px;
              padding:30px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
    <h2 style="color:#28a745;">✅ Deployment Successful</h2>
    <p>Build <strong>#${env.BUILD_NUMBER}</strong> of <strong>${env.JOB_NAME}</strong>
       was deployed successfully to EC2 <strong>(${EC2_IP})</strong>.</p>
  </div>
</body></html>
                """
            )
        }
        failure {
            emailext(
                to:       'nandhinimadhu599@gmail.com',
                subject:  "❌ Build Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body:     """
<html><body style="font-family:Arial,sans-serif; padding:20px;">
  <div style="max-width:500px; margin:auto; background:#fff; border-radius:10px;
              padding:30px; box-shadow:0 2px 8px rgba(0,0,0,0.1);">
    <h2 style="color:#dc3545;">❌ Build Failed or Rejected</h2>
    <p>Build <strong>#${env.BUILD_NUMBER}</strong> of <strong>${env.JOB_NAME}</strong>
       has failed or was rejected.</p>
    <p>Console: <a href="${env.BUILD_URL}console">${env.BUILD_URL}console</a></p>
  </div>
</body></html>
                """
            )
        }
    }
}
