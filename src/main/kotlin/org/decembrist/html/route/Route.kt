package org.decembrist.html.route

import org.decembrist.html.RouteComponent

class Route(val initRoute: String,
            val component: RouteComponent,
            val subRoutes: Array<Route>,
            val initRedirects: Array<String>) {

    var route: String = ""
        get() = if (field == "") initRoute else field

    var redirects: Array<String> = emptyArray()
        get() = if (field.isEmpty()) initRedirects else field

    fun isSubRouted() = subRoutes.isNotEmpty()

    fun isRedirected() = initRedirects.isNotEmpty()

    companion object {

        fun of(url: String,
               component: RouteComponent,
               redirectFrom: Array<String>? = null,
               subRoutes: Array<Route>? = null): Route {
            val builder = RouteBuilder(url, component)
            if (redirectFrom != null) {
                builder.withRedirects(redirectFrom)
            }
            if (subRoutes != null) {
                builder.withSubRoutes(subRoutes)
            }
            return builder.build()
        }

    }

}