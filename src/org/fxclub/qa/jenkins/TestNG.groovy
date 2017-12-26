package org.fxclub.qa.jenkins

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

class TestNG implements Serializable {

    def steps
    TestNG(steps){
        this.steps = steps
    }

    def mergeSuites(String basePath, String testProject, String suitesIncludeString, String suitesExcludeString, String groupsExcludeString) {
        steps.echo "Project: ${testProject}"

        def suitesInclude = StringUtils.isEmpty(suitesIncludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        def suitesExclude = StringUtils.isEmpty(suitesExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        def groupsExclude = StringUtils.isEmpty(groupsExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        steps.echo "Include suites: ${suitesIncludeString}"
        steps.echo "Exclude suites: ${suitesExcludeString}"
        steps.echo "Exclude groups: ${groupsExcludeString}"

        File suitesDir = new File(basePath+"/suites/"+testProject)
        steps.echo "Suites path: " + suitesDir.getAbsolutePath() + suitesDir.exists()
        steps.echo "Suites: " + suitesDir.list()

        List<File> suitesForProject = Arrays.asList(suitesDir.listFiles()).findAll {
            it.getName().toLowerCase().endsWith(".xml")
        }

        def skipSuites = Arrays.asList("debug","debug1","debug2","checkin","weekends","reg_from_web")

        List<File> suitesToMerge = suitesForProject.findAll({
            def name = it.name.replace(".xml","")
            if(!suitesInclude.isEmpty()){
                return suitesInclude.contains(name)
            } else {
                return !suitesExclude.contains(name) && !skipSuites.contains(name)
            }
        })

        File template = new File(basePath + "/suites/_template.xml")
        File targetXml = new File(basePath + "/suites/testng-merged.xml")

        mergeXmlSuites(suitesToMerge, template, targetXml, groupsExclude)
    }

    private def mergeXmlSuites(List<File> suitesToMerge, File template, File targetXml, List<String> groupsExclude) {
        steps.echo "XML Suites for merge:" + suitesToMerge.toString()

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

        Document merged_suite = documentBuilder.parse(template)

        List<Document> suites = new ArrayList<>()
        suitesToMerge.stream().each {
            suite -> suites.add(documentBuilder.parse(suite))
        }

        Node suite_root = merged_suite.getElementsByTagName("suite").item(0)
        for(Document suite : suites){
            NodeList tests = suite.getElementsByTagName("test")
            for(int i = 0; i < tests.getLength(); i++){
                Node importNode = tests.item(i).cloneNode(true)

                //Make test suite name unique
                Node nameNode = importNode.getAttributes().getNamedItem("name")
                nameNode.setNodeValue(nameNode.getNodeValue())
                importNode.getAttributes().setNamedItem(nameNode)

                merged_suite.adoptNode(importNode)
                Node groups = setGroups(merged_suite,importNode,groupsExclude)
                if(groups != null)
                    importNode.appendChild(groups)
                suite_root.appendChild(importNode)
            }
        }

        Transformer transformer = TransformerFactory.newInstance().newTransformer()
        Result output = new StreamResult(new FileOutputStream(targetXml))
        Source input = new DOMSource(merged_suite)
        transformer.transform(input, output)
    }

    private Node setGroups(Document suite, Node test, List<String> exclude) {
        if(!exclude.isEmpty()){
            Node groups = null
            NodeList testChilds = test.getChildNodes()
            for(int i = 0; i < testChilds.getLength(); i++){
                Node child = testChilds.item(i)
                if(child.getNodeName() == "groups"){
                    groups = testChilds.item(i)
                    break
                }
            }
            if(groups == null){
                groups = (Node) suite.createElement("groups")
            }
            Node run = (Node) suite.createElement("run")
            if(!exclude.isEmpty()){
                for(String group_name : exclude){
                    Node include_element = (Node) suite.createElement("exclude")
                    Attr name_attribute = suite.createAttribute("name")
                    name_attribute.setNodeValue(group_name)
                    include_element.getAttributes().setNamedItem(name_attribute)
                    run.appendChild(include_element)
                }
            }
            groups.appendChild(run)
            return groups
        }
        return null
    }

}
