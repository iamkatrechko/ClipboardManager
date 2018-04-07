package com.iamkatrechko.clipboardmanager.domain.use_case.common

import io.reactivex.Completable
import io.reactivex.annotations.Experimental
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.addTo

/**
 * Абстрактный Completable сценарий
 *
 * Для реализации наследника необхоимо переопределить метод buildUseCaseObservable.
 * Schedulers подписки и прослушивания результата должны реализоваться в наследнике
 * @author iamkatrechko
 *         Date: 01.02.18
 *
 * @param [PARAMS] параметры сценария
 */
@Experimental
abstract class CompletableUseCase<in PARAMS> {

    /** Список подписчиков */
    private val disposables = CompositeDisposable()

    /**
     * Возвращает поток-источник, который реализует всю логику сценария
     * @param [params] параметры сценария
     * @return поток-источник
     */
    protected abstract fun buildUseCaseObservable(params: PARAMS?): Completable

    /**
     * Выполняет указанный сценарий
     * @param [params]    параметры сценария
     * @param [onSuccess] действие при успешном выполнении сценария
     * @param [onError]   слушатель ошибки выполнения сценария
     */
    @JvmOverloads
    fun execute(params: PARAMS? = null, onSuccess: Action = Action {}, onError: Consumer<Throwable> = Consumer {}): Disposable {
        return observe(params)
                .subscribe(onSuccess, onError)
                .addTo(disposables)
    }

    /**
     * Возвращает поток-источник на указанный сценарий
     * @param [params] параметры сценария
     */
    @JvmOverloads
    fun observe(params: PARAMS? = null): Completable = buildUseCaseObservable(params)

    /** Останавливает текущую и последующую работу сценария */
    fun dispose() = disposables.dispose()

    /** Останавливает текущую работу сценария */
    fun clear() = disposables.clear()
}