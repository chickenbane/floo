package github.chickenbane.floo

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@SpringBootApplication
class ClientApp

fun main(args: Array<String>) {
    runApplication<ClientApp>(*args)
}

@ShellComponent
class FlooShellComponent {
    val log = LoggerFactory.getLogger(FlooShellComponent::class.java)
    @ShellMethod("my first shell method")
    fun first() {
        log.info("working the pole")
    }
}