package org.fxclub.qa.jenkins

import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition
import org.jenkinsci.plugins.workflow.job.WorkflowJob
import org.jenkinsci.plugins.workflow.libs.GlobalLibraries
import org.jenkinsci.plugins.workflow.libs.LibraryConfiguration
import org.jenkinsci.plugins.workflow.libs.LibraryRetriever
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule

class JenkinsGlobalLibraryUsageTest {

    @Rule
    public JenkinsRule rule = new JenkinsRule()

    @Before
    void configureGlobalLibraries() {
        rule.timeout = 30
        final LibraryRetriever retriever = new LocalLibraryRetriever()
        final LibraryConfiguration localLibrary =
                new LibraryConfiguration('testLibrary', retriever)
        localLibrary.implicit = true
        localLibrary.defaultVersion = 'unused'
        localLibrary.allowVersionOverride = false
        GlobalLibraries.get().setLibraries(Collections.singletonList(localLibrary))
    }

    @Test
    void testingMyLibrary() {
        final CpsFlowDefinition flow = new CpsFlowDefinition(
        '''
       import org.fxclub.qa.jenkins.TestNG

       def testng = new TestNG(this)

       def workspace = "/Users/majer-dy/Documents/IDEA/web-testing-framework"
       echo "workspace: ${workspace}"
       def test_project = "fxbank"
       echo "test project ${test_project}"
       
       def include = "news;audit;"
       def exclude = ""
       def groups = ""
       
       testng.mergeSuites("${workspace}",test_project,include,exclude,groups)
        '''.stripIndent(), true)

        final WorkflowJob workflowJob = rule.createProject(WorkflowJob, 'project')
        workflowJob.definition = flow
        rule.buildAndAssertSuccess(workflowJob)
    }
}
