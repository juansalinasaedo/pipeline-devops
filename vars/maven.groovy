import pipeline.*

def call(String chosenStages) {

	figlet 'maven'

	def pipelineStages = ['compile', 'test', 'jar', 'runJar', 'sonar', 'nexus']

	def utils = new test.MethodsUI()
  	def stages = utils.getValidatedStages(choseStages, pipelineStages)

  	stages.each{
    stage(it){
      try {
        "${it}"()
      }
      catch(Exception e) {
        error "Stage ${it} tiene problemas: ${e}"
      }
    }
  }
}  

def compile(){
  sh 'mvn clean compile -e'	
}

def test(){
  sh 'mvn clean test -e'
}

def jar(){
  sh 'mvn clean package -e'	
}

def runJar(){
	sh 'nohup bash mvn spring-boot:run &'
}

def sonar(){
	withSonarQubeEnv(installationName: 'sonar'){
		sh 'mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.7.0.1746:sonar'
	}
}
  
def nexus(){
	nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
}

return this;