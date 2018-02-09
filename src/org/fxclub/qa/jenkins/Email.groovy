package org.fxclub.qa.jenkins

class Email implements Serializable {
    def steps
    Email(steps){
        this.steps = steps
    }

    def sendAllureResults(subject, mailRecipients) {
        sendAllureResults(subject, mailRecipients , "")
    }
    def sendAllureResults(subject, mailRecipients, notes) {
        steps.emailext body: '''${SCRIPT, template="allure-report.groovy"}''',
                subject: "[Jenkins] ${subject}",
                to: "${mailRecipients}"
    }
}
