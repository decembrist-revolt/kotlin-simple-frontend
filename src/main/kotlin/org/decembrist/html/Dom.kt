package org.decembrist.html

import kotlinx.html.consumers.onFinalize
import kotlinx.html.dom.createTree
import org.w3c.dom.Document
import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.decembrist.html.utils.component.checkInit

fun HTMLElement.appendComponent(component: Component) {
    component.checkInit()
    appendComponent(this, component)
}

fun HTMLElement.prependComponent(component: Component) {
    component.checkInit()
    val tree = ownerDocumentExt
            .createTree()
            .onFinalize { element, partial ->
                if (!partial) {
                    this@prependComponent.insertBefore(element, this@prependComponent.firstChild)
                    component.onFinalize(element)
                }
            }
    component.invokeContent(ComponentTagConsumer(tree, component))
}

internal fun showRoute() {

}

internal fun appendComponent(parent: HTMLElement, component: Component) {
    component.checkInit()
    val tree = parent.ownerDocumentExt
            .createTree()
            .onFinalize { element, partial ->
                if (!partial) {
                    parent.appendChild(element)
                    component.onFinalize(element)
                }
            }
    component.invokeContent(ComponentTagConsumer(tree, component))
}

internal fun appendComponent(parent: HTMLElement, component: RouteComponent) {
    val tree = parent.ownerDocumentExt
        .createTree()
        .onFinalize { element, partial ->
            if (!partial) {
                parent.appendChild(element)
                if (!component.init) component.onFinalize(element)
            }
        }
    component.invokeContent(ComponentTagConsumer(tree, component))
}

private val Node.ownerDocumentExt: Document
    get() = when {
        this is Document -> this
        else -> ownerDocument ?: throw IllegalStateException("Node has no ownerDocument")
    }