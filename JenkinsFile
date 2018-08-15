pipeline {

    agent {
        label 'master'
    }

    tools {
        jdk 'jdk9'
    }

    stages {
        stage("gradle build") {
            steps {
                sh './gradlew build'
            }
        }

        stage("docker ") {
            steps {
                script {
                    docker.withRegistry('https://registry.hub.docker.com', 'docker-hub-credentials') {
                        def temp = docker.build('actoaps/chromium-pdf-gen', '.')
                        temp.push('1.0.${BUILD_NUMBER}')
                        temp.push('latest')
                    }
                }
            }
        }
    }
}