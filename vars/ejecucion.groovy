def call(){
  
  pipeline {
    agent any

    parameters { 
        choice(name: 'herramientas', choices: ['gradle', 'maven'], description: 'Elección de herramienta de construcción para aplicación covid') 
        string(name: 'stage', defaultValue: '', description: 'Escribir stages a ejecutar en formato: stage1;stage2;stage3. Si stage es vacío, se ejecutarán todos los stages')
    } 
 
    stages {
        stage('Pipeline') {
            steps {
                script {
                 // env.TAREA = ''
                  println 'Herramientas de Ejecucion: ' + params.herramientas
                  println 'Stage: ' + params.stage

                  if (params.herramientas == 'gradle'){
                    //gradle.call()
                    gradle "${params.stages}"
                  } else {
                    maven "${params.stages}"
                    //maven.call()
                  }

                }
            }
        }
    }

    post {
            success {
                slackSend color: 'good', message: "[Juan Salinas][${env.JOB_NAME}][${params.herramientas}] Ejecución exitosa"
            }

            failure {
                slackSend color: 'danger', message: "[Juan Salinas][${env.JOB_NAME}][${params.herramientas}] Ejecución fallida en stage ${env.LAST_STAGE_NAME}"
            }
    }    

  }  

}

return this;