package be.marche.apptravaux

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import be.marche.apptravaux.ui.theme.TravauxUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTravaux6Theme {
                //               Conversation(SampleData.conversationSample)
                LoadNews()
            }
        }
    }
}

@Composable
fun LoadNews(mainViewModel: MainViewModel = viewModel()) {
    when (val state = mainViewModel.uiState.collectAsState().value) {
        is MainViewModel.WeatherUiState.Loading -> {
            Log.d("ZEZE", "loading")
        }
        is MainViewModel.WeatherUiState.Error -> {
            Log.d("ZEZE", "error")
            ErrorDialog(state.message)
        }
        is MainViewModel.WeatherUiState.Loaded -> {
            Log.d("ZEZE", "loaded")
            WeatherLoadedScreen(state.data)
        }
    }
}

@Composable
fun WeatherLoadedScreen(data: TravauxUiModel) {
    Text(
        text = data.city,
        modifier = Modifier
            .padding(start = 24.dp, top = 50.dp, end = 24.dp, bottom = 50.dp),
        style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp)
    )
}

@Composable
fun Conversation(messages: List<Message>) {
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
        Conversation(SampleData.conversationSample)
    }
}
