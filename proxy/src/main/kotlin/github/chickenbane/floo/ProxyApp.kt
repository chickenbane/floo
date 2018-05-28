package github.chickenbane.floo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProxyApp

fun main(args: Array<String>) {
    runApplication<ProxyApp>(*args)
}
