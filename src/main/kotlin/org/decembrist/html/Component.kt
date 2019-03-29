package org.decembrist.html

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.html.div
import org.w3c.dom.HTMLElement
import org.decembrist.html.utils.observable.AbstractObserver
import org.decembrist.html.utils.observable.IObservableValue


open class Component(val name: String? = null,
                     val classes: Array<String> = arrayOf(),
                     showIf: IObservableValue<Boolean>? = null,
                     content: ComponentTagConsumer.() -> Unit = {}) {

    protected open val content: ComponentTagConsumer.() -> Unit = {
        val tagConsumer = this
        div(classes = classes.joinToString(" ")) {
            content.invoke(tagConsumer)
        }
    }

    val children
        get() = childrenList.toList()

    val visible: Boolean
        get() = element.style.display != DISPLAY_NONE

    var init: Boolean = false
        private set

    lateinit var element: HTMLElement

    private lateinit var oldDisplay: String

    private var showIfObserver: AbstractObserver<Boolean>? = null
    private val initActions = mutableListOf<Component.() -> Unit>()
    private val childrenMap = mutableMapOf<String, Component>()
    private val childrenList = mutableListOf<Component>()

    init {
        if (showIf != null) {
            setShowIf(showIf)
        }
    }

    fun invokeContent(tagConsumer: ComponentTagConsumer) {
        content.invoke(tagConsumer)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T: Component> getChildByName(name: String): T? {
        val component = childrenMap[name]
        try {
            return component?.let { component as T }
        } catch (ex: ClassCastException) {
            throw IllegalArgumentException("Component by name $name has wrong type", ex)
        }
    }

    fun getChildByName(name: String): Component? = childrenMap[name]

    protected fun setShowIf(value: IObservableValue<Boolean>) {
        showIfObserver?.unsubscribe()
        if (!value.value) {
            setVisibility(false)
        }
        showIfObserver = value.subscribe(::setVisibility)
    }

    protected open fun onChangeVisibility(visibility: Boolean) {
        if (visibility) {
            element.style.display = oldDisplay
        } else {
            oldDisplay = element.style.display
            element.style.display = DISPLAY_NONE
        }
    }

    internal fun addChild(component: Component) {
        val name = component.name
        if (name != null) {
            if (childrenMap[name] != null) {
                throw IllegalArgumentException("Child with name $name already exists")
            }
            childrenMap[name] = component
        }
        childrenList.add(component)
    }

    internal fun onFinalize(element: HTMLElement) {
        this@Component.element = element
        init = true
        initActions.forEach { it.invoke(this@Component) }
        GlobalScope.launch { initActions.clear() }.start()
    }

    private fun setVisibility(visible: Boolean) {
        when {
            init.not() -> initActions.add { setVisibility(visible) }
            visible.not() and this.visible -> onChangeVisibility(visible)
            visible and this.visible.not() -> onChangeVisibility(visible)
        }
    }

    companion object {
        const val DISPLAY_NONE = "none"
    }

}