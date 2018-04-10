package org.fxclub.qa.jenkins

class Email implements Serializable {

    def steps

    Email(steps){
        this.steps = steps
    }

    def sendAllureResults(subject, mailRecipients) {
        steps.emailext body: '''${SCRIPT, template="allure-report.groovy"}''',
                subject: "[Jenkins] ${subject}",
                to: "${mailRecipients}"
    }
}
