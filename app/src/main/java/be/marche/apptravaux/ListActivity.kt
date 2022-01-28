package be.marche.apptravaux

import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel

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
        is MainViewModel.WeatherUiState.Loaded -> Log.d("TAG", "message")
    }
}

@Composable
fun Conversation(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            MessageCard(message)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
   AppTravaux6Theme {
        Conversation(SampleData.conversationSample)
    }
}
