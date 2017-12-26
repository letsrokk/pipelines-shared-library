package org.fxclub.qa.jenkins

class Maven implements Serializable{

    def steps
    def mavenToolName

    Maven(steps){
        this(steps, 'Maven 3.x')
    }

    Maven(steps, mavenToolName){
        this.steps = steps
        this.mavenToolName = mavenToolName
    }

    def goals(command) {
        def mvnHome = steps.tool "${mavenToolName}"
        def mvn = "${mvnHome}/bin/mvn"

        def mvnCommand = "${mvn} ${command}"

        steps.echo "Executing Maven goals: ${mvnCommand}"
        steps.sh "${mvnCommand}"
    }
}