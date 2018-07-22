package github.chickenbane.floo.it

import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class Integration2Test {
    @Test
    fun foo3() {
        System.out.println("oh hai")
    }

    private val log = LoggerFactory.getLogger(FlooTest::class.java)
    @Test
    fun foo4() {
        log.info("There she blows")
    }
}