pipeline {
    agent any

    stages {

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

        stage('Run Selenium Tests') {
            steps {
                bat 'mvn clean test -f selenium-test/pom.xml'
            }
        }

    }

    post {
        success {
            echo 'Pipeline completed! Deployed and all tests passed.'
        }
        failure {
            echo 'Pipeline failed! Check logs above.'
        }
    }
}
