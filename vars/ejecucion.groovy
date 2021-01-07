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
                  env.TAREA = ''
                  echo "1.-HERRAMIENTA SELECCIONADA: ${params.HERRAMIENTA}" 
                  echo "2.-PARAMETROS SELECCIONADOS: ${stage}"   
                  echo "3.-Running ${env.BUILD_ID} on ${env.JENKINS_URL}"   

                  if (params.herramientas == 'gradle'){
                    gradle.call(stage)
                  } else {
                    maven.call(stage)
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