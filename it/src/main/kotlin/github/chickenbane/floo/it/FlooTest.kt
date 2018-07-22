package github.chickenbane.floo.it

import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
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