package be.marche.apptravaux.samples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import be.marche.apptravaux.networking.ConnectionState
import be.marche.apptravaux.networking.connectivityState
import be.marche.apptravaux.ui.theme.AppTravaux6Theme
import com.myricseptember.countryfactcomposefinal.widgets.ConnectivityStatusBox
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    @ExperimentalCoroutinesApi
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTravaux6Theme {
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = "Check Live Connectivity",
                                        modifier = Modifier.fillMaxWidth(),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            )
                        },
                        content = {
                            Column {
                                ConnectivityStatus()
                                LazyColumn {
                                    items(20) { index ->
                                        Card(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(100.dp)
                                                .padding(10.dp, 5.dp, 10.dp, 5.dp)
                                                .background(Color.White),
                                            elevation = 10.dp,
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(10.dp),
                                                verticalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = "Make it Easy ${index + 1}",
                                                    color = Color.Black,
                                                    fontSize = 20.sp,
                                                    fontWeight = FontWeight.Bold
                                                )

                                                Spacer(modifier = Modifier.padding(2.dp))

                                                Text(
                                                    text = "Live Connectivity Status ${index + 1}",
                                                    fontSize = 16.sp,
                                                    color = Color.Gray
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@Composable
fun ConnectivityStatus() {
    val connection by connectivityState()
    val isConnected = connection == ConnectionState.Available
    var visibility by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = visibility,
        enter = expandVertically(),
        exit = shrinkVertically()
    ) {
        ConnectivityStatusBox(isConnected = isConnected)
    }

    LaunchedEffect(isConnected) {
        visibility = if (!isConnected) {
            true
        } else {
            delay(2000)
            false
        }
    }
}
