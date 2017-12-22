import org.fxclub.qa.jenkins.TestNG

def testNG = new TestNG();

TestNG.mergeSuites("/Users/majer-dy/Documents/IDEA/web-testing-framework", "fxbank", "news;audit;", "", "")