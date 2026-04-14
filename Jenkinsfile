def runCommand(String unixCommand, String windowsCommand) {
    if (isUnix()) {
        sh unixCommand
    } else {
        powershell windowsCommand
    }
}

pipeline {
    agent any

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    parameters {
        booleanParam(name: 'SKIP_TESTS', defaultValue: true, description: 'Skip Maven tests during packaging.')
        booleanParam(name: 'BUILD_DOCKER_IMAGES', defaultValue: false, description: 'Build Docker images for all services after packaging.')
    }

    environment {
        MAVEN_ARGS = '-B -ntp'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Tooling') {
            steps {
                script {
                    runCommand('java -version', 'java -version')
                    runCommand('mvn -version', 'mvn -version')
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def mvnGoal = params.SKIP_TESTS ? 'clean package -DskipTests' : 'clean verify'
                    runCommand("mvn ${env.MAVEN_ARGS} ${mvnGoal}", "mvn ${env.MAVEN_ARGS} ${mvnGoal}")
                }
            }
        }

        stage('Docker Images') {
            when {
                expression { return params.BUILD_DOCKER_IMAGES }
            }
            steps {
                script {
                    def images = [
                        [name: 'quantity-platform/eureka-server', dockerfile: 'eureka-server/Dockerfile'],
                        [name: 'quantity-platform/api-gateway', dockerfile: 'api-gateway/Dockerfile'],
                        [name: 'quantity-platform/authentication-service', dockerfile: 'authentication-service/Dockerfile'],
                        [name: 'quantity-platform/user-service', dockerfile: 'user-service/Dockerfile'],
                        [name: 'quantity-platform/quantity-measurement-service', dockerfile: 'quantity-measurement-service/Dockerfile'],
                        [name: 'quantity-platform/admin-server', dockerfile: 'admin-server/Dockerfile']
                    ]

                    for (image in images) {
                        def dockerBuild = "docker build -t ${image.name}:${env.BUILD_NUMBER} -f ${image.dockerfile} ."
                        runCommand(dockerBuild, dockerBuild)
                    }
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: false
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
            deleteDir()
        }
    }
}
