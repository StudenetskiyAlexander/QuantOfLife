package com.skyfolk.quantoflife.ui.feeds.entity

import com.skyfolk.quantoflife.entity.EventBase
import com.skyfolk.quantoflife.entity.QuantBase
import com.skyfolk.quantoflife.shared.presentation.state.SingleLifeEvent

sealed class FeedsFragmentSingleLifeEvent: SingleLifeEvent {
    data class ShowEditEventDialog(val quant: QuantBase, val event: EventBase?) : FeedsFragmentSingleLifeEvent()
}
