package com.example

import io.vertx.core.Vertx
import io.vertx.kotlin.core.deployVerticleAwait

suspend fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticleAwait("com.example.HttpVerticle")
    vertx.deployVerticleAwait("com.example.ExampleVerticle")
}