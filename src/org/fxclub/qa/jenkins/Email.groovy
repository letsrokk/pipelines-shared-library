package org.fxclub.qa.jenkins

class Email implements Serializable {
    def steps
    Email(steps){
        this.steps = steps
    }

    def sendAllureResults(subject,mailRecipients) {
        steps.emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                subject: "[Jenkins] ${subject}",
                to: "${mailRecipients}",
                recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}
