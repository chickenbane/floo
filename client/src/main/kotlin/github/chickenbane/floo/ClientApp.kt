package github.chickenbane.floo

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
class QuoteClient(private val client: GrpcQuoteClient) {

    @ShellMethod("create a quote")
    fun create(text: String, author: String) = client.create(text, author)

    @ShellMethod("find a quote")
    fun findById(id: String) = client.findById(id)
}