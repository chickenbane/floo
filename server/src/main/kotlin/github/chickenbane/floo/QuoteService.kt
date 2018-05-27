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
import org.springframework.boot.info.GitProperties
import java.net.InetAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong


@GRpcService
class QuoteService(private val gitProperties: GitProperties) : QuoteServiceGrpc.QuoteServiceImplBase() {

    private val log = LoggerFactory.getLogger(QuoteService::class.java)

    private val startingQuotes = listOf(
            Quote("Float like a butterfly, sting like a bee", "Madonna"),
            Quote("Ask not what your country can do for you", "Trump")
    )

    private val nextMapId = AtomicLong()
    private val map = ConcurrentHashMap<Long, Quote>().apply {
        putAll(startingQuotes.map { Pair(nextMapId.incrementAndGet(), it) })
        log.info("initial quotes=$this")
    }

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
        val key = nextMapId.incrementAndGet()
        map[key] = quote
        responseObserver.onNext(CreateQuoteResponse.newBuilder().setId(key.toString()).build())
        responseObserver.onCompleted()
    }

    override fun findById(request: FindQuoteByIdRequest, responseObserver: StreamObserver<FindQuoteByIdResponse>) {
        log.debug("findById: request.id=${request.id}")
        val key: Long
        try {
            key = request.id.toLong()
        } catch (e: NumberFormatException) {
            responseObserver.onError(StatusRuntimeException(Status.INVALID_ARGUMENT))
            return
        }
        val quote = map[key]
        if (quote == null) {
            responseObserver.onError(StatusRuntimeException(Status.NOT_FOUND))
            return
        }

        val from = "host=${InetAddress.getLocalHost().hostAddress} gitSha=${gitProperties.shortCommitId}"

        val response = FindQuoteByIdResponse.newBuilder()
                .setId(request.id)
                .setAuthor(quote.author)
                .setText(quote.text)
                .setServer(from)
                .build()
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}

data class Quote(val text: String, val author: String)