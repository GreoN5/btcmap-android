package search

import androidx.lifecycle.ViewModel
import elements.Element
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class SearchResultModel : ViewModel() {

    val element = MutableStateFlow<Element?>(null)
}