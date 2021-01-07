def call(stageOptions){

  //figlet 'gradle'

  def buildEjecutado = false;

  stage("Validar"){
    if (
       stageOptions.contains('build')  ||
       stageOptions.contains('test')   ||
       stageOptions.contains('sonar')  ||
       stageOptions.contains('run')    ||
       stageOptions.contains('rest')   || 
       stageOptions.contains('nexus')  || 
       (stageOptions =='')
       ) {
          echo "Ok, se continua con los stage, ya que ingreso parametros conocidos"
         } else {
          currentBuild.result = 'FAILURE'
          echo "No se puede ejecutar este pipeline, ya que no ingreso parametros conocidos"
         }    
  }

  stage("Build & Test"){
    env.TAREA = env.STAGE_NAME
    buildEjecutado = false;

    if(stageOptions.contains('build') || (stageOptions == '')) {
      sh "gradle clean build -x test" 
      buildEjecutado = true;
    }
    if ((stageOptions.contains('Test') || (stageOptions == '')) && (buildEjecutado) ) {
      sh "gradle clean build" 
    }
  }

  stage("sonar") {
    env.TAREA = env.STAGE_NAME
    if(!buildEjecutado) {
      currentBuild.result = 'FAILURE'
      echo "No puede ejecutar Sonar, no se ha ejecutado Build"
      buildEjecutado = false;
    }

    def scannerHome = tool 'sonar-scanner';
    withSonarQubeEnv('sonar') {
         sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
    }
  }

  stage("run"){
    env.TAREA = env.STAGE_NAME
    if (stageOptions.contains('run') || (stageOptions == '') && (buildEjecutado)){
      sh "nohup gradle bootRun &"
      sleep 20
    }
  }

  stage("rest"){
    env.TAREA = env.STAGE_NAME
    if (stageOptions.contains('run') || (stageOptions == '') && (buildEjecutado))
    sh "curl -X GET 'http://localhost:8081/rest/mscovid/test?msg=testing'"
  }

  stage("nexus"){
    env.TAREA = env.STAGE_NAME
    if (stageOptions.contains('nexus') || (stageOptions == '') && (buildEjecutado))
    nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
  }

}

return this;