package github.chickenbane.floo

import github.chickenbane.proto.CreateQuoteRequest
import github.chickenbane.proto.FindQuoteByIdRequest
import github.chickenbane.proto.QuoteServiceGrpc
import github.chickenbane.proto.QuoteServiceGrpc.QuoteServiceBlockingStub
import github.saturnism.k8sapi.KubernetesNameResolverProvider
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.util.RoundRobinLoadBalancerFactory
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Configuration
class GrpcConfig {
    @Bean
    @Profile("local")
    fun localChannel(@Value("\${quote.server.host}") host: String,
                     @Value("\${quote.server.port}") port: Int): ManagedChannel =
            ManagedChannelBuilder.forAddress(host, port).usePlaintext().build()

    @Bean
    @Profile("default")
    fun channel(@Value("\${floo.service.target}") target: String): ManagedChannel =
            ManagedChannelBuilder.forTarget(target)
                    .nameResolverFactory(KubernetesNameResolverProvider())
                    .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
                    .usePlaintext()
                    .build()
}

@Component
class GrpcQuoteClient(private val channel: ManagedChannel) {
    private val log = LoggerFactory.getLogger(GrpcQuoteClient::class.java)
    private fun stub(): QuoteServiceBlockingStub = QuoteServiceGrpc.newBlockingStub(channel)

    fun create(text: String, author: String): String {
        log.debug("create: text=$text author=$author")
        val request = CreateQuoteRequest.newBuilder()
                .setAuthor(author)
                .setText(text)
                .build()
        val response = stub().create(request)
        log.info("create: response id=${response.id}")
        return response.id
    }

    fun findById(id: String): FindResponse {
        log.debug("findById: id=$id")
        val request = FindQuoteByIdRequest.newBuilder().setId(id).build()
        val response = stub().findById(request)
        log.info("findById: response.text=${response.text} response.author=${response.author} response.server=${response.server}")
        return FindResponse(response.text, response.author, response.server)
    }
}

data class FindResponse(val text: String, val author: String, val server: String)