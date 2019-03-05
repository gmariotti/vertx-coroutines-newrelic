package com.example

import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaderValues.APPLICATION_JSON
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory
import io.vertx.kotlin.core.eventbus.sendAwait
import io.vertx.kotlin.core.json.JsonObject
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.ext.web.api.contract.openapi3.OpenAPI3RouterFactory.createAwait
import kotlinx.coroutines.launch

class HttpVerticle : CoroutineVerticle() {

    override suspend fun start() {
        val router = createAwait(vertx, "/openapi.yaml")
            .addSuspendingHandler("example", this::exampleFunction)
            .router
        vertx.createHttpServer().requestHandler(router).listen(8080)
    }

    private suspend fun exampleFunction(routingContext: RoutingContext) {
        vertx.eventBus().sendAwait<Int>(ADDRESS, 0)
        routingContext.response().putHeader(CONTENT_TYPE, APPLICATION_JSON).end(JsonObject().encode())
    }

    private fun OpenAPI3RouterFactory.addSuspendingHandler(
        operationId: String,
        handler: suspend (RoutingContext) -> Unit
    ) = addHandlerByOperationId(operationId) { context ->
        launch {
            try {
                handler(context)
            } catch (exception: Exception) {
                context.fail(exception)
            }
        }
    }
}