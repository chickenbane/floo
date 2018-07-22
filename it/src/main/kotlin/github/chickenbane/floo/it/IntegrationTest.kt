package github.chickenbane.floo.it

import org.junit.Test
import org.slf4j.LoggerFactory

class FlooTest {
    @Test
    fun foo() {
        System.out.println("oh hai")
    }

    private val log = LoggerFactory.getLogger(FlooTest::class.java)
    @Test
    fun foo2() {
        log.info("There she blows")
    }
}