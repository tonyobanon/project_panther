package com.re.paas.api.fusion;

public interface StaticFileContext {

	String appId();

	String staticPath();

	HttpServerRequest request();

	HttpServerResponse response();

}