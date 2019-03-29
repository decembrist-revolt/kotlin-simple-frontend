package org.decembrist.html.utils.observable

class ObservableValue<T> internal constructor(initialValue: T): IObservableValue<T> {
    override var value: T = initialValue
        set(value) {
            notify(value)
            field = value
        }

    private val observers = mutableListOf<Observer<T>>()

    override fun subscribe(listener: (T) -> Unit): Observer<T> {
        val observer = Observer(this, listener)
        observers.add(observer)
        return observer
    }

    private fun notify(value: T) {
        for (observer in observers) {
            observer.notify(value)
        }
    }

    data class Observer<T> internal constructor(private val observableValue: ObservableValue<T>,
                                                private val listener: (T) -> Unit
    ) : AbstractObserver<T>() {

        override fun notify(value: T) {
            listener.invoke(value)
        }

        override fun unsubscribe() {
            observableValue.observers.remove(this)
        }

    }


}