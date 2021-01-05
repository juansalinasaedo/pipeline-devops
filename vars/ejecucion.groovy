def call(){
  
  pipeline {
    agent any

    /*parameters { 
        choice(name: 'herramientas', choices: ['gradle', 'maven'], description: 'Elección de herramienta de construcción para aplicación covid') 
        string(name: 'stage', defaultValue: '', description: '')
    } */
 
    stages {
        stage('Pipeline') {
            environment {
              LAST_STAGE_NAME = ''
            }

            steps {
                script {
                 // env.TAREA = ''
                  println 'Herramientas de Ejecucion: ' + params.herramientas
                  println 'Stage: ' + params.stage

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
                slackSend color: 'good', message: "[Juan Salinas][${env.JOB_NAME}][${params.buildtool}] Ejecución exitosa"
            }

            failure {
                slackSend color: 'danger', message: "[Juan Salinas][${env.JOB_NAME}][${params.buildtool}] Ejecución fallida en stage ${env.LAST_STAGE_NAME}"
            }
    }    

    }    
  }
}

return this;