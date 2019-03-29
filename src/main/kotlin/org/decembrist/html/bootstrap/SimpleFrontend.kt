package org.decembrist.html.bootstrap

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.decembrist.html.route.router.Router
import org.decembrist.html.route.router.State
import org.decembrist.html.utils.observable.Observable
import org.w3c.dom.COMPLETE
import org.w3c.dom.DocumentReadyState
import org.w3c.dom.INTERACTIVE
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Promise

class SimpleFrontend private constructor(router: Promise<Router>, navigate: Boolean) {

    private val onLoadCalls: MutableSet<() -> Unit> = mutableSetOf()

    init {
        window.onload = {
            onLoadCalls.forEach { it() }
            GlobalScope.launch {
                if (navigate) router.await().navigate()
            }
        }
    }

    companion object {

        private var fronted: SimpleFrontend? = null

        private val onInitCalls: MutableSet<(SimpleFrontend) -> Unit> = mutableSetOf()

        internal val stateObservable = {
            val state = Observable.of<State?>(null)
            val pushState = window.history.asDynamic().pushState.bind(window.history)
            window.history.asDynamic().pushState = { data: Any?, title: String, url: String? ->
                console.log("PushState data, title, url^", data, title, url)
                pushState(data, title, url)
                state.value = object : State {
                    override val data = data
                    override val title = title
                    override val url = url
                }
            }
            val back = window.history.asDynamic().back.bind(window.history)
            window.history.asDynamic().back = {
                console.log("Back state")
                back()
                console.log("STATE ", window.history.state)
            }
            state
        }()

        fun bootstrap(router: Promise<Router>, navigate: Boolean = true) {
            fronted = SimpleFrontend(router, navigate)
            onInitCalls.forEach { it(fronted!!) }
        }

        internal fun invokeOnLoad(callback: () -> Unit) {
            if (checkLoaded()) {
                callback()
            } else {
                fronted?.onLoadCalls?.add(callback) ?: onInitCalls.add { fronted ->
                    fronted.onLoadCalls.add(callback)
                }
            }
        }

        internal fun checkLoaded(): Boolean = document.readyState == DocumentReadyState.COMPLETE
                || document.readyState === DocumentReadyState.INTERACTIVE

    }

}