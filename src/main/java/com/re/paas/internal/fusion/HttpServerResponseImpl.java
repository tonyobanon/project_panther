package com.re.paas.internal.fusion;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.ObjectWrapper;
import com.re.paas.api.fusion.Cookie;
import com.re.paas.api.fusion.HttpServerResponse;
import com.re.paas.api.fusion.components.BaseComponent;
import com.re.paas.api.fusion.components.NodeUtil;
import com.re.paas.api.fusion.components.RecordNode;
import com.re.paas.api.fusion.components.ScalarNode;
import com.re.paas.api.utils.SerializationUtil;
import com.re.paas.internal.fusion.components.EnumAdapterFactory;
import com.re.paas.internal.runtime.spi.CustomClassLoader;
import com.re.paas.internal.runtime.spi.FusionClassloaders;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class HttpServerResponseImpl implements HttpServerResponse {

	private static final long serialVersionUID = 1L;

	HttpServletRequest req;
	HttpServletResponse resp;

	HttpServerResponseImpl(HttpServletRequest request, HttpServletResponse response) {
		this.req = request;
		this.resp = response;
	}

	@Override
	public void addCookie(Cookie cookie) {
		ensureNotCommitted();
		this.resp.addCookie(CookieHelper.toServletCookie(cookie));
	}

	@Override
	public int getStatus() {
		return this.resp.getStatus();
	}

	@Override
	public HttpServerResponse setStatus(int statusCode) {
		ensureNotCommitted();

		this.resp.setStatus(statusCode);
		return this;
	}

	@Override
	public Collection<String> getHeaderNames() {
		return this.resp.getHeaderNames();
	}

	@Override
	public Collection<String> getHeaders(String name) {
		return this.resp.getHeaders(name);
	}

	@Override
	public HttpServerResponse setHeader(String name, String value) {
		ensureNotCommitted();

		this.resp.setHeader(name, value);
		return this;
	}

	@Override
	public HttpServerResponse addHeader(String name, String value) {
		ensureNotCommitted();

		this.resp.addHeader(name, value);
		return this;
	}

	@Override
	public HttpServerResponse writeHtml(String contents) {
		return write("text/html", contents.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public HttpServerResponse write(String contentType, byte[] bytes) {
		ensureNotCommitted();

		try {

			setContentType(contentType);
			setContentLength(bytes.length);

			getOutputStream().write(bytes);
			getOutputStream().flush();

		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}

		return this;
	}

	@Override
	public boolean isCommited() {
		return this.resp.isCommitted();
	}

	@Override
	public Locale getLocale() {
		return this.resp.getLocale();
	}

	@Override
	public HttpServerResponse setLocale(Locale locale) {
		this.resp.setLocale(locale);
		return this;
	}

	@Override
	public void reset() {
		this.resp.reset();
	}

	@Override
	public void flushBuffer() {
		ensureNotCommitted();

		try {
			this.resp.flushBuffer();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}

	@Override
	public void resetBuffer() {
		this.resp.resetBuffer();
	}

	@Override
	public void setBufferSize(int size) {
		this.resp.setBufferSize(size);
	}

	@Override
	public int getBufferSize() {
		return this.resp.getBufferSize();
	}

	@Override
	public OutputStream getOutputStream() {
		try {
			return this.resp.getOutputStream();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	@Override
	public String getContentType() {
		return this.resp.getContentType();
	}

	@Override
	public HttpServerResponse setContentType(String type) {
		this.resp.setContentType(type);
		return this;
	}

	@Override
	public HttpServerResponse setContentLength(int len) {
		this.resp.setContentLength(len);
		return this;
	}

	@Override
	public HttpServerResponse setContentLengthLong(long len) {
		this.resp.setContentLengthLong(len);
		return this;
	}

	@Override
	public PrintWriter getWriter() {
		try {
			return this.resp.getWriter();
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
			return null;
		}
	}

	private static Boolean isRtlLocale(Locale l) {
		// Todo
		return false;
	}

	@Override
	public HttpServerResponse render(BaseComponent<?> component, Boolean testMode) {

		CustomClassLoader cl = (CustomClassLoader) component.getClass().getClassLoader();

		String appId = cl.getName();

		String assetId = component.getAssetId();

		Boolean rtl = isRtlLocale(req.getLocale());

		String contents = FusionClassloaders.getComponentResourceFile(appId, assetId, "client.html");

		// Add RTL setting
		contents = contents.replace("\"{{rtl}}\"", Boolean.toString(rtl));

		// Set testMode
		contents = contents.replace("\"{{testMode}}\"", Boolean.toString(testMode));

		if (!testMode) {

			component.setSessionId(req.getSession().getId());

			String data = HttpServerResponseImpl.serializeComponent(appId, component);

			contents = contents.replace("\"{{data}}\"", "() => (" + data + ")");
		}

		// This is required to load assets URL since they are not contextualized with
		// appId
		addCookie(new CookieImpl(FusionClassloaders.APP_ID_COOKIE, appId).setPath("/").setMaxAge(84900));

		writeHtml(contents);

		return this;
	}

	@Override
	public HttpServerResponse render(BaseComponent<?> component) {
		return render(component, false);
	}

	private void ensureNotCommitted() {
		if (this.resp.isCommitted()) {
			throw new IllegalStateException("The response has already been committed, and cannot be written to");
		}
	}

	private static String serializeComponent(String appId, BaseComponent<?> component) {

		final List<BaseComponent<?>> componentList = new ArrayList<>();

		GsonBuilder gsonBuilder = new GsonBuilder();
		ObjectWrapper<Gson> gson = new ObjectWrapper<>();

		gsonBuilder.registerTypeAdapter(Date.class, new JsonSerializer<Date>() {
			@Override
			public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
				return context.serialize(SerializationUtil.toDateString(src));
			}
		});

		gsonBuilder.registerTypeAdapter(BaseComponent.class, new JsonSerializer<BaseComponent<?>>() {
			@Override
			public JsonElement serialize(BaseComponent<?> src, Type typeOfSrc, JsonSerializationContext context) {

				if (componentList.contains(src)) {
					return new JsonPrimitive("""
							%% BaseRenderer.getComponent("$id")%%""".replace("$id", src.getId()));
				} else {
					componentList.add(src);

					String input = gson.get().toJson(context.serialize(src, RecordNode.class));
					String config = gson.get().toJson(context.serialize(src.getConfig()));

					return new JsonPrimitive("""
							%% new components["$type"]({ id: "$id", input: $input, config: $config })%%"""
							.replace("$type", src.getClass().getSimpleName()).replace("$id", src.getId())
							.replace("$input", input).replace("$config", config));
				}
			}

		});

		gsonBuilder.registerTypeAdapter(RecordNode.class, new JsonSerializer<RecordNode<?>>() {
			@Override
			public JsonElement serialize(RecordNode<?> src, Type typeOfSrc, JsonSerializationContext context) {

				JsonObject o = new JsonObject();

				src.getChildren().forEach(field -> {
					
					var key = field.getKey();
					var value = field.getValue();

					if (value instanceof BaseComponent) {

						var component = ((BaseComponent<?>) value);

						JsonObject componentConfig = new JsonParser().parse(FusionClassloaders
								.getComponentResourceFile(appId, NodeUtil.getComponent(src).getAssetId(), "config.json"))
								.getAsJsonObject();

						JsonElement pathConfig = componentConfig.get("scalars").getAsJsonObject().get(key)
								.getAsJsonObject().get("config");

						if (!pathConfig.isJsonNull()) {
							pathConfig.getAsJsonObject().entrySet().forEach(e2 -> {
								component.addConfig(e2.getKey(), e2.getValue());
							});
						}
					}

					o.add(key, context.serialize(value));
				});

				return o;
			}
		});

		gsonBuilder.registerTypeAdapter(ScalarNode.class, new JsonSerializer<ScalarNode<?>>() {
			@Override
			public JsonElement serialize(ScalarNode<?> src, Type typeOfSrc, JsonSerializationContext context) {
				return context.serialize(src.getValue());
			}
		});

		gsonBuilder.registerTypeAdapterFactory(new EnumAdapterFactory());

		gson.set(gsonBuilder.create());

		return gson.get().toJson(component).replaceAll("\\\\\"", "\"").replaceAll("\"\\s*%%", "")
				.replaceAll("%%\\s*\"", "").replaceAll("\\n", "");
	}
}
