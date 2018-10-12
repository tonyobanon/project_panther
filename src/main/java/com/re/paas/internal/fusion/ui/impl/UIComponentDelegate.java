package com.re.paas.internal.fusion.ui.impl;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.re.paas.api.annotations.BlockerTodo;
import com.re.paas.api.annotations.Note;
import com.re.paas.api.annotations.Todo;
import com.re.paas.api.classes.Exceptions;
import com.re.paas.api.classes.FluentArrayList;
import com.re.paas.api.classes.ResourceException;
import com.re.paas.api.cloud.CloudEnvironment;
import com.re.paas.api.fusion.server.Buffer;
import com.re.paas.api.fusion.server.Cookie;
import com.re.paas.api.fusion.server.HttpStatusCodes;
import com.re.paas.api.fusion.server.JsonObject;
import com.re.paas.api.fusion.server.RoutingContext;
import com.re.paas.api.fusion.services.Functionality;
import com.re.paas.api.fusion.ui.AbstractUIComponentDelegate;
import com.re.paas.api.logging.Logger;
import com.re.paas.api.spi.DelegateSpec;
import com.re.paas.api.spi.SpiTypes;
import com.re.paas.api.utils.Utils;
import com.re.paas.internal.classes.AppDirectory;
import com.re.paas.internal.classes.GsonFactory;
import com.re.paas.internal.classes.spec.BlobSpec;
import com.re.paas.internal.core.keys.CacheValues;
import com.re.paas.internal.filesystems.FileSystemProviderImpl;
import com.re.paas.internal.fusion.services.impl.FusionHelper;
import com.re.paas.internal.models.PlatformModel;

@DelegateSpec(dependencies = { SpiTypes.CLOUD_ENVIRONMENT, SpiTypes.FUNCTIONALITY })
public class UIComponentDelegate extends AbstractUIComponentDelegate {

	// Use Google CDN for caching instead, for fine grained control over cached data
	private static final boolean CACHE_WEB_CONTENT = true;
	private static final Integer DEFAULT_CACHE_MAX_AGE = 259200;

	// @DEV
	public static ExecutorService fileWatcherPool = null;
	// @DEV
	public static WatchService watchService = null;
	// @DEV
	private static final Path PROJECT_RESOURCES_FOLDER = Paths
			.get("C:/Users/Tony/Documents/workspace/ce/src/main/resources");

	protected static final String webFolder = "web/public_html";

	// @DEV
	protected static Path webFolderURI = AppDirectory.getPath(webFolder);

	public static String DEFAULT_INDEX_URI = "/";

	public static String NOT_FOUND_URI = "/404";

	public static String SETUP_URI_PREFIX = "/setup";
	public static String DEFAULT_SETUP_URI = SETUP_URI_PREFIX + "/one";

	public static String DEFAULT_CONSOLE_URI = "/console";

	public static String DEFAULT_LOGIN_URI = "/user-login";

	private static Pattern webpagePattern = Pattern
			.compile("\\A[a-zA-Z0-9]+([_]*[-]*[a-zA-Z0-9]+)*\\Q.\\E(html|htm|xhtml){1}\\z");
	// Let's be lenient a little with the fileName
	// .compile("\\A[a-zA-Z]+(\\Q_\\E[a-zA-Z]+)*\\Q.\\E[a-zA-Z]+\\z");

	public static Pattern webpageUriPattern = Pattern
			.compile("\\A\\Q/\\E[a-zA-Z]+[-]*[a-zA-Z]+(\\Q/\\E[a-zA-Z]+[-]*[a-zA-Z]+)*\\z");

	private static final Map<String, String> mimeTypes = new HashMap<String, String>();
	private static Tika TIKA_INSTANCE = new Tika();

	private static final Map<String, List<String>> routeParams = new HashMap<>();

	private static Multimap<String, String> functionalityToRoutesMappings = ArrayListMultimap.create();

