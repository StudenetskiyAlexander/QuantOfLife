package com.skyfolk.quantoflife.shared.presentation.binding

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class DataBindingDelegate<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int?,
    private val onDestroyView: T.() -> Unit
) : ReadOnlyProperty<Fragment, T?> {

    protected var binding: T? = null

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T? = binding

    fun inflate(inflater: LayoutInflater, layoutId: Int?, parent: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, this.layoutId ?: layoutId!!, parent, false)
        return binding!!.root
    }

    fun onViewCreated(lifecycleOwner: LifecycleOwner) {
        binding?.lifecycleOwner = lifecycleOwner

        lifecycleOwner.lifecycleScope.whenCompleted {
            binding?.apply(onDestroyView)
            binding = null
        }
    }
}

fun CoroutineScope.whenCompleted(action: (Throwable?) -> Unit) {
    coroutineContext[Job]?.onCompletion(action)
}

fun Job.onCompletion(action: CompletionHandler): Job {
    invokeOnCompletion(action)
    return this
}