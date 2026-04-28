pipeline {
    agent any

    stages {
        stage('Install Vercel') {
            steps {
                bat 'npm install -g vercel'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'vercel-token', variable: 'VERCEL_TOKEN')]) {
                    bat 'vercel --prod --token=%VERCEL_TOKEN% --yes'
                }
            }
        }
    }
}
