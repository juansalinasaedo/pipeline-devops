def call(){
  
  pipeline {
    agent any

    parameters { 
        choice(name: 'herramientas', choices: ['gradle', 'maven'], description: 'Elección de herramienta de construcción para aplicación covid') 
       // string(name: 'stage', defaultValue: '', description: '')
    }

    stages {
        stage('Pipeline') {
            steps {
                script {
                 // env.TAREA = ''
                  println 'Herramientas de Ejecucion: ' + params.herramientas

                  if (params.buildtool == 'gradle'){
                    gradle.call()
                  } else {
                    maven.call()
                  }

                }
            }
        }
    }


    post {
        success {
            slackSend color: 'good', message: "[Juan Salinas][pipeline-devops][${params.herramientas}] Ejecución exitosa."
        }
        failure {
            slackSend color: 'danger', message: "[Juan Salinas][pipeline-devops][${params.herramientas}] Ejecución fallida en stage ${STAGE_NAME}."
            }
        }
    }

    
  }
}

return this;