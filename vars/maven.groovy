def call() {

	stages = ['Compile', 'Test', 'Jar', 'SonarQube', 'uploadNexus']

	// Si stage es vacio se toman todos los stages
    _stage = params.stage ? params.stage.split(';') : stages

    // Se valida el stage ingresado
    _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es una opción válida.")
        }
    }
  
  	if(_stage.contains('Compile')) {
	    stage('Compile'){
	    	 env.LAST_STAGE_NAME = env.STAGE_NAME
	    	 sh 'mvn clean compile -e'
		}
	}	

    if(_stage.contains('Test')) {
	    stage('Test'){
	    	env.LAST_STAGE_NAME = env.STAGE_NAME
			sh 'mvn clean test -e'
		}
	}	

      
    if(_stage.contains('Jar')) {  
	    stage('Jar'){
			env.LAST_STAGE_NAME = env.STAGE_NAME
			sh 'mvn clean package -e'
			sleep 30
		}
	}


    if(_stage.contains('SonarQube')) {
	     stage('SonarQube'){
			env.LAST_STAGE_NAME = env.STAGE_NAME
			withSonarQubeEnv(installationName: 'sonar'){
				sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
			}
		}
	}


	if(_stage.contains('uploadNexus')) {
      stage('uploadNexus') {
          env.LAST_STAGE_NAME = env.STAGE_NAME
          nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
         
       }  
    }    
}

return this;