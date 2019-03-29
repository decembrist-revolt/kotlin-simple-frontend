package org.decembrist.html.route.router

import org.w3c.dom.HTMLElement
import org.decembrist.html.RouteComponent
import org.decembrist.html.appendComponent
import org.decembrist.html.bootstrap.SimpleFrontend
import org.decembrist.html.route.Route
import kotlin.browser.window
import kotlin.dom.clear

class Router internal constructor(
    private val mode: RouterMode,
    private val routes: List<Route>,
    private val root: String,
    private val container: HTMLElement
) {

    private var currentRoute: RouteComponent? = null

    private lateinit var lastState: State

    val currentPath
        get() = window.location.href.replace(window.location.origin, "")

    init {
        SimpleFrontend.stateObservable.subscribe { state ->
            if (state != null) {
                lastState = state
                check(state.url)
            }
        }
        for (route in routes) {
            route.component.router = this
        }
        instance = this
    }

    fun navigate(path: String = "", useCurrent: Boolean = false): Router {
        val navigatePath = if (useCurrent) {
            "$currentPath/${path.trimStart('/')}"
        } else path

        if (mode == RouterMode.HISTORY) {
            console.log("navigate", path, useCurrent)
            window.history.pushState(null, js("null"), "$root${navigatePath.clearSlashes()}")
        } else {
            window.location.href = window.location.href.replace(Regex("$SHARP(.*)$"), "") + SHARP
        }
        return this
    }

    fun back(): Router {
        window.history.back()
        return this

    }

    fun check(uri: String? = null, root: String = this.root, routes: Collection<Route> = this.routes): Boolean {
        val fragment = uri ?: getFragment(root)
        for (route in routes) {
            var match = fragment.match(route.route)
            if (match == null) {
                match = matchRedirect(route, fragment)
                if (match != null) {
                    fixHistory(route.route)
                }
            }
            if (match != null) {
                currentRoute?.onLeave()
                route.component.onVisit(match)
                currentRoute = route.component
                container.clear()
                appendComponent(container, route.component)
                return true
            }
        }
        return false
    }

    private fun matchRedirect(route: Route, fragment: String): Array<String>? {
        return if (route.isRedirected()) {
            var redirectMatch: Array<String>?
            for (redirect in route.redirects) {
                redirectMatch = fragment.match(redirect)
                if (redirectMatch != null) {
                    return redirectMatch
                }
            }
            return null
        } else null
    }

    private fun matchSubRoutes(uri: String?, route: Route): Boolean {
        return if (route.isSubRouted()) {
            val newRoot = if (root.endsWith("/")) {
                "${root.substringBeforeLast("/")}/${route.initRoute.trimStart('/')}"
            } else "$root/${route.initRoute.trimStart('/')}"
            check(uri, newRoot, route.subRoutes.toList())
        } else false
    }

    private fun getFragment(root: String): String {
        val location = window.location
        val fragment = if (mode == RouterMode.HISTORY) {
            val fragment = window.asDynamic()
                .decodeURI(location.pathname + location.search)
                .clearSlashes()
                .replace(Regex("\\?(.*)$"), "") as String
            if (root != "/") fragment.replace(root, "") else fragment
        } else {
            val match = window.location.href.match("#(.*)$")
            match?.get(1) ?: ""
        }
        return fragment.clearSlashes()
    }

    private fun fixHistory(url: String) {
        window.history.replaceState(lastState.data, lastState.title, url)
    }

    private fun String.clearSlashes() = trim('/')

    companion object {

        internal lateinit var instance: Router

        private const val SHARP = "#"

    }

}