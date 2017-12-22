package org.fxclub.qa.jenkins

import org.junit.Test

class TestNgTest {

    @Test
    void testingMyLibrary() {
        TestNG.mergeSuites("/Users/majer-dy/Documents/IDEA/web-testing-framework","fxbank","news;audit;","","")
    }
}
