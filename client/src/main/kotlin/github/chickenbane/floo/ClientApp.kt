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

    private fun pfAvail(): Availability = if (pf == null) Availability.unavailable("you are not port forwarding") else Availability.available()

    @ShellMethod("forward floo-proxy")
    fun forward(): String {
        val proxyPods = k8sClient.defaultPods("app=floo-proxy")
        if (proxyPods.isEmpty()) {
            return "floo-proxy not running"
        }
        val podName = proxyPods.first().metadata.name
        pf = k8sClient.forwardDefaultPodPort(podName, 6565)
        return "forwarding $podName"
    }

    @ShellMethod("create a quote")
    fun create(text: String, author: String) = quoteClient.create(text, author)

    fun createAvailability() = pfAvail()

    @ShellMethod("find a quote")
    fun findById(id: String) = quoteClient.findById(id)

    fun findByIdAvailability() = pfAvail()
}

@ShellComponent
class KubernetesClient(private val client: K8sClient) {

    @ShellMethod("get pod names in the default namespace")
    fun podNames(@ShellOption(defaultValue = "", help = "label selector, eg: 'app=floo-proxy'") labelSelector: String): List<String> =
            client.defaultPods(labelSelector).map { it.metadata.name }

}
