def call(){
  
        def descarga = false;
        figlet 'Gradle CD'

        stage("downloadNexus"){    
            env.TAREA =  env.STAGE_NAME       
            sh 'curl -X GET -u admin:admin http://localhost:8081/repository/test-nexus/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O' 
            descarga = true;
        }


        stage("runDownloadedJar"){    
            env.TAREA =  env.STAGE_NAME   
            if (descarga) {
                sh "java -jar DevOpsUsach2020-0.0.1.jar &"
                sleep 20   
            }             
        }  

        stage("rest"){
            env.TAREA =  env.STAGE_NAME 
            if (descarga) 
                sh 'curl -X GET "http://localhost:8081/rest/mscovid/test?msg=testing"' 
        }  

        stage("nexusCD"){    
            env.TAREA =  env.STAGE_NAME   
            if (descarga)          
                nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: 'release-v1.0.0']]]                     
        }                    

}

return this;