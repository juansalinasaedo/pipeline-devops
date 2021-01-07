def call(stageOptions) {

	//figlet 'maven'

	def buildEjecutado = false;

	 stage("Validar"){

      if (
        stageOptions.contains('compile')  ||
        stageOptions.contains('test')     ||
        stageOptions.contains('jar')      ||
        stageOptions.contains('sonar')    ||
        stageOptions.contains('nexus')    || 
        (stageOptions =='')
        ) {
           echo "Ejecutando Stage..."
          } else {
           currentBuild.result = 'FAILURE'
           echo "Ejecución fallida, parametros desconocidos"
          }   
    }
   

  stage('compile') {
    env.TAREA = env.STAGE_NAME
    buildEjecutado = false;
    if (stageOptions.contains('compile') || (stageOptions == '')) {
      sh 'mvn clean compile -e'
    }
  }

  stage('test') {
    env.TAREA = env.STAGE_NAME
    if (stageOptions.contains('test') || (stageOptions == '')) {
      sh 'mvn clean test -e'
    }
  }

  stage('jar'){
    env.TAREA = env.STAGE_NAME
    if (stageOptions.contains('jar') || (stageOptions == '')){
      sh 'mvn clean package -e' 
      buildEjecutado = true;
    }
  }

  stage('sonar') {
    env.TAREA = env.STAGE_NAME
    if (!buildEjecutado){
      currentBuild.result = 'FAILURE'
      cho "No puede ejecutar Sonar, no se ha ejecutado Build"
      buildEjecutado = false;
    }

    def scannerHome = tool 'sonar-scanner';
    withSonarQubeEnv('sonar') {
      if((stageOptions.contains('sonar') || (stageOptions == '')) && (buildEjecutado))
         sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
    }
  }

  stage('nexus') {
    env.TAREA = env.STAGE_NAME
    if ((stageOptions.contains('nexus') || (stageOptions == '')) && (buildEjecutado)) 
      nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
    
  }
}

return this;