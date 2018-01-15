package org.fxclub.qa.jenkins

class Email implements Serializable {
    def sendAllureResults(subject,mailRecipients) {
        emailext body: '''${SCRIPT, template="groovy-html.template"}''',
                subject: "[Jenkins] ${subject}",
                to: "${mailRecipients}",
                recipientProviders: [[$class: 'CulpritsRecipientProvider']]
    }
}
