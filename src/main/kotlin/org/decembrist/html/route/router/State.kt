package org.decembrist.html.route.router

interface State {

    val data: Any?

    val title: String

    val url: String?

}