def call(){
  
  pipeline {
    agent any

   /*parameters { 
        choice(name: 'herramientas', choices: ['gradle', 'maven'], description: 'Elección de herramienta de construcción para aplicación covid') 
        string(name: 'stage', defaultValue: '', description: 'Escribir stages a ejecutar en formato: stage1;stage2;stage3. Si stage es vacío, se ejecutarán todos los stages')
    } */
 
    stages {
        stage('Pipeline') {
            steps {
                script {
                //segun el valor del parametro se debe llamar a gradle o maven
                sh 'env'
                env.TAREA = '' 
                echo "-RUNNING ${env.BUILD_ID} on ${env.JENKINS_URL}" 
                echo "-GIT_BRANCH ${env.GIT_BRANCH}"   

                                          
                if (env.GIT_BRANCH == "develop" || env.GIT_BRANCH == "feature"){
                        gradle_ci.call();
                } else if (env.GIT_BRANCH.contains("release")){  
                        gradle_cd.call();                 
                } else {
                    echo " La rama <${env.GIT_BRANCH}> no se proceso" 
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