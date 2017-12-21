#!/usr/bin/groovy
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

def mergeSuites(String testProject, String suitesInclude, String suitesExclude, String groupsExclude) throws SAXException, IOException, ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
    System.out.println("Project: " + testProject)
    System.out.println("Include suites: " + suitesInclude)
    System.out.println("Exclude suites: " + suitesExclude)
    System.out.println("Exclude groups: " + groupsExclude)

    File[] suitesToMerge = new File("suites/"+project).listFiles(new FileFilter() {
        @Override
        boolean accept(File pathname) {
            return !file.isHidden() && file.getName().toLowerCase().endsWith(".xml")
        }
    })

    mergeSuites(suitesToMerge, suitesInclude, suitesExclude, groupsExclude)
}

def mergeSuites(File[] suitesToMerge, String suites_include, String suites_exclude, String groups_exclude) throws ParserConfigurationException, SAXException, IOException, TransformerException {
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

    Document merged_suite = documentBuilder.parse(new File("suites/_template.xml"))

    List<Document> suites = new ArrayList<>()
    for(File suite : suitesToMerge){
        String suitName = suite.getName().toLowerCase()
        if(!StringUtils.isEmpty(suites_include)){
            List<String> include = Arrays.asList(suites_include.split(";"))
            if(include.contains(suitName.replace(".xml", "")))
                suites.add(documentBuilder.parse(suite))
        } else if(!StringUtils.isEmpty(suites_exclude)){
            List<String> exclude = Arrays.asList(suites_exclude.split(";"))
            if(exclude.contains(suitName.replace(".xml", "")))
                continue
            if(skip_suites.contains(suitName.replace(".xml", "")))
                continue
            suites.add(documentBuilder.parse(suite))
        } else {
            if(skip_suites.contains(suitName.replace(".xml", "")))
                continue
            suites.add(documentBuilder.parse(suite))
        }
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
            Node groups = setGroups(merged_suite,importNode,groups_exclude)
            if(groups != null)
                importNode.appendChild(groups)
            suite_root.appendChild(importNode)
        }
    }

    File merged_xml_file = new File("suites/testng-merged.xml")

    Transformer transformer = TransformerFactory.newInstance().newTransformer()
    Result output = new StreamResult(new FileOutputStream(merged_xml_file))
    Source input = new DOMSource(merged_suite)
    transformer.transform(input, output)
}

private Node setGroups(Document suite, Node test, String exclude){
    if(!StringUtils.isEmpty(exclude)){
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
        if(!StringUtils.isEmpty(exclude)){
            List<String> exclude_list = Arrays.asList(exclude.split(";"))
            for(String group_name : exclude_list){
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