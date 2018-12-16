package com.iamkatrechko.clipboardmanager.view.base

import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

/**
 * Базовый презентер
 *
 * Предоставляет доступ к связанному view.
 * Хранит в себе список Rx-подписчиков.
 * @author iamkatrechko
 *         Date: 16.12.2018
 */
abstract class BasePresenter<V : MvpView>(
) : MvpPresenter<V>, ViewLifecycleObserver by EmptyViewLifecycleObserver() {

    /** View, которым управляет презентер */
    protected val view: V? get() = weakView.get()
    /** Привязана ли view к презентеру */
    protected val isViewAttached: Boolean get() = view != null
    /** Слабая ссылка на View, которым управляет презентер */
    private var weakView: WeakReference<V?> = WeakReference(null)
    /** Механизм отмены реактивных потоков */
    private val disposables = CompositeDisposable()

    @CallSuper
    override fun onAttach(view: V) {
        weakView = WeakReference(view)
        view.lifecycle.addObserver(this)
        disposables.clear()
    }

    @CallSuper
    override fun onDetach() {
        view?.lifecycle?.removeObserver(this)
        weakView.clear()
        disposables.dispose()
    }

    /** Выполненяет функцию на view в качестве приемника */
    protected fun onView(action: V.() -> Unit) =
            view?.action()

    /** Добавляет disposable в общий список presenter'a */
    protected fun Disposable.addToPresenter(): Boolean =
            disposables.add(this)
}