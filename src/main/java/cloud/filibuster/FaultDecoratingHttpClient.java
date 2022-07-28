package cloud.filibuster;

import com.linecorp.armeria.client.ClientRequestContext;
import com.linecorp.armeria.client.HttpClient;
import com.linecorp.armeria.client.SimpleDecoratingHttpClient;
import com.linecorp.armeria.common.FilteredHttpResponse;
import com.linecorp.armeria.common.HttpData;
import com.linecorp.armeria.common.HttpHeaderNames;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpHeadersBuilder;
import com.linecorp.armeria.common.HttpObject;
import com.linecorp.armeria.common.HttpRequest;
import com.linecorp.armeria.common.HttpResponse;
import com.linecorp.armeria.common.HttpStatus;
import com.linecorp.armeria.common.ResponseHeaders;
import com.linecorp.armeria.common.ResponseHeadersBuilder;
import org.apache.curator.shaded.com.google.common.net.MediaType;

public class FaultDecoratingHttpClient extends SimpleDecoratingHttpClient {
    public boolean shouldReturnError = true;

    public FaultDecoratingHttpClient(HttpClient delegate) {
        super(delegate);
    }

    @Override
    public HttpResponse execute(ClientRequestContext ctx, HttpRequest req) throws Exception {
        if (shouldReturnError) {
            ResponseHeadersBuilder responseHeadersBuilder = ResponseHeaders.builder().status(200)
                    .add("content-type", "application/grpc+proto")
                    .add("content-length", "0".toString())
                    .add("grpc-status", "6")
                    .add("grpc-message", "my error message");
            responseHeadersBuilder.endOfStream(true);
            return HttpResponse.of(responseHeadersBuilder.build());
        } else {
            HttpResponse response = unwrap().execute(ctx, req);

            return new FilteredHttpResponse(response) {
                @Override
                protected HttpObject filter(HttpObject obj) {
                    return obj;
                }
            };
        }
    }
}