	private static final Map<String, WebRouteSpec> routeFunctionalities = new HashMap<>();

	private static final Map<String, PageSpec> layoutMap = new LinkedHashMap<>();

	private static Map<String, BlobSpec> singleRoutesData = new HashMap<>();
	private static Map<String, Map<String, BlobSpec>> multiRoutesData = new HashMap<>();

	private static final Map<String, String> routes = new HashMap<>();

	public static final String USER_ID_PARAM_NAME = "x_uid";

	@Override
	public void destroy() {

		// Stop File watcher

		if (!CloudEnvironment.get().isProduction() && FileSystemProviderImpl.supportsWatchers()) {

			if (fileWatcherPool != null) {
				fileWatcherPool.shutdownNow();
			}

			if (watchService != null) {

				try {
					watchService.close();
				} catch (IOException e) {
					Exceptions.throwRuntime(e);
				}
			}
		}

	}

	@Todo("Create util for walking file tree")
	@Override
	public void init() {

		try {

			Gson gson = GsonFactory.getInstance();

			// Get Route functionalities

			JsonObject route_functionalities = new JsonObject(
					Utils.getString(Files.newInputStream(webFolderURI.resolve("route_functionalities.json"))));

			route_functionalities.forEach(e -> {

				// Map functionality to route. Note: only some routes have this mapping

				WebRouteSpec spec = gson.fromJson(e.getValue().toString(), WebRouteSpec.class).setUri(e.getKey());

				List<Functionality> allFunctionalities = new ArrayList<>(spec.getMin().size() + spec.getMax().size());
				allFunctionalities.addAll(spec.getMin());
				allFunctionalities.addAll(spec.getMax());

				allFunctionalities.forEach(f -> {

					if (!f.isFrontend()) {
						throw new RuntimeException("Only frontend functionalities are allowed");
					}

					functionalityToRoutesMappings.put(Functionality.toString(f), spec.getUri());
				});

				routeFunctionalities.put(e.getKey(), spec);
			});

			// Get Route params

			JsonObject route_params = new JsonObject(
					Utils.getString(Files.newInputStream(webFolderURI.resolve("route_params.json"))));

			route_params.forEach(e -> {

				List<String> params = gson.fromJson(e.getValue().toString(), new TypeToken<List<String>>() {
				}.getType());
				routeParams.put(e.getKey(), params);
			});

			// Walk tree inorder to expose all files in the <webFolderURI>

			Files.walkFileTree(webFolderURI, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

					Path path = webFolderURI.relativize(file);

					String uri = path.toString().replaceAll("\\\\", "/");

					if (webpagePattern.matcher(uri).matches()) {
						uri = toCanonicalURI(uri);
					} else {
						uri = "/" + uri;
					}

					routes.put(uri, path.toString());

					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path file, IOException e) throws IOException {
					if (e == null) {
						return FileVisitResult.CONTINUE;
					} else {
						// directory iteration failed
						throw e;
					}
				}
			});

			routes.put("/", "index.html");

			// Get web resource mapping

			JsonObject layout = new JsonObject(
					Utils.getString(Files.newInputStream(webFolderURI.resolve("layout.json"))));

			layout.forEach(e -> {

				PageSpec spec = gson.fromJson(e.getValue().toString(), PageSpec.class);

				layoutMap.put(e.getKey(), spec);

			});

			// For all entries, put data in <routesData>.

