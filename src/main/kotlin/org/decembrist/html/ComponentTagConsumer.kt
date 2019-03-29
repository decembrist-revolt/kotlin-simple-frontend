package org.decembrist.html

import kotlinx.html.Tag
import kotlinx.html.TagConsumer
import org.decembrist.html.route.router.Router
import org.w3c.dom.HTMLElement
import org.decembrist.html.utils.observable.IObservableValue
import org.decembrist.html.utils.component.checkInit

class ComponentTagConsumer(private val tagConsumer: TagConsumer<HTMLElement>,
                           component: Component)
    : TagConsumer<HTMLElement> by tagConsumer {

    val router = Router.instance

    private val componentStack = mutableListOf(component)

    override fun onTagStart(tag: Tag) {
        tagConsumer.onTagStart(tag)
        if (componentStack.last().init.not()) {
            val elements = tagConsumer.asDynamic()
                    .downstream
                    .path_0.unsafeCast<ArrayList<HTMLElement>>()
            componentStack.last().onFinalize(elements.last())
        }
    }

    fun <T : Component> component(component: T): T {
        component.checkInit()
        componentStack.last().addChild(component)
        val consumer: ComponentTagConsumer.() -> Unit = {
            startComponent(component)
            component.invokeContent(this)
            endComponent()
        }
        consumer.invoke(this)
        return component
    }

    fun component(name: String? = null,
                  classes: Array<String> = arrayOf(),
                  showIf: IObservableValue<Boolean>? = null,
                  content: ComponentTagConsumer.() -> Unit): Component {
        val component = Component(name, classes, showIf, content)
        return component(component)
    }

    private fun startComponent(component: Component) {
        componentStack.add(component)
    }

    private fun endComponent() {
        componentStack.removeAt(componentStack.lastIndex)
    }
}