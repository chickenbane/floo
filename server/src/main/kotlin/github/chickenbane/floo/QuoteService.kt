package github.chickenbane.floo

import github.chickenbane.proto.CreateQuoteRequest
import github.chickenbane.proto.CreateQuoteResponse
import github.chickenbane.proto.FindQuoteByIdRequest
import github.chickenbane.proto.FindQuoteByIdResponse
import github.chickenbane.proto.QuoteServiceGrpc
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver
import org.lognet.springboot.grpc.GRpcService
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@GRpcService
class QuoteService : QuoteServiceGrpc.QuoteServiceImplBase() {
    private val log = LoggerFactory.getLogger(QuoteService::class.java)
    private val map = ConcurrentHashMap<String, Quote>()

    override fun create(request: CreateQuoteRequest, responseObserver: StreamObserver<CreateQuoteResponse>) {
        log.debug("create: request.author=${request.author} request.text=${request.text}")
        if (request.author.isBlank() || request.text.isBlank()) {
            responseObserver.onError(StatusRuntimeException(Status.INVALID_ARGUMENT))
            return
        }
        val quote = Quote(request.text, request.author)
        if (map.containsValue(quote)) {
            responseObserver.onError(StatusRuntimeException(Status.ALREADY_EXISTS))
            return
        }
        val key = UUID.randomUUID().toString()
        map[key] = quote  // todo check key collision?
        responseObserver.onNext(CreateQuoteResponse.newBuilder().setId(key).build())
        responseObserver.onCompleted()
    }

    override fun findById(request: FindQuoteByIdRequest, responseObserver: StreamObserver<FindQuoteByIdResponse>) {
        log.debug("findById: request.id=${request.id}")
        val quote = map[request.id]
        if (quote == null) {
            responseObserver.onError(StatusRuntimeException(Status.NOT_FOUND))
            return
        }
        val response = FindQuoteByIdResponse.newBuilder()
                .setId(request.id)
                .setAuthor(quote.author)
                .setText(quote.text)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}

data class Quote(val text: String, val author: String)