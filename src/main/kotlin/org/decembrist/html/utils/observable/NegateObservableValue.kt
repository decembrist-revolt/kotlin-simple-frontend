package org.decembrist.html.utils.observable

class NegateObservableValue internal constructor(
        private val observableValue: IObservableValue<Boolean>)
    : IObservableValue<Boolean> by observableValue {

    override var value: Boolean
        set(value) {
            observableValue.value = value
        }
        get() = observableValue.value.not()

    override fun subscribe(listener: (Boolean) -> Unit): AbstractObserver<Boolean> {
        return observableValue.subscribe {
            listener.invoke(it.not())
        }
    }

}