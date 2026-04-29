pipeline {
    agent any 
    environment {
        MAVEN_HOME = 'C:\\Users\\Nandhini Madhu\\AppData\\Roaming\\Code\\User\\globalStorage\\pleiades.java-extension-pack-jdk\\maven\\latest'
        JAVA_HOME  = 'C:\\Program Files\\Java\\jdk-24'
        RECIPIENT  = 'nandhinimadhu599@gmail.com'
    }
    stages {
        stage('Run Selenium Tests') {
            steps {
                bat """
                    set PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%
                    mvn clean test
                """
            }
        }

        stage('Send Approval Mail') {
            steps {
                script {
                    def jobName    = env.JOB_NAME.replace('/', '/job/')
                    def approveUrl = "${env.JENKINS_URL}job/${jobName}/${env.BUILD_NUMBER}/input/Approval/proceedEmpty"
                    def abortUrl   = "${env.JENKINS_URL}job/${jobName}/${env.BUILD_NUMBER}/input/Approval/abort"

                    emailext(
                        to: "${RECIPIENT}",
                        subject: "Action Required: Approve Deployment - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        mimeType: 'text/html',
                        body: """
                            <div style="font-family:Arial,sans-serif;max-width:500px;margin:auto;
                                        border:1px solid #ddd;border-radius:8px;overflow:hidden;">
                                <div style="background:#1a1a2e;padding:20px;text-align:center;">
                                    <h2 style="color:#6ef7c4;margin:0;">Deployment Approval</h2>
                                </div>
                                <div style="padding:24px;">
                                    <p style="font-size:15px;color:#333;">
                                        Selenium tests have completed for build <b>#${env.BUILD_NUMBER}</b>.
                                        Please review the attached log and approve or reject the deployment.
                                    </p>
                                    <table style="width:100%;margin-top:24px;">
                                        <tr>
                                            <td style="text-align:center;padding:8px;">
                                                <a href="${approveUrl}"
                                                   style="background:#28a745;color:#fff;padding:12px 28px;
                                                          text-decoration:none;border-radius:6px;
                                                          font-size:15px;font-weight:bold;display:inline-block;">
                                                    ✅ YES &nbsp;– Deploy Now
                                                </a>
                                            </td>
                                            <td style="text-align:center;padding:8px;">
                                                <a href="${abortUrl}"
                                                   style="background:#dc3545;color:#fff;padding:12px 28px;
                                                          text-decoration:none;border-radius:6px;
                                                          font-size:15px;font-weight:bold;display:inline-block;">
                                                    ❌ NO &nbsp;– Abort
                                                </a>
                                            </td>
                                        </tr>
                                    </table>
                                    <p style="margin-top:24px;font-size:12px;color:#999;text-align:center;">
                                        This approval link will expire in <b>24 hours</b>.<br/>
                                        You must be logged into Jenkins for the link to work.
                                    </p>
                                </div>
                            </div>
                        """,
                        attachLog: true
                    )
                }
            }
        }

        stage('Approval') {
            steps {
                timeout(time: 24, unit: 'HOURS') {
                    input id: 'Approval',
                          message: 'Approve deployment?',
                          ok: 'Deploy Now'
                }
            }
        }

        stage('Install Vercel') {
            steps {
                bat 'npm install -g vercel'
            }
        }

        stage('Deploy to Vercel') {
            steps {
                withCredentials([string(credentialsId: 'vercel-token', variable: 'VERCEL_TOKEN')]) {
                    bat """
                        set PATH=%APPDATA%\\npm;%PATH%
                        vercel --prod --token=%VERCEL_TOKEN% --yes --global-config="%WORKSPACE%\\.vercel-config"
                    """
                }
            }
        }
    }

    post {
        success {
            emailext(
                subject: " Deployment Successful - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Deployment completed successfully after approval.",
                to: "${RECIPIENT}"
            )
        }
        failure {
            emailext(
                subject: " Pipeline Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build/Test/Deployment failed. Check Jenkins logs.",
                to: "${RECIPIENT}"
            )
        }
        aborted {
            emailext(
                subject: "Deployment Aborted - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Deployment was rejected via email approval.",
                to: "${RECIPIENT}"
            )
        }
    }
}
