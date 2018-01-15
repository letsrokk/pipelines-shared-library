package org.fxclub.qa.jenkins

import hudson.slaves.DumbSlave
import hudson.slaves.JNLPLauncher
import jenkins.model.Jenkins
import hudson.slaves.SlaveComputer

class Network implements Serializable {

    def steps
    def env
    def hostname
    Network(steps){
        this.steps = steps
        this.env = steps.env
        this.hostname = InetAddress.getLocalHost().getHostName()
    }

    def getHost() {
        String nodeName = env.NODE_NAME
        def computer = Jenkins.getInstance().getComputer(nodeName)
        if (!(computer instanceof SlaveComputer)) {
            return InetAddress.getByName(hostname).address.collect { it & 0xFF }.join('.')
        } else {
            def node = computer.getNode()
            def launcher = node.getLauncher()
            if(launcher instanceof DumbSlave){
                return launcher.getHost()
            } else if(launcher instanceof JNLPLauncher) {
                return InetAddress.getByName(hostname).address.collect { it & 0xFF }.join('.')
            } else {
                throw new UnsupportedOperationException('Unsupported Slave type: ' + launcher.getClass().getCanonicalName())
            }
        }
    }

}
