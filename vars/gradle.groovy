def call(){
	stages = ['Build & Test', 'Sonar', 'Run', 'Rest', 'Nexus']

	// Si stage es vacio se toman todos los stages
    _stage = params.stage ? params.stage.split(';') : stages

    // Se valida el stage ingresado
    _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es una opción válida.")
        }
    }

    if(_stage.contains('Build & Test')) {
		stage('Build & Test'){
			//STAGE_NAME = 'Build & test'
			//env.TAREA = env.STAGE_NAME
			sh "./gradlew clean build"
		}	
	}

	if(_stage.contains('Sonar')) {
		stage('Sonar'){
			env.LAST_STAGE_NAME = env.STAGE_NAME
			def scannerHome = tool 'sonar-scanner';

				withSonarQubeEnv('Sonar-Server') {
					sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
				}
			
		}
	}


	if(_stage.contains('Run')) {
		stage('Run'){
			//STAGE_NAME = 'Run'
			env.LAST_STAGE_NAME = env.STAGE_NAME
			//sh "nohup bash gradlew bootRun &"
			sh "nohup gradle bootRun &"
			sleep 30
		}
	}


	if(_stage.contains('Rest')) {
		stage('Rest'){
			//STAGE_NAME = 'Test'
			env.LAST_STAGE_NAME = env.STAGE_NAME
			sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
		}
	}

	if(_stage.contains('Nexus')) {
		stage('Nexus'){
			//STAGE_NAME = 'Nexus'
			env.LAST_STAGE_NAME = env.STAGE_NAME
			nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/home/juan/ejemplo-maven-1/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
		}
	}
}

return this;