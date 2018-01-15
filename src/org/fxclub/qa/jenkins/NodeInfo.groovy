package org.fxclub.qa.jenkins

import jenkins.slaves.*
import jenkins.model.*
import hudson.slaves.*
import hudson.model.*

class NodeInfo implements Serializable {

    def steps
    def slave

    NodeInfo(steps){
        this.steps = steps
        String nodeName = steps.env.NODE_NAME
        this.slave = Jenkins.getInstance().getComputer(nodeName) as Slave
    }

    def toJSON(node) {
        if (node instanceof Map) {
            return "{" + node.collect { k,v -> "\"${k}\":" + toJSON(v) }.join(", ") + "}"
        }
        else if (node instanceof String) {
            return "\"${node}\""
        }
        else {
            return node
        }
    }

    def printNodeInfo(){
        if (slave == null) {
            println "{}"
        }
        else {
            node = [
                    "name" : slave.name,
                    "description" : slave.nodeDescription,
                    "remote_fs" : slave.remoteFS,
                    "executors" : slave.numExecutors.toInteger(),
                    "mode" : slave.mode.toString(),
                    "labels" : slave.labelString,
                    "availability" : slave.retentionStrategy.class.name.tokenize('$').get(1),
            ]

            if ((env = slave.nodeProperties.get(EnvironmentVariablesNodeProperty.class)?.envVars)) {
                node["env"] = env
            }

            if (slave.retentionStrategy instanceof RetentionStrategy.Demand) {
                retention = slave.retentionStrategy as RetentionStrategy.Demand
                node["in_demand_delay"] = retention.inDemandDelay
                node["idle_delay"] = retention.idleDelay
            }

            launcher = slave.launcher
            /*
            if (launcher instanceof CommandLauncher) {
                node["launcher"] = "command"
                node["command"] = launcher.command
            }
            else */
            if (launcher instanceof JNLPLauncher) {
                node["launcher"] = "jnlp"
                if (jenkins.slaves.JnlpSlaveAgentProtocol.declaredFields.find { it.name == 'SLAVE_SECRET' }) {
                    node["secret"] = jenkins.slaves.JnlpSlaveAgentProtocol.SLAVE_SECRET.mac( slave.name )
                }
            }
            else {
                node["launcher"] = "ssh"
                node["host"] = launcher.host
                node["port"] = launcher.port
                node["username"] = launcher.username
                if (launcher.password != null) {
                    node["password"] = launcher.password
                }
                node["private_key"] = launcher.privatekey
                node["jvm_options"] = launcher.jvmOptions
            }

            println toJSON(node)
        }
    }

}
