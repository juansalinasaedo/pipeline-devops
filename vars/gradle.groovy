import pipeline.*

def call(String choseStages){

  figlet 'gradle'

  def pipelineStages = ['buildAndTest', 'sonar', 'jar', 'rest', 'nexus']

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

def hola(){
  println 'Hola Mundo'
}

def buildAndTest(){
  sh "gradle clean build" 
}

def sonar(){
  //def sonarhome = tool 'sonar-scanner'
  def scannerHome = tool 'sonar-scanner';
  sh "${scannerHome}/bin/sonar-scanner -Dsonar.projectKey=ejemplo-gradle -Dsonar.java.binaries=build"
}

def jar(){
  sh "nohup gradle bootRun &"
  sleep 20
}
    

def rest(){
  sh "curl -X GET 'http://localhost:8082/rest/mscovid/test?msg=testing'"
}

def nexus(){
  nexusPublisher nexusInstanceId: 'nexus', nexusRepositoryId: 'test-nexus', packages: [[$class: 'MavenPackage', mavenAssetList: [[classifier: '', extension: 'jar', filePath: 'build/libs/DevOpsUsach2020-0.0.1.jar']], mavenCoordinate: [artifactId: 'DevOpsUsach2020', groupId: 'com.devopsusach2020', packaging: 'jar', version: '0.0.1']]]
}

return this;