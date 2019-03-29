package org.decembrist.html.utils.observable

interface IObservableValue<T> {

    var value: T

    fun subscribe(listener: (T) -> Unit): AbstractObserver<T>

}
