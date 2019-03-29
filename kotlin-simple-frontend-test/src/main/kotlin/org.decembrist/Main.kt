package org.decembrist

import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.js.onClickFunction
import org.decembrist.html.Component
import org.decembrist.html.RouteComponent
import org.decembrist.html.bootstrap.SimpleFrontend
import org.decembrist.html.route.Route
import org.decembrist.html.route.router.RouterBuilder
import org.decembrist.html.route.router.RouterMode

fun main() {
    val mainPage = RouteComponent(content = {
        div {
            div { text("main page") }
            button {
                text("sub-route1")
                onClickFunction = {
                    router.navigate("/sub-route1", true)
                }
            }
        }
    })
    val subRoute1 = RouteComponent(content = {
        div {
            div { text("sub-route1") }
            button {
                text("back")
                onClickFunction = {
                    router.navigate("/main")
                }
            }
        }
    })
    SimpleFrontend.bootstrap(
        RouterBuilder(RouterMode.HISTORY, "/", "main-contatiner")
            .addRoute(
                Route.of(
                    "/main", mainPage, arrayOf("/"), arrayOf(
                        Route.of(
                            "/sub-route1", subRoute1
                        )
                    )
                )
            ).build()
    )
}