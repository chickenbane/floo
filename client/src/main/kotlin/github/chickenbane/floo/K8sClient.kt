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

    fun defaultPods(label: String = ""): List<V1Pod> {
        val labelSelector = if (label.isBlank()) null else label
        val list = api.listNamespacedPod(DEFAULT_NAMESPACE, null, null, null, null, labelSelector, null, null, null, null)
        return list.items
    }

    fun forwardDefaultPodPort(podName: String, port: Int): K8sPortForward {
        val forward = PortForward(client)
        return K8sPortForward(forward.forward(DEFAULT_NAMESPACE, podName, listOf(port)), port)
    }

}

class K8sPortForward(private val forwardResult: PortForward.PortForwardResult, private val port: Int) {

}