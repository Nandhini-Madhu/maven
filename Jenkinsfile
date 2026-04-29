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
                emailext(
                    to: "${RECIPIENT}",
                    subject: "Approval Required - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                    mimeType: 'text/html',
                    body: """
                        <div style="border:1px solid #ccc; padding:15px; width:420px;">

                            <h3>Deployment Approval</h3>

                            <p>Build: ${env.BUILD_NUMBER}</p>
                            <p>Status: Tests Completed</p>

                            <p>
                                Report:
                                <a href="${env.BUILD_URL}Extent_Report/">View Report</a>
                            </p>

                            <hr/>

                            <p><b>Choose an action:</b></p>

                            <p>
                                <a href="${env.BUILD_URL}" 
                                   style="padding:8px 15px; border:1px solid green; text-decoration:none;">
                                   Approve
                                </a>

                                &nbsp;&nbsp;

                                <a href="${env.BUILD_URL}" 
                                   style="padding:8px 15px; border:1px solid red; text-decoration:none;">
                                   Abort
                                </a>
                            </p>

                            <p style="margin-top:15px;">
                                Open Jenkins and click <b>Proceed</b> or <b>Abort</b>.
                            </p>

                        </div>
                    """,
                    attachmentsPattern: 'reports/extent-report.html',
                    attachLog: true
                )
            }
        }

        stage('Approval') {
            steps {
                timeout(time: 24, unit: 'HOURS') {
                    input message: 'Approve deployment?',
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

        always {
            publishHTML([
                reportDir: 'reports',
                reportFiles: 'extent-report.html',
                reportName: 'Extent Report'
            ])
        }

        success {
            emailext(
                subject: "Deployment Successful - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Deployment completed successfully.\n\nReport: ${env.BUILD_URL}Extent_Report/",
                to: "${RECIPIENT}"
            )
        }

        failure {
            emailext(
                subject: "Pipeline Failed - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed.\nCheck: ${env.BUILD_URL}",
                to: "${RECIPIENT}"
            )
        }

        aborted {
            emailext(
                subject: "Deployment Aborted - ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Deployment was aborted.",
                to: "${RECIPIENT}"
            )
        }
    }
}
