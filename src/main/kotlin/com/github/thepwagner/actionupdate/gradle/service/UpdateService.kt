package com.github.thepwagner.actionupdate.gradle.service

import com.github.thepwagner.actionupdate.v1.rpc.RpcUpdateService
import com.github.thepwagner.actionupdate.v1.rpc.Update.*
import com.github.thepwagner.actionupdate.v1.rpc.Update.Dependency

class UpdateService : RpcUpdateService {
    var dep: Dependency? = null
    override fun handleListDependencies(req: ListDependenciesRequest?): ListDependenciesResponse {
        val foo = Dependency.newBuilder()
            .setPath("foo")
            .setVersion("2.0.0")
        val bar = Dependency.newBuilder()
            .setPath("bar")
            .setVersion("2.0.0")
        return ListDependenciesResponse.newBuilder()
            .addDependencies(foo)
            .addDependencies(bar)
            .build()
    }
}
