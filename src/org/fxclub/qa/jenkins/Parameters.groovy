package org.fxclub.qa.jenkins

class Parameters implements Serializable {

    def steps

    Parameters(steps){
        this.steps = steps
    }

    String getParam(param, defaultValue){
        try{
            return "${param}"
        } catch(ignore){
            steps.echo ignore.getMessage()
            return defaultValue
        }
    }
}