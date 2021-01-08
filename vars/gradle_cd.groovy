def call(String stageParam) {

    stages = ['downloadNexus', 'runDownloadedJar', 'rest', 'nexusCD']

   _stage = stageParam ? stageParam.split(';') : stages
    // Se valida el stage que el usuario ingrese
    _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es valido")
        }
    }

	if(_stage.contains('downloadNexus')) {
        stage('downloadNexus') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "curl -X GET -U admin:admin http://localhost:8081/repository/test-repo/com/devopsusach2020/DevOpsUsach2020/0.0.1/DevOpsUsach2020-0.0.1.jar -O"
            sh "ls -ltr"
        }
    }

    if(_stage.contains('runDownloadedJar')) {
        stage('runDownloadedJar') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "nohup java -jar DevOpsUsach2020-0.0.1.jar --server.port=8083 &"
            sleep 20
        }
    }


    if(_stage.contains('rest')) {
        stage('rest') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "curl -X GET 'http://localhost:8083/rest/mscovid/test?msg=testing'"
            //sh 'curl -X GET "http://localhost:8081/rest/mscovid/test?msg=testing"'
        }
    }


    if(_stage.contains('nexusCD')) {
        stage('nexusCD') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: 'v1-0-0']]]
        }
    }


}

return this;