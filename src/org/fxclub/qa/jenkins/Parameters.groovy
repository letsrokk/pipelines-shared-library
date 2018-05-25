package org.fxclub.qa.jenkins

class Parameters implements Serializable {

    def steps

    Parameters(steps){
        this.steps = steps
    }

    String getParam(parameterName, defaultValue){
        try{
            return steps.params.parameterName
        } catch(ignore){
            steps.echo ignore.getMessage()
            return defaultValue
        }
    }
}