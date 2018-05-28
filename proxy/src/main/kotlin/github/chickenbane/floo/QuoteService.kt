package github.chickenbane.floo

import github.chickenbane.proto.CreateQuoteRequest
import github.chickenbane.proto.CreateQuoteResponse
import github.chickenbane.proto.FindQuoteByIdRequest
import github.chickenbane.proto.FindQuoteByIdResponse
import github.chickenbane.proto.QuoteServiceGrpc
import github.saturnism.k8sapi.KubernetesNameResolverProvider
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import io.grpc.util.RoundRobinLoadBalancerFactory
import org.lognet.springboot.grpc.GRpcService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component


@Configuration
class GrpcConfig {

    @Bean
    fun channel(@Value("\${floo.service.target}") target: String): ManagedChannel =
            ManagedChannelBuilder.forTarget(target)
                    .nameResolverFactory(KubernetesNameResolverProvider())
                    .loadBalancerFactory(RoundRobinLoadBalancerFactory.getInstance())
                    .usePlaintext()
                    .build()
}

@Component
class QuoteProxy(private val channel: ManagedChannel) {
    private fun stub(): QuoteServiceGrpc.QuoteServiceBlockingStub = QuoteServiceGrpc.newBlockingStub(channel)

    fun create(request: CreateQuoteRequest): CreateQuoteResponse = stub().create(request)

    fun findById(request: FindQuoteByIdRequest): FindQuoteByIdResponse = stub().findById(request)
}

@GRpcService
class QuoteProxyService(private val proxy: QuoteProxy) : QuoteServiceGrpc.QuoteServiceImplBase() {

    override fun create(request: CreateQuoteRequest, responseObserver: StreamObserver<CreateQuoteResponse>) {
        responseObserver.onNext(proxy.create(request))
        responseObserver.onCompleted()
    }

    override fun findById(request: FindQuoteByIdRequest, responseObserver: StreamObserver<FindQuoteByIdResponse>) {
        responseObserver.onNext(proxy.findById(request))
        responseObserver.onCompleted()
    }
}