package org.decembrist.html.route

import org.decembrist.html.RouteComponent

internal class RouteBuilder(val url: String,
                            val component: RouteComponent) {

    private var redirects: Array<String>? = null

    private var subRoutes: Array<Route>? = null

    fun withRedirects(redirects: Array<String>) {
        this.redirects = redirects
    }

    fun withSubRoutes(subRoutes: Array<Route>) {
        this.subRoutes = subRoutes
    }

    fun build() = Route(
            url,
            component,
            subRoutes ?: emptyArray(),
            redirects ?: emptyArray()
    )

}