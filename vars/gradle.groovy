def call(){

	stage('Build & Test'){
		//STAGE_NAME = 'Build & test'
		env.TAREA = env.STAGE_NAME
		sh "./gradlew clean build"
	}	

	stage('Sonar'){
		//STAGE_NAME = 'sonar'
		env.TAREA = env.STAGE_NAME
		//Generar instancia de tipo tool del scanner
		//Va el nombre de la instancia en Jenkins>Global tool config.
		//def scannerHome = tool 'sonar-scanner';
		//Corresponde a lo  configurado en Jenkins>Configurar el sistema
		/*withSonarQubeEnv('sonar') {
			sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
		}*/
		stage('SonarQube Analisis') {
			env.TAREA = env.STAGE_NAME
			def scannerHome = tool 'sonar-scanner';
			withSonarQubeEnv('Sonar-Server') {
				sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
			}
		}
	}

	stage('Run'){
		//STAGE_NAME = 'Run'
		env.TAREA = env.STAGE_NAME
		//sh "nohup bash gradlew bootRun &"
		sh "nohup gradle bootRun &"
		sleep 30
	}

	stage('Rest'){
		//STAGE_NAME = 'Test'
		env.TAREA = env.STAGE_NAME
		sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
	}

	stage('Nexus'){
		//STAGE_NAME = 'Nexus'
		env.TAREA = env.STAGE_NAME
		nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/home/juan/ejemplo-maven-1/build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
	}
}

return this;