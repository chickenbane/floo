package github.chickenbane.floo

import io.kubernetes.client.ApiClient
import io.kubernetes.client.Configuration
import io.kubernetes.client.PortForward
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.util.Config
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import java.io.PrintStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket


@org.springframework.context.annotation.Configuration
class K8sConfig {

    @Bean
    fun client(): ApiClient = Config.defaultClient().also {
        Configuration.setDefaultApiClient(it)
    }
}

private const val DEFAULT_NAMESPACE = "default"

@Component
class K8sClient(private val client: ApiClient) {
    private val log = LoggerFactory.getLogger(K8sClient::class.java)
    private val api = CoreV1Api(client)

    fun defaultPods(labelSelector: String = ""): List<V1Pod> {
        val label = if (labelSelector.isBlank()) null else labelSelector
        val list = api.listNamespacedPod(DEFAULT_NAMESPACE, null, null, null, null, label, null, null, null, null)
        return list.items
    }

    fun forwardDefaultPodPort(podName: String, port: Int) = K8sPortForward(client, podName, port)

}

class K8sPortForward(client: ApiClient, podName: String, private val port: Int) {
    private val result = PortForward(client).forward(DEFAULT_NAMESPACE, podName, listOf(port))
    private val log = LoggerFactory.getLogger(K8sPortForward::class.java)

    init {
        log.warn("Starting k8sPortForward")
    }

    private val serverSocket = ServerSocket(port, 0, InetAddress.getByName(null))


    private val socket: Socket by lazy {
        serverSocket.accept()
    }

    private val outputThread = Thread(outputRunnable()).also {
        log.warn("start outputThread")
        it.start()
    }

    private fun outputRunnable() = Runnable {
        log.warn("start outputRunnable")
        val output = result.getOutboundStream(port)
        while (true) {
            socket.getInputStream().copyTo(output)
        }
    }

    private val inputThread = Thread(inputRunnable()).also {
        log.warn("start inputThread")
        it.start()
    }

    private fun inputRunnable() = Runnable {
        log.warn("start inputRunnable")
        val input = result.getInputStream(port)
        while (true) {
            input.copyTo(socket.getOutputStream())
        }
    }

    private val error = PrintStream(result.getErrorStream(port), true)
}
