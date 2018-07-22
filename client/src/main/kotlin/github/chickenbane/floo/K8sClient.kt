package github.chickenbane.floo

import io.kubernetes.client.ApiClient
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.models.V1Pod
import io.kubernetes.client.util.Config
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


@Configuration
class K8sConfig {

    @Bean
    fun client(): ApiClient = Config.defaultClient().also {
        io.kubernetes.client.Configuration.setDefaultApiClient(it)
    }
}

@Component
class K8sClient(private val client: ApiClient,
                @Value("\${k8s.namespace}") private val namespace: String) {
    private val log = LoggerFactory.getLogger(K8sClient::class.java)
    private val api = CoreV1Api(client)

    fun defaultPods(labelSelector: String = ""): List<V1Pod> {
        val label = if (labelSelector.isBlank()) null else labelSelector
        val list = api.listNamespacedPod(namespace, null, null, null, null, label, null, null, null, null)
        return list.items
    }

    fun defaultPodLog(podName: String): String {
        return api.readNamespacedPodLog(podName, namespace, null, null, null, null, null, null, null, null)
    }


}
