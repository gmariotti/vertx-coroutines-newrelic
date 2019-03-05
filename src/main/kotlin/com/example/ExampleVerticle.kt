package com.example

import io.vertx.core.eventbus.Message
import io.vertx.core.eventbus.MessageConsumer
import io.vertx.kotlin.coroutines.CoroutineVerticle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val ADDRESS = "address"
class ExampleVerticle : CoroutineVerticle() {

    override suspend fun start() {
        vertx
            .eventBus()
            .consumer<Int>(ADDRESS)
            .addSuspendingHandler(this, this::exampleFunction)
    }

    private suspend fun exampleFunction(message: Message<Int>) {
        message.reply(0)
    }
}

private fun <T> MessageConsumer<T>.addSuspendingHandler(scope: CoroutineScope, handler: suspend (Message<T>) -> Unit) {
    handler { message ->
        scope.launch {
            try {
                handler(message)
            } catch (exception: Exception) {
                message.fail(1, exception.message)
            }
        }
    }
}