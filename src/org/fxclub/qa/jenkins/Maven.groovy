package org.fxclub.qa.jenkins

class Maven implements Serializable{

    def steps
    def mvnHome
    def mvn
    def mvnProfile = ''

    Maven(steps){
        this('Maven 3.x', 'jenkins', steps)
    }

    Maven(maven, profile, steps){
        this.steps = steps
        this.mvnHome = tool "${maven}"
        this.mvn = "${mvnHome}/bin/mvn"
        if(profile != null)
            mvnProfile = "-P ${profile}"
    }

    def goals(command) {
        def mvnGoals = "${mvn} ${mvnProfile} ${command}"
        steps.echo "Executing Maven goals: ${mvnGoals}"
        sh "${mvnGoals}"
    }
}