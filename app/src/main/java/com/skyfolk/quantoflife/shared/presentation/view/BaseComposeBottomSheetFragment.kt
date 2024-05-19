package com.skyfolk.quantoflife.shared.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyfolk.quantoflife.shared.presentation.binding.DataBindingDelegate
import com.skyfolk.quantoflife.shared.presentation.state.ScreenAction
import com.skyfolk.quantoflife.shared.presentation.state.ScreenState
import com.skyfolk.quantoflife.shared.presentation.state.SingleLifeEvent
import com.skyfolk.quantoflife.shared.presentation.vm.BaseViewModel
import kotlinx.coroutines.flow.StateFlow

abstract class BaseComposeBottomSheetFragment<A: ScreenAction, S: ScreenState, E: SingleLifeEvent>: BottomSheetDialogFragment() {

    protected abstract val viewModel: BaseViewModel<A, S, E>
    open val layoutId: Int? = null
    open val binding: ViewBinding? by dataBinding()
    private lateinit var bindingDelegate: DataBindingDelegate<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        viewModel.singleLifeEvent.observe(
            viewLifecycleOwner
        ) {
            proceedSingleLifeEvent(it)
        }

        return bindingDelegate.inflate(inflater, layoutId, container)
            .apply {
                proceedScreenState(viewModel.state)
            }
    }

    protected abstract fun proceedSingleLifeEvent(event: E)
    protected abstract fun proceedScreenState(stateFlow: StateFlow<S>)

    protected fun <Y : ViewDataBinding> dataBinding(
        @LayoutRes layoutId: Int? = null,
        onDestroyView: Y.() -> Unit = {}
    ): DataBindingDelegate<Y> = DataBindingDelegate(layoutId, onDestroyView).also { bindingDelegate = it }
}