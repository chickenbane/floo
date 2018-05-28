package github.chickenbane.floo

import github.chickenbane.proto.CreateQuoteRequest
import github.chickenbane.proto.CreateQuoteResponse
import github.chickenbane.proto.FindQuoteByIdRequest
import github.chickenbane.proto.FindQuoteByIdResponse
import github.chickenbane.proto.QuoteServiceGrpc
import io.grpc.ManagedChannel
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.springframework.stereotype.Component


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