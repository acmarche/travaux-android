package be.marche.apptravaux.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.flow

class CardViewModel : ViewModel(

) {

    val itemsFlow = flow {
        val items = MutableList((1..100).random()) { index ->
            index
        }
        emit(items)
    }

    ///loop infinitive
    fun getItems() = flow {
        val items = MutableList((1..100).random()) { index ->
            index
        }
        emit(items)
    }
}