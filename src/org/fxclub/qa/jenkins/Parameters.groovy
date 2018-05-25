package org.fxclub.qa.jenkins

class Parameters implements Serializable {

    def steps

    Parameters(steps){
        this.steps = steps
    }

    String get(parameterName, defaultValue){
        try{
            return params.parameterName
        } catch(ignore){
            return defaultValue
        }
    }
}