			layoutMap.forEach((uri, spec) -> {

				// Perform validations

				if (spec.isParent() && spec.getParents() != null && spec.getParents().size() > 1) {
					Exceptions.throwRuntime(new ResourceException(ResourceException.FAILED_VALIDATION,
							"Parent: " + uri + " cannot have multiple parents itself"));
					return;
				}

				try {

					if (spec.getParents() == null || spec.getParents().isEmpty()) {

						byte[] bytes = IOUtils
								.toByteArray(Files.newInputStream(webFolderURI.resolve(Paths.get(routes.get(uri)))));
						BlobSpec bs = new BlobSpec().setMimeType(TIKA_INSTANCE.detect(routes.get(uri))).setData(bytes);

						singleRoutesData.put(uri, bs);
						return;
					}

					if (!spec.isParent()) {
						Map<String, BlobSpec> parentData = multiRoutesData.get(uri);
						if (parentData == null) {
							multiRoutesData.put(uri, new HashMap<>());
						}
					}

					// The general contract is that parents should exist in resource_mapping.json,
					// before their children

					for (PageParentSpec parent : spec.getParents()) {

						String fileContent = null;

						Path path = webFolderURI.resolve(Paths.get(routes.get(uri)));

						fileContent = Utils.getString(Files.newInputStream(path));

						fileContent = mergeHtml(uri, parent, fileContent,

								new String(singleRoutesData.get(parent.getPath()).getData()));

						byte[] bytes = fileContent.getBytes();
						BlobSpec bs = new BlobSpec().setMimeType(TIKA_INSTANCE.detect(routes.get(uri))).setData(bytes);

						if (spec.isParent()) {
							singleRoutesData.put(uri, bs);
							break;
						} else {
							multiRoutesData.get(uri).put(parent.getPath(), bs);
						}
					}

				} catch (IOException ex) {
					Exceptions.throwRuntime(ex);
				}

			});

			// @Dev Watch files for uris in <singleRoutesData && multiRoutesData>:

