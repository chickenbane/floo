package github.chickenbane.floo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.shell.Availability
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@SpringBootApplication
class ClientApp

fun main(args: Array<String>) {
    runApplication<ClientApp>(*args)
}


@ShellComponent
class QuoteClient(private val quoteClient: GrpcQuoteClient, private val k8sClient: K8sClient) {

    private var pf: K8sPortForward? = null

    @ShellMethod("forward floo-proxy")
    fun forward(): String {
        val proxyPods = k8sClient.defaultPods("app=floo-proxy")
        if (proxyPods.isEmpty()) {
            return "floo-proxy not running"
        }
        val flooName = proxyPods.first().metadata.name
        pf = k8sClient.forwardDefaultPodPort(flooName, 6565)
        return "forwarding $flooName"
    }

    @ShellMethod("create a quote")
    fun create(text: String, author: String) = quoteClient.create(text, author)

    fun createAvailability(): Availability =
            if (pf == null) Availability.unavailable("not port forwarding") else Availability.available()

    @ShellMethod("find a quote")
    fun findById(id: String) = quoteClient.findById(id)

    fun findByIdAvailability(): Availability =
            if (pf == null) Availability.unavailable("not port forwarding") else Availability.available()
}

@ShellComponent
class KubernetesClient(private val client: K8sClient) {

    @ShellMethod("get pod names in the default namespace, optionally providing a label")
    fun podNames(@ShellOption(defaultValue = "") label: String): List<String> =
            client.defaultPods(label).map { it.metadata.name }

}
