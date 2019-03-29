package org.decembrist.html.component

import kotlinx.html.p
import org.decembrist.html.Component
import org.decembrist.html.ComponentTagConsumer
import org.decembrist.html.utils.observable.IObservableValue

class ParagraphComponent(name: String? = null,
                         classes: Array<String> = arrayOf(),
                         showIf: IObservableValue<Boolean>? = null) : Component(name, classes, showIf) {

    override val content: ComponentTagConsumer.() -> Unit = {
        p(classes.joinToString(" "))
    }

    var text: String
        set(value) {
            element.innerText = value
        }
        get() = element.innerText

}