package org.decembrist.html

import org.decembrist.html.route.router.Router
import org.decembrist.html.utils.observable.IObservableValue

open class RouteComponent(name: String? = null,
                              classes: Array<String> = arrayOf(),
                              showIf: IObservableValue<Boolean>? = null,
                              content: ComponentTagConsumer.() -> Unit = {})
    : Component(name, classes, showIf, content) {

    lateinit var router: Router

    open fun onVisit(arguments: Array<String>) {

    }

    open fun onLeave() {

    }
}