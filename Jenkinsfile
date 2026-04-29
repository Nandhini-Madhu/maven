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

        stage('Send Test Report Mail') {
            steps {
                emailext(
                    subject: "Test Results - Action Required",
                    body: "Tests executed. Please review and approve deployment.",
                    to: "${RECIPIENT}",
                    attachLog: true
                )
            }
        }

        stage('Approval') {
            steps {
                input message: 'Approve deployment?', ok: 'Deploy Now'
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
                subject: "Deployment Successful",
                body: "Deployment completed successfully after approval.",
                to: "${RECIPIENT}"
            )
        }

        failure {
            emailext(
                subject: "Pipeline Failed",
                body: "Build/Test/Deployment failed. Check Jenkins logs.",
                to: "${RECIPIENT}"
            )
        }
    }
}
