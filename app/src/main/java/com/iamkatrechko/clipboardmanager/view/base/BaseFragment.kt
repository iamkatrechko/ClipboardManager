package com.iamkatrechko.clipboardmanager.view.base

import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.iamkatrechko.clipboardmanager.view.extension.showToast

/**
 * Базовый фрагмент
 * @author iamkatrechko
 *         Date: 16.12.2018
 *
 * @property [B] binding-класс разметки активности
 */
abstract class BaseFragment<B : ViewDataBinding> : Fragment(), MvpView {

    /** Биндинг разметки */
    protected lateinit var binding: B
        private set

    /** Базовая активность, связанная с текущим фрагментом */
    protected val baseActivity: BaseActivity<*>?
        get() = activity as? BaseActivity<*>

    /** Идентификатор разметки */
    @get:LayoutRes
    protected abstract val layoutId: Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun showMessage(message: String) {
        showToast(message)
    }

    override fun showMessage(resId: Int) {
        showToast(resId)
    }

    override fun showError(resId: Int) {
        showToast(resId)
    }

    override fun showError(message: String) {
        showToast(message)
    }
}