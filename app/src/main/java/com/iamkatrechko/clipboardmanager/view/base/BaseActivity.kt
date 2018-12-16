package com.iamkatrechko.clipboardmanager.view.base

import android.annotation.SuppressLint
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.iamkatrechko.clipboardmanager.view.extension.showToast

/**
 * Базовая активность приложения.
 * @author iamkatrechko
 *         Date: 16.12.2018
 *
 * @property [B] binding-класс разметки активности
 */
@SuppressLint("Registered")
abstract class BaseActivity<B : ViewDataBinding> : AppCompatActivity(), MvpView {

    /** Идентификатор разметки */
    @get:LayoutRes
    protected abstract val layoutId: Int
    /** Биндинг разметки */
    protected lateinit var binding: B
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showMessage(message: String) = showToast(message)

    override fun showMessage(resId: Int) = showToast(resId)

    override fun showError(message: String) = showToast(message)

    override fun showError(resId: Int) = showToast(resId)
}