			if (!CloudEnvironment.get().isProduction()) {
				
				if(!FileSystemProviderImpl.supportsWatchers()) {
					return;
				}

				startWatchService();

				List<String> watchedFolders = new ArrayList<>();

				List<String> allRoutes = new FluentArrayList<String>().withAll(singleRoutesData.keySet())
						.withAll(multiRoutesData.keySet());

				allRoutes.forEach(k -> {

					Path folder = PROJECT_RESOURCES_FOLDER.resolve(webFolder).resolve(Paths.get(routes.get(k)))
							.getParent();

					if (!watchedFolders.contains(folder.toString())) {
						try {
							folder.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
							watchedFolders.add(folder.toString());

						} catch (IOException ex) {
							Exceptions.throwRuntime(ex);
						}
					}
				});

				Logger.get().trace("Watching the following folders for changes: ");
				watchedFolders.forEach(s -> {
					Logger.get().trace("* " + s);
				});

				fileWatcherPool.execute(() -> {

					WatchKey key;
					try {
						while ((key = watchService.take()) != null) {
							for (WatchEvent<?> event : key.pollEvents()) {

								WatchEvent.Kind<?> kind = event.kind();
								if (kind == StandardWatchEventKinds.OVERFLOW) {
									continue;
								}

								@SuppressWarnings("unchecked")
								WatchEvent<Path> ev = (WatchEvent<Path>) event;
								Path file = Paths.get(key.watchable().toString()).resolve(ev.context());

								if (file.toFile().isDirectory()) {
									continue;
								}

								String uri = PROJECT_RESOURCES_FOLDER.resolve(webFolder).relativize(file).toString()
										.replaceAll("\\\\", "/");

								if (webpagePattern.matcher(uri).matches()) {
									uri = toCanonicalURI(uri);
								} else {
									uri = "/" + uri;
								}

								String fileContent = null;
								try {
									fileContent = Utils.getString(Files.newInputStream(file));

								} catch (IOException ex) {
									Exceptions.throwRuntime(ex);
								}

								onFileChange(uri, fileContent);

								Logger.get().debug("Refreshing " + uri);
							}
							key.reset();
						}
					} catch (InterruptedException e) {
						// Its totally fine. This file watcher thread was destroyed probably due to
						// application shutdown
					}
				});

			}

		} catch (IOException e1) {
			Exceptions.throwRuntime(e1);
		}

	}

	private static final void onFileChange(String uri, String fileContent) {

		PageSpec spec = layoutMap.get(uri);

		if (spec == null || spec.getParents() == null || spec.getParents().isEmpty()) {

			BlobSpec bs = new BlobSpec().setMimeType(TIKA_INSTANCE.detect(routes.get(uri)))
					.setData(fileContent.getBytes());
			singleRoutesData.put(uri, bs);

		} else {

			for (PageParentSpec parent : spec.getParents()) {

				String _fileContent = mergeHtml(uri, parent, fileContent,
						new String(singleRoutesData.get(parent.getPath()).getData()));

				byte[] bytes = _fileContent.getBytes();
				BlobSpec bs = new BlobSpec().setMimeType(TIKA_INSTANCE.detect(routes.get(uri))).setData(bytes);

				if (spec.isParent()) {
					singleRoutesData.put(uri, bs);
					break;
				} else {
					multiRoutesData.get(uri).put(parent.getPath(), bs);
				}
			}

		}

		// Check if this is a parent, and update all children, if necessary

		if (spec != null && spec.isParent()) {

			layoutMap.forEach((k, v) -> {

				if (v.getParents() == null) {
					return;
				}

				for (PageParentSpec parent : v.getParents()) {

					if (parent.getPath().equals(uri)) {
						String childContents = null;
						try {
							childContents = Utils
									.getString(Files.newInputStream(webFolderURI.resolve(Paths.get(routes.get(k)))));
						} catch (IOException ex) {
							Exceptions.throwRuntime(ex);
						}

						try {
							onFileChange(k, childContents);
						} catch (Exception ex) {
							ex.printStackTrace();
						}
					}

				}
			});
		}
	}

	private static String mergeHtml(String uri, PageParentSpec spec, String childContents, String parentContents) {

		if (spec.getIntersectionNodes() == null) {
			spec.setIntersectionNodes(FluentArrayList.asList(
					new IntersectionNode().setFrom("#ce-page-main-container").setTo("#ce-page-main-container")));
		}

		// Read files
		Document childPage = Jsoup.parse(childContents);
		Document parentPage = Jsoup.parse(parentContents);

		if (!spec.getIntersectionNodes().isEmpty()) {

			Element childHead = childPage.select("head").first();
			parentPage.select("head").first().append(childHead.html());

			Element childScripts = childPage.select("#ce-page-scripts").first();
			parentPage.select("body").first().append(childScripts.html());

		}

		for (IntersectionNode intersectionNode : spec.getIntersectionNodes()) {

			Element childMainContent = childPage.select(intersectionNode.getFrom()).first();
			parentPage.select(intersectionNode.getTo()).html(childMainContent.html());

			if ((!intersectionNode.getFrom().equals(intersectionNode.getTo()))
					&& !intersectionNode.getTo().equals("#ce-page-main-container")) {
				parentPage.select(intersectionNode.getTo()).attr("id",
						intersectionNode.getFrom().replaceFirst("#", ""));
			}

		}

		String originUrl = null;

		if (spec.getOriginUri() != null) {
			originUrl = spec.getOriginUri();
		} else if (spec.getIntersectionNodes().isEmpty() && spec.getSetOriginUri()) {
			originUrl = uri;
		}

		if (originUrl != null) {
			// Set origin Uri
			parentPage.select("body").first()
					.append("<script type='text/javascript'>CE_ORIGIN_URL='" + originUrl + "';</script>");
			return parentPage.toString();
		}

		return parentPage.toString();
	}

	private static String toCanonicalURI(String fileName) {
		return "/"

				// replace underscores
				+ fileName.replaceAll("_", "/")
						// remove file extensions
						.split(Pattern.quote("."))[0];
	}

	private static boolean handleAuth(RoutingContext ctx, List<Functionality> functionalities) {
		for (Functionality f : functionalities) {
			if (!handleAuth(ctx, f)) {
				return false;
			}
		}
		return true;
	}

	private static boolean handleAuth(RoutingContext ctx, Functionality functionality) {

		if (functionality != null && !functionality.requiresBasicAuth()) {
			return true;
		}

		// Login is required, at the barest minimum
		// Get sessionToken from either a cookie or request header
		String sessionToken = getSessionToken(ctx);

		if (sessionToken == null) {
			return false;
		}

		// Verify that sessionToken is valid
		Long userId = FusionHelper.getUserIdFromToken(sessionToken);

		if (userId == null) {
			return false;
		} else {

			if (
			/* No functionality provided */
			functionality == null ||
			/* Requires Basic Auth, but not Role-based auth */
					(functionality.requiresBasicAuth() && !functionality.requiresAuth())) {
				return true;
			}
		}

		if (functionality.requiresAuth()) {

			for (String roleName : FusionHelper.getRoles(userId)) {
				if (FusionHelper.isAccessAllowed(roleName, functionality)) {
					return true;
				}
			}
		}

		return false;
	}

	private static final String getSessionToken(RoutingContext ctx) {
		String sessionToken;
		try {
			sessionToken = ctx.getCookie(FusionHelper.sessionTokenName()).getValue();
		} catch (NullPointerException e) {
			sessionToken = ctx.request().getHeader(FusionHelper.sessionTokenName());
		}
		return sessionToken;
	}

	private static String getMimeType(String uri, String file) {
		String mimeType = mimeTypes.get(uri);
		if (mimeType == null) {
			mimeType = TIKA_INSTANCE.detect(file);
			mimeTypes.put(uri, mimeType);
		}
		return mimeType;
	}

	private String getLoginReturnUri(RoutingContext ctx) {

		boolean b = false;

		if (b) {
			return null;
		}

		String returnUriString = ctx.request().getParam("returnUrl");
		String returnUri = returnUriString != null ? URI.create(returnUriString).getPath() : null;

		boolean canAccess = false;

		if (returnUri != null) {
			// Verify that user has the right to view returnUrl
			canAccess = handleAuth(ctx, routeFunctionalities.get(returnUri).getMin());
		}

		if (!canAccess && handleAuth(ctx, (Functionality) null)) {

			// If a user is logged in, then fallback to a page he has access to view
			Functionality f = FusionHelper.getCanonicalFrontend(ctx);
			returnUriString = returnUri = getUri(f);

			canAccess = true;
		}

		if (!canAccess) {
			return null;
		} else {
			return returnUriString;
		}
	}

	@Note
	@BlockerTodo("Select a suitable functionality, instead of using [0],"
			+ " or investigate. If [] is empty, how do we fallback? Use the" + "server's failure handlers?")
	@Override
	public void handler(RoutingContext ctx) {

		String uri = ctx.request().path();

		if (webpageUriPattern.matcher(uri).matches() && !PlatformModel.isInstalled()
				&& !uri.startsWith(SETUP_URI_PREFIX)) {
			ctx.response().setStatusCode(HttpStatusCodes.SC_FOUND).putHeader("Location", DEFAULT_SETUP_URI).end();
			return;
		} else if (uri.equals("/")) {
			// @DEV
			uri = DEFAULT_CONSOLE_URI;
		}

		if (!routes.containsKey(uri) && !uri.equals(DEFAULT_CONSOLE_URI)) {

			if (webpageUriPattern.matcher(uri).matches()) {
				ctx.response().setStatusCode(HttpStatusCodes.SC_FOUND).putHeader("Location", NOT_FOUND_URI).end();
			} else {
				ctx.response().setStatusCode(HttpStatusCodes.SC_NOT_FOUND);
			}
			return;
		}

		if (CACHE_WEB_CONTENT) {
			// allow proxies to cache the data
			ctx.response().putHeader("Cache-Control", "public, max-age=" + DEFAULT_CACHE_MAX_AGE);
		} else {
			ctx.response().putHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		}

		// Improve security
		ctx.response()

				// prevents Internet Explorer from MIME -
				// sniffing a
				// response away from the declared content-type
				.putHeader("X-Content-Type-Options", "nosniff")
				// Strict HTTPS (for about ~6Months)
				.putHeader("Strict-Transport-Security", "max-age=" + 15768000)
				// IE8+ do not allow opening of attachments in
				// the context
				// of this resource
				.putHeader("X-Download-Options", "noopen")
				// enable XSS for IE
				.putHeader("X-XSS-Protection", "1; mode=block")
				// deny frames
				.putHeader("X-FRAME-OPTIONS", "DENY");

		WebRouteSpec spec = routeFunctionalities.get(uri);

		if (spec != null) {
			if (!handleAuth(ctx, spec.getMin())) {
				ctx.response().setStatusCode(HttpStatusCodes.SC_FOUND).putHeader("Location", DEFAULT_LOGIN_URI
						+ "?returnUrl=" + uri + (ctx.request().query() != null ? "?" + ctx.request().query() : ""))
						.end();
				return;
			}
		}

		if ((uri.equals(DEFAULT_LOGIN_URI))) {
			String returnUri = getLoginReturnUri(ctx);
			if (returnUri != null) {
				ctx.response().setStatusCode(HttpStatusCodes.SC_FOUND).putHeader("Location", returnUri).end();
				return;
			}

		}

		ctx.request().params().forEach(e -> {
			ctx.addCookie(new Cookie(e.getKey(), e.getValue()).setPath(ctx.request().path())
					.setMaxAge(CacheValues.REQUEST_PARAM_COOKIE_EXPIRY_IN_SECS));
		});

		if (uri.equals(DEFAULT_CONSOLE_URI)) {

			// Serve a page he has access to view
			Functionality f = FusionHelper.getCanonicalFrontend(ctx);
			ctx.reroute(getUri(f));

			return;
		}

		if (layoutMap.containsKey(uri)) {

			PageSpec pageSpec = layoutMap.get(uri);
			BlobSpec data = null;

			if (pageSpec == null || pageSpec.isParent() || pageSpec.getParents() == null
					|| pageSpec.getParents().isEmpty()) {
				data = singleRoutesData.get(uri);
			} else {

				for (PageParentSpec parent : pageSpec.getParents()) {

					if (parent.getParams().isEmpty()) {
						if (ctx.request().params().isEmpty()) {
							data = multiRoutesData.get(uri).get(parent.getPath());
							break;
						}
					} else {

						boolean paramsFound = true;

						for (String param : parent.getParams()) {
							if (ctx.request().getParam(param) == null) {
								paramsFound = false;
								break;
							}
						}

						if (paramsFound && parent.getParams().size() == ctx.request().params().size()) {
							data = multiRoutesData.get(uri).get(parent.getPath());
							break;
						}
					}

				}
			}

			if (data != null) {
				ctx.response().putHeader("content-type", data.getMimeType()).write(Buffer.buffer(data.getData()));
			} else {
				ctx.reroute("/404");
			}

		} else {

			String file = routes.get(uri);
			Path p = webFolderURI.resolve(file);

			try {
				byte[] bytes = IOUtils.toByteArray(p.toUri());
				ctx.response().putHeader("content-type", getMimeType(uri, file)).write(Buffer.buffer(bytes));
			} catch (IOException e) {
				Exceptions.throwRuntime(e);
			}
		}
	}

	@Override
	public List<String> getUriParams(String uri) {
		return routeParams.get(uri);
	}

	@Override
	public String getUri(Functionality f) {
		return functionalityToRoutesMappings.asMap().get(Functionality.toString(f)).iterator().next();
	}

	private static void startWatchService() {
		
		try {
			watchService = FileSystems.getDefault().newWatchService();
			fileWatcherPool = Executors.newFixedThreadPool(1);
		} catch (IOException e) {
			Exceptions.throwRuntime(e);
		}
	}
}
