pipeline {
    agent any

    environment {
        MAVEN_HOME = 'C:\\Users\\Nandhini Madhu\\AppData\\Roaming\\Code\\User\\globalStorage\\pleiades.java-extension-pack-jdk\\maven\\latest'
        JAVA_HOME  = 'C:\\Program Files\\Java\\jdk-24'
    }

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
                bat """
                    set PATH=%JAVA_HOME%\\bin;%MAVEN_HOME%\\bin;%PATH%
                    mvn clean test -f selenium-test/pom.xml
                """
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
