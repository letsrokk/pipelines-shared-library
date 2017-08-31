package org.fxclub.qa.jenkins

@Grapes(
        @Grab(group='org.jenkins-ci.main', module='jenkins-core', version='2.76', scope='provided')
)

import jenkins.model.Jenkins;
import hudson.slaves.SlaveComputer;
import hudson.slaves.DumbSlave;
import hudson.plugins.sshslaves.SSHLauncher;

class Network implements Serializable {

    def getHost() {
        def computer = Jenkins.getInstance().getComputer(env.NODE_NAME);
        if (!(computer instanceof SlaveComputer)) {
            def hostname = "${HOSTNAME}"
            return InetAddress.getByName(hostname).address.collect { it & 0xFF }.join('.')
        } else {
            def node = computer.getNode();
            if (!(node instanceof DumbSlave)) {
                error "Not a dumb slave";
            }
            def launcher = node.getLauncher();
            if (!(launcher instanceof SSHLauncher)) {
                error "Not a SSHLauncher";
            }
            return launcher.getHost();
        }
    }

}
