#!/usr/bin/groovy
import org.apache.commons.lang3.StringUtils
import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import org.xml.sax.EntityResolver
import org.xml.sax.InputSource
import org.xml.sax.SAXException

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import javax.xml.transform.Result
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.TransformerFactoryConfigurationError
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import java.util.stream.Collectors

//mergeSuites("/Users/majer-dy/Documents/IDEA/web-testing-framework", "fxbank", "news;audit;", "", "")

def mergeSuites(String basePath, String testProject, String suitesIncludeString, String suitesExcludeString, String groupsExcludeString) throws SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
    System.out.println("Project: " + testProject)

    def suitesInclude = StringUtils.isEmpty(suitesIncludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
    def suitesExclude = StringUtils.isEmpty(suitesExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
    def groupsExclude = StringUtils.isEmpty(groupsExcludeString) ? Collections.emptyList() : Arrays.asList(suitesIncludeString.split(';'))
    System.out.println("Include suites: " + suitesInclude.size())
    System.out.println("Exclude suites: " + suitesExclude.size())
    System.out.println("Exclude groups: " + groupsExclude.size())

    def suitesDir = new File(basePath+"/suites/"+testProject)

    File[] suitesForProject = suitesDir.listFiles({ File file ->
        !file.hidden && file.name.toLowerCase().endsWith(".xml")
    } as FileFilter)
    def skipSuites = Arrays.asList("debug","debug1","debug2","checkin","weekends","reg_from_web")

    List<File> suitesToMerge = Arrays.stream(suitesForProject).filter({
        def name = it.name.replace(".xml","")
        if(!suitesInclude.isEmpty()){
            return suitesInclude.contains(name)
        } else {
            return !suitesExclude.contains(name) && !skipSuites.contains(name)
        }
    }).collect(Collectors.toList())

    File template = new File(basePath + "/suites/_template.xml")
    File targetXml = new File(basePath + "/suites/testng-merged.xml")

    mergeXmlSuites(suitesToMerge, template, targetXml, groupsExclude)
}

def mergeXmlSuites(List<File> suitesToMerge, File template, File targetXml, List<String> groupsExclude) throws ParserConfigurationException, SAXException, IOException, TransformerException {
    System.out.println("XML Suites for merge:")
    suitesToMerge.forEach({
        System.out.println(it.getAbsolutePath())
    })

    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    EntityResolver entityResolver = new EntityResolver() {
        @Override
        InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            URL url = new URL(systemId)
            boolean isRedirect = true
            while (isRedirect){
                HttpURLConnection conn = (HttpURLConnection) url.openConnection()
                conn.setInstanceFollowRedirects(true)
                if(conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_TEMP || conn.getResponseCode() == HttpURLConnection.HTTP_MOVED_PERM){
                    isRedirect = true
                    url = new URL(conn.getHeaderField("Location"))
                } else {
                    isRedirect = false
                }
            }
            return new InputSource(url.openStream())
        }
    }
    documentBuilder.setEntityResolver(entityResolver)

    Document merged_suite = documentBuilder.parse(template)

    List<Document> suites = new ArrayList<>()
    suitesToMerge.stream().forEach({
        suites.add(documentBuilder.parse(it))
    })

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

Node setGroups(Document suite, Node test, List<String> exclude){
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
            groups = suite.createElement("groups")
        }
        Node run = suite.createElement("run")
        if(!exclude.isEmpty()){
            for(String group_name : exclude){
                Node include_element = suite.createElement("exclude")
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