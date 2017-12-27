package org.fxclub.qa.jenkins

import org.apache.commons.io.IOUtils
import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Attr
import org.w3c.dom.Document;

class TestNG implements Serializable {

    def steps
    TestNG(steps){
        this.steps = steps
    }

    def mergeSuites(String testProject, String suitesIncludeString, String suitesExcludeString, String groupsExcludeString) {
        steps.echo "Project: ${testProject}"

        def suitesInclude = StringUtils.isEmpty(suitesIncludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        def suitesExclude = StringUtils.isEmpty(suitesExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        def groupsExclude = StringUtils.isEmpty(groupsExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
        steps.echo "Include suites: ${suitesIncludeString}"
        steps.echo "Exclude suites: ${suitesExcludeString}"
        steps.echo "Exclude groups: ${groupsExcludeString}"

        def suitesPattern = "suites/" + testProject + "/**.xml"
        def suitesForProject = steps.findFiles glob: suitesPattern

        steps.echo "Suites: " + suitesForProject

        def skipSuites = Arrays.asList("debug","debug1","debug2","checkin","weekends","reg_from_web")

        def suitesToMerge = suitesForProject.findAll({
            def name = it.name.replace(".xml","")
            if(!suitesInclude.isEmpty()){
                return suitesInclude.contains(name)
            } else {
                return !suitesExclude.contains(name) && !skipSuites.contains(name)
            }
        })

        String basePath = steps.pwd()

        String template = basePath + "suites/_template.xml"
        String targetXml = basePath + "suites/testng-merged.xml"

        mergeXmlSuites(suitesToMerge, template, targetXml, groupsExclude)
    }

    private def mergeXmlSuites(def suitesToMerge, String template, String targetXml, List<String> groupsExclude) {
        steps.echo "XML Suites for merge:" + suitesToMerge.toString()

        XmlParser parser = new XmlParser()

        Node merged_suite = parser.parseText(readFile(template))

        steps.writeFile file: targetXml, text: merged_suite.toString()
        /*
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

        Document merged_suite = documentBuilder.parse(readFileToInputStream(template))

        List<Document> suites = new ArrayList<>()
        suitesToMerge.each {
            suites.add(documentBuilder.parse(readFileToInputStream(it.path)))
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

        writeFile(targetXml, baos.toString())
        */
    }

    String readFile(String path){
        steps.readFile path
    }

    def readFileToInputStream(def path){
        String content = steps.readFile "${path}"
        IOUtils.toInputStream(content)
    }

    def writeFile(def path, def content){
        steps.writeFile file: path, text: content
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
