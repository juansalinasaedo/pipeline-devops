def call() {
  
    stage('Compile'){
    	 script {
           env.TAREA = env.STAGE_NAME
          }
          sh 'mvn clean compile -e'
	}

    stage('Test'){
    	 script {
           env.TAREA = env.STAGE_NAME
         }
		sh 'mvn clean test -e'
	}

      
    stage('Jar'){
		script {
           env.TAREA = env.STAGE_NAME
        }
		sh 'mvn clean package -e'
		sleep 30
	}


      stage('SonarQube'){
		script {
           env.TAREA = env.STAGE_NAME
        }
		withSonarQubeEnv(installationName: 'sonar'){
			sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
		}
	}


      stage('uploadNexus') {
          script {
           env.TAREA = env.STAGE_NAME
          }
          nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
         
       }   
}

return this;