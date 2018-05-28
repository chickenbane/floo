package github.chickenbane.floo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@SpringBootApplication
class ClientApp

fun main(args: Array<String>) {
    runApplication<ClientApp>(*args)
}


@ShellComponent
class QuoteClient(private val quoteClient: GrpcQuoteClient) {

    @ShellMethod("create a quote")
    fun create(text: String, author: String) = quoteClient.create(text, author)

    @ShellMethod("find a quote")
    fun findById(id: String) = quoteClient.findById(id)


}

@ShellComponent
class KubernetesClient(private val client: K8sClient) {

    @ShellMethod("get pod names in the default namespace")
    fun podNames(@ShellOption(defaultValue = "", help = "label selector, eg: 'app=floo-proxy'") labelSelector: String): List<String> =
            client.defaultPods(labelSelector).map { it.metadata.name }

    @ShellMethod("floo-proxy log")
    fun flooProxyLog(): String {
        val proxyPods = client.defaultPods("app=floo-proxy")
        if (proxyPods.isEmpty()) {
            return "floo-proxy not running"
        }
        return client.defaultPodLog(proxyPods.first().metadata.name)
    }
}
