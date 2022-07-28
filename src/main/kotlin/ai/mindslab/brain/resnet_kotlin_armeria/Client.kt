package ai.mindslab.brain.resnet_kotlin_armeria

import cloud.filibuster.FaultDecoratingHttpClient
import com.linecorp.armeria.client.circuitbreaker.CircuitBreakerClient
import com.linecorp.armeria.client.circuitbreaker.CircuitBreakerRule
import com.linecorp.armeria.client.grpc.GrpcClientBuilder
import com.linecorp.armeria.client.grpc.GrpcClients
import com.linecorp.armeria.client.retry.RetryRule
import com.linecorp.armeria.client.retry.RetryingClient
import com.linecorp.armeria.common.grpc.GrpcSerializationFormats

fun main() {
    val baseURI = "http://127.0.0.1:50911/"

    val rule = CircuitBreakerRule.builder()
        .onServerErrorStatus()
        .onException()
        .thenFailure()

    var grpcClientBuilder: GrpcClientBuilder? = GrpcClients.builder(baseURI)
        .serializationFormat(GrpcSerializationFormats.PROTO)
        .decorator { delegate -> FaultDecoratingHttpClient(delegate) }
//        .decorator(CircuitBreakerClient.builder(rule).newDecorator());
        .decorator(RetryingClient.newDecorator(RetryRule.failsafe()));

    val blockingStub: ImageClassificationServiceGrpc.ImageClassificationServiceBlockingStub? = grpcClientBuilder?.build(ImageClassificationServiceGrpc.ImageClassificationServiceBlockingStub::class.java)
    val request: ImageClassification.ClassifyRequest = ImageClassification.ClassifyRequest.newBuilder().build()
    val reply = blockingStub?.classify(request)
    print(reply)
    return;
}