package org.decembrist.html.utils.component

import org.decembrist.html.Component
import org.decembrist.html.ComponentTagConsumer
import org.decembrist.html.RouteComponent
import org.decembrist.html.component.ParagraphComponent
import org.decembrist.html.component.ShowHideComponent
import org.decembrist.html.utils.observable.IObservableValue

internal fun Component.checkInit() {
    if (init) {
        if (this is RouteComponent) {
            throw IllegalArgumentException("It looks like you try to add route component manually")
        } else throw IllegalArgumentException("Component should have not been initialized")
    }
}

fun ComponentTagConsumer.paragraph(name: String? = null,
                                   classes: Array<String> = arrayOf(),
                                   showIf: IObservableValue<Boolean>? = null): ParagraphComponent {
    return component(ParagraphComponent(name, classes, showIf))
}

fun ComponentTagConsumer.showHideComponent(name: String? = null,
                                           classes: Array<String> = arrayOf(),
                                           showIf: IObservableValue<Boolean>,
                                           content: ComponentTagConsumer.() -> Unit = {}): ShowHideComponent {
    return component(ShowHideComponent(name, classes, showIf, content))
}

fun ComponentTagConsumer.showHideComponent(name: String? = null,
                                           classes: Array<String> = arrayOf(),
                                           initialVisibility: Boolean,
                                           content: ComponentTagConsumer.() -> Unit = {}): ShowHideComponent {
    return component(ShowHideComponent(name, classes, initialVisibility, content))
}