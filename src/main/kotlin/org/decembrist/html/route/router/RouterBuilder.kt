package org.decembrist.html.route.router

import org.decembrist.html.bootstrap.SimpleFrontend
import org.w3c.dom.HTMLElement
import org.decembrist.html.route.Route
import kotlin.browser.document
import kotlin.js.Promise

class RouterBuilder(
    mode: RouterMode = RouterMode.HISTORY,
    private val root: String = "/",
    private val containerId: String? = null
) {

    private val redirectMap = mutableMapOf<String, String>()

    private val mode = if (mode == RouterMode.HISTORY && js("!!history.pushState") as Boolean) {
        RouterMode.HISTORY
    } else RouterMode.HASH

    private val routes = mutableListOf<Route>()

    fun addRoute(route: Route): RouterBuilder {
        routes.add(route)
        return this
    }

    fun redirect(source: String, destination: String) {
        redirectMap[source] = destination
    }

    fun build(): Promise<Router> {
        checkDuplicates()
        checkRedirects()
        val routes = flatRoutes(routes, "")
        return Promise { resolve, reject ->
            SimpleFrontend.invokeOnLoad {
                val container = if (containerId != null) {
                    val element = document.getElementById(containerId)
                    element as? HTMLElement ?: throw RuntimeException("Container with id $containerId not found")
                } else {
                    document.body!!
                }
                val router = Router(mode, routes, root, container)
                resolve(router)
            }
        }
    }

    private fun flatRoutes(routes: Iterable<Route>, parent: String): List<Route> {
        val trimmedParent = parent.trim('/')
        for (route in routes) {
            val trimmedRoute = route.initRoute.trim('/')
            route.route = if (parent != "") {
                "/$trimmedParent/$trimmedRoute"
            } else {
                "/$trimmedRoute"
            }
            route.redirects = if (parent != "") {
                route.initRedirects.map { redirect ->
                    "/$trimmedParent/${redirect.trim('/')}"
                }.toTypedArray()
            } else {
                route.initRedirects
            }
        }
        return routes
            .filter { it.subRoutes.isNotEmpty() }
            .map { flatRoutes(it.subRoutes.asIterable(), it.route) }
            .flatten()
            .plus(routes)
    }

    private fun checkDuplicates() {
        val routesDist = routes.map(Route::initRoute)
        if (routesDist.size != routesDist.distinct().size) {
            val duplicate = routesDist.first { dist -> routesDist.count { it == dist } > 1 }
            throw IllegalArgumentException("$duplicate is found more than once")
        }
    }

    private fun checkRedirects() {
        val routesDist = routes.map(Route::initRoute)
        val redirects = routes
            .filter(Route::isRedirected)
            .map(Route::initRedirects)
            .flatMap(Array<String>::asIterable)
        val intersect = routesDist.any { redirects.contains(it) }
        if (intersect) throw IllegalArgumentException("One of initRedirects in initRoute list")
    }

}