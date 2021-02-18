package com.github.thepwagner.actionupdate.gradle

import com.github.thepwagner.actionupdate.v1.rpc.RpcUpdateServiceHandler
import com.flit.runtime.undertow.FlitHandler;
import io.undertow.Undertow;
import com.github.thepwagner.actionupdate.gradle.service.UpdateService

fun main(args : Array<String>) {
    val portVar = (System.getenv("ACTION_UPDATE_TWIRP_PORT") ?: "9600").toInt()

    val server = Undertow.builder()
        .addHttpListener(portVar, "0.0.0.0")
        .setHandler(FlitHandler.Builder()
            .withNext(null)
            .withRoute(RpcUpdateServiceHandler.ROUTE, RpcUpdateServiceHandler(UpdateService()))
            .build()
        )
        .build()
    server.start()
}