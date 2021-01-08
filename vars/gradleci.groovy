def call(String stageParam) {

    stages = ['buildAndTest', 'sonar', 'runJar', 'rest', 'nexusCI']

    _stage = stageParam ? stageParam.split(';') : stages
    // Se valida el stage que el usuario ingrese
    _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es valido")
        }
    }

    if(_stage.contains('buildAndTest')) {
        stage('buildAndTest') {
            sh "gradle clean build" 
        }
    }


    if(_stage.contains('sonar')) {
        stage('sonar') {
            env.LAST_STAGE_NAME = env.STAGE_NAME

            def scannerHome = tool 'sonar-scanner';
            withSonarQubeEnv('sonar') { 
                sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
            }
        }
    }

	   
    if(_stage.contains('runJar')) {
        stage('runJar') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh "nohup bash gradle bootRun &"
            sleep 20
        }
    }


    if(_stage.contains('rest')) {
        stage('rest') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            //sh 'curl -X GET "http://localhost:8081/rest/mscovid/test?msg=testing"'
            sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
        }
    }


    if(_stage.contains('nexusCI')) {
        stage('nexusCI') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        }
    }
    
}

return this;