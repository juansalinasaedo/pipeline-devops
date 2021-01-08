def call(){
  
  pipeline {
    agent any

   parameters { 
        choice(name: 'herramientas', choices: ['gradle', 'maven'], description: 'Elección de herramienta de construcción para aplicación covid') 
        string(name: 'stage', defaultValue: '', description: 'Escribir stages a ejecutar en formato: stage1;stage2;stage3. Si stage es vacío, se ejecutarán todos los stages')
    } 
 
    stages {
        stage('Pipeline') {

           environment {
             LAST_STAGE_NAME = ''
           }

            steps {
                script {
                  
                  println "BRANCH_NAME: " + env.BRANCH_NAME
                  pipelineType = env.BRANCH_NAME ==~ /release-v(\d{1,3})\-(\d{1,3})\-(\d{1,3})/ ? "CD" : "CI"
                  figlet params.herramientas
                  figlet pipelineType

                  if (params.herramientas == 'gradle') { 
                      if(pipelineType == "CI") {
                          gradle_ci.call(params.stage)
                      } else { 
                          gradle_cd.call(params.stage)
                         }
                      } else {
                          maven.call()
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