package org.decembrist.html.component

import org.decembrist.html.Component
import org.decembrist.html.ComponentTagConsumer
import org.decembrist.html.utils.observable.IObservableValue
import org.decembrist.html.utils.observable.Observable

open class ShowHideComponent(name: String? = null,
                             classes: Array<String> = arrayOf(),
                             showIf: IObservableValue<Boolean>,
                             content: ComponentTagConsumer.() -> Unit = {})
    : Component(name, classes, showIf, content) {

    constructor(name: String? = null,
                classes: Array<String> = arrayOf(),
                initialVisibility: Boolean,
                content: ComponentTagConsumer.() -> Unit = {})
            : this(name, classes, Observable.of(initialVisibility), content)

    private val visibility: IObservableValue<Boolean> = showIf

    open fun show() {
        visibility.value = true
    }

    open fun hide() {
        visibility.value = false
    }

}