package org.fxclub.qa.jenkins

class Maven implements Serializable{

    def steps
    def mavenToolName
    def mvnProfile = ''

    Maven(steps){
        this('Maven 3.x', 'jenkins', steps)
    }

    Maven(mavenToolName, mvnProfile, steps){
        this.steps = steps
        this.mavenToolName = mavenToolName

        if(mvnProfile != null)
            this.mvnProfile = "-P ${mvnProfile}"
    }

    def goals(command) {
        this.mvnHome = steps.tool "${mavenToolName}"
        this.mvn = "${mvnHome}/bin/mvn"

        def mvnCommand = "${mvn} ${mvnProfile} ${command}"

        steps.echo "Executing Maven goals: ${mvnCommand}"
        steps.sh "${mvnCommand}"
    }
}