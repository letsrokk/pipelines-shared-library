package org.fxclub.qa.jenkins

class Parameters implements Serializable {

    static String getParam(param, defaultValue){
        try{
            return "${param}"
        } catch(ignore){
            return defaultValue
        }
    }
}