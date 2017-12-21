#!/usr/bin/groovy
package org.fxclub.qa.jenkins

import jenkins.model.Jenkins;
import hudson.slaves.SlaveComputer;
import hudson.slaves.DumbSlave;
import hudson.plugins.sshslaves.SSHLauncher;

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
        def computer = Jenkins.getInstance().getComputer(env.NODE_NAME);
        if (!(computer instanceof SlaveComputer)) {
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
