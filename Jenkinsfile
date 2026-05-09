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
                    // Build direct API URLs — clicking these approves/rejects without loading any page
                    def approveUrl = "http://${EC2_IP}:8080/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/input/Approval/proceedEmpty"
                    def rejectUrl  = "http://${EC2_IP}:8080/job/${env.JOB_NAME}/${env.BUILD_NUMBER}/input/Approval/abort"

                    // Send HTML email with buttons
                    emailext(
                        to:       'nandhinimadhu599@gmail.com',
                        subject:  "⏳ Approval Needed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        mimeType: 'text/html',
                        body:     """
<html>
<body style="margin:0;padding:0;background:#f0f2f5;font-family:Arial,sans-serif;">
<div style="max-width:480px;margin:40px auto;background:#ffffff;
            border-radius:12px;overflow:hidden;
            box-shadow:0 4px 16px rgba(0,0,0,0.12);">

  <!-- Header -->
  <div style="background:#1a1a2e;padding:24px 32px;">
    <h2 style="color:#6ef7c4;margin:0;font-size:20px;">🚀 Deployment Approval</h2>
    <p style="color:#aaa;margin:6px 0 0;font-size:13px;">Jenkins CI/CD Pipeline</p>
  </div>

  <!-- Body -->
  <div style="padding:28px 32px;">
    <p style="color:#333;font-size:15px;margin:0 0 16px;">
      Build <strong>#${env.BUILD_NUMBER}</strong> of
      <strong>${env.JOB_NAME}</strong> has
      <span style="color:#28a745;font-weight:bold;">passed Selenium tests</span>
      and is waiting for your approval to deploy.
    </p>

    <!-- Info table -->
    <table style="width:100%;background:#f8f9fa;border-radius:8px;
                  padding:14px;border:1px solid #e9ecef;
                  border-collapse:separate;border-spacing:0;margin-bottom:24px;">
      <tr>
        <td style="color:#888;font-size:13px;padding:5px 10px;">Target EC2</td>
        <td style="font-weight:bold;font-size:13px;padding:5px 10px;">${EC2_IP}</td>
      </tr>
      <tr>
        <td style="color:#888;font-size:13px;padding:5px 10px;">Image</td>
        <td style="font-weight:bold;font-size:13px;padding:5px 10px;">${IMAGE_NAME}</td>
      </tr>
      <tr>
        <td style="color:#888;font-size:13px;padding:5px 10px;">Container</td>
        <td style="font-weight:bold;font-size:13px;padding:5px 10px;">${CONTAINER}</td>
      </tr>
      <tr>
        <td style="color:#888;font-size:13px;padding:5px 10px;">Build #</td>
        <td style="font-weight:bold;font-size:13px;padding:5px 10px;">${env.BUILD_NUMBER}</td>
      </tr>
    </table>

    <!-- Buttons -->
    <p style="color:#555;font-size:14px;margin:0 0 18px;">
      Click a button below to respond <strong>directly from this email</strong>:
    </p>
    <div style="text-align:center;">
      <a href="${approveUrl}"
         style="display:inline-block;background:#28a745;color:#ffffff;
                padding:14px 32px;border-radius:8px;text-decoration:none;
                font-size:15px;font-weight:bold;margin-right:12px;
                letter-spacing:0.3px;">
        ✅ Approve &amp; Deploy
      </a>
      <a href="${rejectUrl}"
         style="display:inline-block;background:#dc3545;color:#ffffff;
                padding:14px 32px;border-radius:8px;text-decoration:none;
                font-size:15px;font-weight:bold;letter-spacing:0.3px;">
        ❌ Reject
      </a>
    </div>
  </div>

  <!-- Footer -->
  <div style="background:#f8f9fa;padding:14px 32px;border-top:1px solid #e9ecef;
              text-align:center;">
    <p style="color:#aaa;font-size:12px;margin:0;">
      ⏱ This approval expires in <strong>30 minutes</strong>.
      No response = pipeline auto-aborted.
    </p>
  </div>

</div>
</body>
</html>
                        """
                    )

                    // Pause pipeline and wait for approval
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
            emailext(
                to:       'nandhinimadhu599@gmail.com',
                subject:  "✅ Deployed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body:     """
<html><body style="font-family:Arial,sans-serif;background:#f0f2f5;padding:30px;">
<div style="max-width:480px;margin:auto;background:#fff;border-radius:12px;
            padding:30px;box-shadow:0 4px 16px rgba(0,0,0,0.1);">
  <h2 style="color:#28a745;">✅ Deployment Successful</h2>
  <p style="color:#555;">Build <strong>#${env.BUILD_NUMBER}</strong> of
  <strong>${env.JOB_NAME}</strong> was deployed to EC2 <strong>(${EC2_IP})</strong>.</p>
</div>
</body></html>
                """
            )
        }
        failure {
            emailext(
                to:       'nandhinimadhu599@gmail.com',
                subject:  "❌ Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                mimeType: 'text/html',
                body:     """
<html><body style="font-family:Arial,sans-serif;background:#f0f2f5;padding:30px;">
<div style="max-width:480px;margin:auto;background:#fff;border-radius:12px;
            padding:30px;box-shadow:0 4px 16px rgba(0,0,0,0.1);">
  <h2 style="color:#dc3545;">❌ Build Failed or Rejected</h2>
  <p style="color:#555;">Build <strong>#${env.BUILD_NUMBER}</strong> of
  <strong>${env.JOB_NAME}</strong> failed or was rejected.</p>
  <p><a href="${env.BUILD_URL}console" style="color:#007bff;">View Console Log</a></p>
</div>
</body></html>
                """
            )
        }
    }
}
