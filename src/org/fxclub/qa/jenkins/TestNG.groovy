package org.fxclub.qa.jenkins

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class TestNG implements Serializable {

    def steps
    TestNG(steps){
        this.steps = steps
    }

    def mergeSuites(String testProject, String suitesIncludeString, String suitesExcludeString, String groupsExcludeString) {
        mergeSuites(null, testProject, suitesIncludeString, suitesExcludeString, groupsExcludeString)
    }

    def mergeSuites(String basePath, String testProject, String suitesIncludeString, String suitesExcludeString, String groupsExcludeString) {
        steps.echo "Project: ${testProject}"

        def suitesInclude = StringUtils.isEmpty(suitesIncludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        def suitesExclude = StringUtils.isEmpty(suitesExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        def groupsExclude = StringUtils.isEmpty(groupsExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        steps.echo "Include suites: ${suitesIncludeString}"
        steps.echo "Exclude suites: ${suitesExcludeString}"
        steps.echo "Exclude groups: ${groupsExcludeString}"

        if(StringUtils.isEmpty(basePath)){
            basePath = ""
        }

        def suitesPattern = basePath + "src/test/resources/suites/" + testProject + "/**.xml"
        def suitesForProject = steps.findFiles glob: suitesPattern

        def skipSuites = Arrays.asList("debug","debug1","debug2","checkin","weekends","reg_from_web")

        List<String> suitesToMerge = new ArrayList<>()
        suitesForProject.findAll({
            def name = it.name.replace(".xml","")
            if(!suitesInclude.isEmpty()){
                return suitesInclude.contains(name)
            } else {
                return !suitesExclude.contains(name) && !skipSuites.contains(name)
            }
        }).each {
            String suite = steps.readFile it.path
            suitesToMerge.add(suite)
        }

        String template = steps.readFile basePath + "suites/_template.xml"

        String mergedSuite = mergeXmlSuites(suitesToMerge, template, groupsExclude)

        String targetXml = basePath + "suites/testng-merged.xml"
        steps.writeFile file: targetXml, text: mergedSuite
    }

    private def mergeXmlSuites(List<String> suitesToMerge, String template, List<String> groupsExclude) {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

        Document merged_suite = documentBuilder.parse(IOUtils.toInputStream(template))

        List<Document> suites = new ArrayList<>()
        suitesToMerge.each {
            suites.add(documentBuilder.parse(IOUtils.toInputStream(it)))
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
        ByteArrayOutputStream baos = new ByteArrayOutputStream()
        Result output = new StreamResult(baos)
        Source input = new DOMSource(merged_suite)
        transformer.transform(input, output)

        baos.toString()
    }

//    String readFile(String path){
//        steps.readFile path
//    }
//
//    def readFileToInputStream(def path){
//        String content = steps.readFile "${path}"
//        IOUtils.toInputStream(content)
//    }
//
//    def writeFile(def path, def content){
//        steps.writeFile file: path, text: content
//    }
//
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
