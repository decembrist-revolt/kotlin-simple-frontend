package org.decembrist.html.utils.observable

object Observable {

    fun <T> of(initialValue: T): IObservableValue<T> = ObservableValue(initialValue)

}

fun IObservableValue<Boolean>.negate() = NegateObservableValue(this)