package com.github.thepwagner.actionupdate.v1.rpc;

import com.flit.runtime.ErrorCode;
import com.flit.runtime.FlitException;
import com.flit.runtime.undertow.ErrorWriter;
import com.flit.runtime.undertow.FlitHandler;
import com.google.protobuf.util.JsonFormat;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcUpdateServiceHandler implements HttpHandler {
  private static final Logger LOGGER = LoggerFactory.getLogger(RpcUpdateServiceHandler.class);

  public static final String ROUTE = "/twirp/actionupdate.v1.UpdateService";

  private final RpcUpdateService service;

  private final ErrorWriter errorWriter;

  public RpcUpdateServiceHandler(RpcUpdateService service) {
    this.service = service;
    this.errorWriter = new ErrorWriter();
  }

  @Override
  public void handleRequest(HttpServerExchange exchange) throws Exception {
    if (exchange.isInIoThread()) {
      exchange.dispatch(this);
      return;
    }
    exchange.startBlocking();
    String method = exchange.getAttachment(FlitHandler.KEY_METHOD);
    try {
      switch (method) {
        case "ListDependencies": handleListDependencies(exchange); break;
        default: throw FlitException.builder().withErrorCode(ErrorCode.BAD_ROUTE).withMessage("No such route").build();
      }
    } catch (FlitException e) {
      errorWriter.write(e, exchange);
    } catch (Exception e) {
      LOGGER.error("Exception caught at handler: {}", e.getMessage(), e);
      errorWriter.write(e, exchange);
    }
  }

  private void handleListDependencies(HttpServerExchange exchange) throws Exception {
    boolean json = false;
    final Update.ListDependenciesRequest data;
    final String contentType = exchange.getRequestHeaders().get(Headers.CONTENT_TYPE).getFirst();
    if (contentType.equals("application/protobuf")) {
      data = Update.ListDependenciesRequest.parseFrom(exchange.getInputStream());
    } else if (contentType.startsWith("application/json")) {
      json = true;
      Update.ListDependenciesRequest.Builder builder = Update.ListDependenciesRequest.newBuilder();
      JsonFormat.parser().merge(new InputStreamReader(exchange.getInputStream(), StandardCharsets.UTF_8), builder);
      data = builder.build();
    } else {
      exchange.setStatusCode(415);
      return;
    }
    Update.ListDependenciesResponse response = service.handleListDependencies(data);
    exchange.setStatusCode(200);
    if (json) {
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json;charset=UTF-8");
      exchange.getResponseSender().send(JsonFormat.printer().omittingInsignificantWhitespace().print(response));
    } else {
      exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/protobuf");
      response.writeTo(exchange.getOutputStream());
    }
  }
}
