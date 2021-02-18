package com.github.thepwagner.actionupdate.v1.rpc;

public interface RpcUpdateService {
  Update.ListDependenciesResponse handleListDependencies(
      Update.ListDependenciesRequest in);
}
