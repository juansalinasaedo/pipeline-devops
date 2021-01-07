def call(){
  
  pipeline {
    agent any

   /* parameters { 
        choice(name: 'herramientas', choices: ['gradle', 'maven'], description: 'Elección de herramienta de construcción para aplicación covid') 
        string(name: 'stage', defaultValue: '', description: 'Escribir stages a ejecutar en formato: stage1;stage2;stage3. Si stage es vacío, se ejecutarán todos los stages')
    } */
 
    stages {
        stage('Pipeline') {
            steps {
                script {
                  
                  //Segun el valor del parametro se inovacara a gradle o maven
                  sh 'env'
                  env.TAREA = ''
                  /*echo "1.-HERRAMIENTA SELECCIONADA: ${params.HERRAMIENTA}" 
                  echo "2.-PARAMETROS SELECCIONADOS: ${stage}"   
                  echo "3.-Running ${env.BUILD_ID} on ${env.JENKINS_URL}" */
                  echo "-RUNNING ${env.BUILD_ID} on ${env.JENKINS_URL}" 
                  echo "-GIT_BRANCH ${env.GIT_BRANCH}"   

                  if (env.GIT_BRANCH == "develop" || env.GIT_BRANCH == "feature"){
                    gradle.call();
                  } else if (env.GIT_BRANCH.contains("release")){
                    maven.call();
                  } else {
                    echo "No se ha procesado la rama ${env.GIT_BRANCH}"
                  }

                }
            }
        }
    }

    post {
            success {
               // slackSend color: 'good', message: "[Juan Salinas][${env.JOB_NAME}][${params.herramientas}] Ejecución exitosa"
               slackSend color: 'good', message: "[Juan Salinas][${env.JOB_NAME}][${env.GIT_BRANCH}] Ejecución exitosa"
            }

            failure {
               // slackSend color: 'danger', message: "[Juan Salinas][${env.JOB_NAME}][${params.herramientas}] Ejecución fallida en stage ${env.LAST_STAGE_NAME}"
               slackSend color: 'danger', message: "[Juan Salinas][${env.JOB_NAME}][${env.GIT_BRANCH}] Ejecución fallida en stage ${env.TAREA}"
            }
    }    

  }  

}

return this;