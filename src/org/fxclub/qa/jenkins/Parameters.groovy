package org.fxclub.qa.jenkins

class Parameters implements Serializable {

    def steps

    Parameters(steps){
        this.steps = steps
    }

    String getParam(parameterName, defaultValue){
        def value = steps.params[parameterName]

        if(value == null)
            return defaultValue
        else
            value
    }
}