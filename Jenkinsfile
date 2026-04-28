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
                withCredentials([string(credentialsId: 'VERCEL_TOKEN', variable: 'VERCEL_TOKEN')]) {
                    bat """
                        vercel --prod --token=%VERCEL_TOKEN% --yes --global-config="%WORKSPACE%\\.vercel-config"
                    """
                }
            }
        }

    }
}
