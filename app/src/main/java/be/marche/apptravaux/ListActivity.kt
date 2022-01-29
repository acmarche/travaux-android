package be.marche.apptravaux

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.viewModel.AvaloirViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTravaux6Theme {
                //Conversation(SampleData.conversationSample)
                LoadNews()
            }
        }
    }
}

@Composable
fun LoadNews(mainViewModel: AvaloirViewModel = viewModel()) {
    when (val state = mainViewModel.uiState.collectAsState().value) {
        is AvaloirViewModel.AvaloirUiState.Loading -> {
            Log.d("ZEZE", "loading")
        }
        is AvaloirViewModel.AvaloirUiState.Error -> {
            Log.d("ZEZE", "error")
            ErrorDialog(state.message)
        }
        is AvaloirViewModel.AvaloirUiState.Loaded -> {
            Log.d("ZEZE", "loaded")
            WeatherLoadedScreen(state.data)
        }
    }
}

@Composable
fun WeatherLoadedScreen(avaloirs: List<Avaloir>) {
    LazyColumn {
        items(avaloirs) { avaloir ->
            MessageCard(avaloir)
        }
    }
}

@Composable
fun Conversation(messages: List<Avaloir>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Composable
fun ErrorDialog(message: String) {
    Text(
        text = message,
        modifier = Modifier
            .padding(start = 24.dp, top = 50.dp, end = 24.dp, bottom = 50.dp),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
    )
}

@Preview
@Composable
fun PreviewConversation() {
    AppTravaux6Theme {
        //Conversation(SampleData.conversationSample)
    }
}
