def call(){
  
	pipeline {
    agent any
	
	parameters { choice(name: 'devtool', choices: ['maven', 'gradle'], description: 'Elección de herramienta de construcción para aplicación covid') }

    stages {
        stage('Pipeline') {
            steps {
                script {
					def STAGE_NAME = ''
					params.devtool
				
					def pipe = (params.devtool == 'gradle') ? load("gradle.groovy") : load("maven.groovy")
					pipe.call()
				}
           }
        }
    }
	
	post {
		success {
			slackSend color: 'good', message: "[Juan Salinas][pipeline-maven-gradle][${params.devtool}] Ejecución exitosa."
		}
		failure {
			slackSend color: 'danger', message: "[Juan Salinas][pipeline-maven-gradle][${params.devtool}] Ejecución fallida en stage ${STAGE_NAME}."
			}
		}
	}

}

return this;