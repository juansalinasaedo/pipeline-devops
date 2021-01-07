package pipeline.test

def getValidatedStages(String chosenStages, ArrayList pipelineStages){

	def stages = []

	if (chosenStages?.trim()){
		chosenStages.split(';').each{
			if (it in pipelineStages){
				stages.add(it)
			} else {
				error "${it} no existe como Stage. Stages disponibles: ${pipelineStages}"
			}
		}
		println "Validación de stages correcta. Se ejecutaran los Stages: ${stages}"
	} else {
		stages = pipelineStages
		println "Parametro de stages vacío. Se ejecutaran los Stages: ${stages}"
	}

	return stages
}

def hola(){
	println 'Hola Mundo'
}

return this;