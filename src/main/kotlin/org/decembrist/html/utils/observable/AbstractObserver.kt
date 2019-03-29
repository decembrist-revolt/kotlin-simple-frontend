package org.decembrist.html.utils.observable

abstract class AbstractObserver<T> {

    internal abstract fun notify(value: T)

    abstract fun unsubscribe()

}
