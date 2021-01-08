def call() {

  stages = ['compile', 'test', 'jar', 'SonarQube', 'uploadNexus']

   _stage = params.stage ? params.stage.split(';') : stages
   _stage.each { el ->
        if (!stages.contains(el)) {
            throw new Exception("Stage: $el no es valido")
        }
    }


  if(_stage.contains('compile')) {
        stage('compile') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh 'mvn clean compile -e'
        }
    }
	
  
  if(_stage.contains('test')) {
        stage('test') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh 'mvn clean test -e'
        }
    }
  

  if(_stage.contains('jar')) {
        stage('jar') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            sh 'mvn clean package -e' 
        }
    }


  if(_stage.contains('SonarQube')) {
        stage('SonarQube') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            withSonarQubeEnv(installationName: 'sonar') {
                sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
            }
        }
    }

  if(_stage.contains('uploadNexus')) {
        stage('uploadNexus') {
            env.LAST_STAGE_NAME = env.STAGE_NAME
            nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: '/home/juan/ejemplo-maven-1/build']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
            //nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
        }
    }
  
}

return this;