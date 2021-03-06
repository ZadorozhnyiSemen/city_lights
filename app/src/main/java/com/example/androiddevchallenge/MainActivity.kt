/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.androiddevchallenge.ui.theme.CityLightsTheme
import com.example.androiddevchallenge.ui.theme.MyTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CityLightsTheme {
                MyApp()
            }
        }
    }
}

enum class TimerState {
    Running,
    Idle
}

enum class CityState {
    Day,
    Night
}

enum class SkyState {
    Empty,
    Stars
}

@Composable
fun MyApp() {
    Surface(color = MaterialTheme.colors.background) {
        var timerState by remember { mutableStateOf(TimerState.Idle) }
        val animationTransition = updateTransition(targetState = timerState)
        val scope = rememberCoroutineScope()

        var cityState by remember { mutableStateOf(CityState.Day) }
        val cityTransition = updateTransition(targetState = cityState)

        var skyState by remember { mutableStateOf(SkyState.Empty) }
        val skyTransition = updateTransition(targetState = skyState)

        var initialTime: Int by remember { mutableStateOf(0) }

        val windowsState by remember { mutableStateOf(List(44) { Window(Color(0xFF2F71CF)) }) }

        val timerOffset by animationTransition.animateDp(
            transitionSpec = {
                tween(200)
            }
        ) { state ->
            when (state) {
                TimerState.Running -> 128.dp
                TimerState.Idle -> 0.dp
            }
        }

        val starsAlpha by skyTransition.animateFloat(
            transitionSpec = {
                tween(initialTime, easing = LinearEasing)
            }
        ) { state ->
            when (state) {
                SkyState.Empty -> 0f
                SkyState.Stars -> 1f
            }
        }

        val cityColor by cityTransition.animateColor(
            transitionSpec = {
                keyframes {
                    val animateToNight = cityState == CityState.Night
                    durationMillis = initialTime
                    val stepSize = initialTime / 5
                    val step1 = stepSize
                    val step2 = stepSize * 2
                    val step3 = stepSize * 3
                    val step4 = stepSize * 4
                    (if (animateToNight) Color(0xFFF6EFEB) else Color(0xFF0C122E)) at 0 with LinearEasing
                    (if (animateToNight) Color(0xFFFAC9AE) else Color(0xFF823F61)) at step1 with LinearEasing
                    (if (animateToNight) Color(0xFFE1697A) else Color(0xFFA8536A)) at step2 with LinearEasing
                    (if (animateToNight) Color(0xFFA8536A) else Color(0xFFE1697A)) at step3 with LinearEasing
                    (if (animateToNight) Color(0xFF823F61) else Color(0xFFFAC9AE)) at step4 with LinearEasing
                    (if (animateToNight) Color(0xFF0C122E) else Color(0xFFF6EFEB)) at initialTime with LinearEasing
                }
            }
        ) { state ->
            when (state) {
                CityState.Day -> Color(0xFFF6EFEB)
                CityState.Night -> Color(0xFF0C122E)
            }
        }

        val textColor by cityTransition.animateColor(
            transitionSpec = {
                tween(initialTime, easing = CubicBezierEasing(0.6f, 0.0f, 1.0f, 0.0f))
            }
        ) { state ->
            when (state) {
                CityState.Day -> Color(0xFF164EAF)
                CityState.Night -> Color.White
            }
        }

        val sunOffsetX by cityTransition.animateDp(
            transitionSpec = {
                tween(initialTime, easing = LinearEasing)
            }
        ) { state ->
            when (state) {
                CityState.Day -> 0.dp
                CityState.Night -> 320.dp
            }
        }
        val sunOffsetY by cityTransition.animateDp(
            transitionSpec = {
                tween(initialTime, easing = LinearEasing)
            }
        ) { state ->
            when (state) {
                CityState.Day -> 0.dp
                CityState.Night -> 732.dp
            }
        }

        BoxWithConstraints(Modifier.fillMaxSize()) {
            val screenWidth = maxWidth
            // Top
            Box(
                Modifier
                    .height(128.dp)
                    .fillMaxWidth()
                    .background(cityColor)
                    .align(Alignment.TopCenter),
                contentAlignment = Alignment.Center
            ) {
                val (m, s) = TimeUtils.getTimeAsString(initialTime.toFloat())
                Row(modifier = Modifier.align(Alignment.BottomCenter)) {
                    Text(m, style = MaterialTheme.typography.h3.copy(color = textColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(":", style = MaterialTheme.typography.h3.copy(color = textColor))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(s, style = MaterialTheme.typography.h3.copy(color = textColor))
                }
            }
            // Bottom
            Box(
                Modifier
                    .height(128.dp)
                    .fillMaxWidth()
                    .background(Color(0xFF040616))
                    .align(Alignment.BottomCenter),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Set timer", style = MaterialTheme.typography.subtitle1.copy(color = Color.White))
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyRow(
                        Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(
                            horizontal = 36.dp,
                            vertical = 0.dp
                        ),
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            for (i in 5..300 step 5) {
                                item {
                                    Surface(
                                        shape = MaterialTheme.shapes.medium,
                                    ) {
                                        val (m, s) = TimeUtils.getTimeAsString(i * 1000f)
                                        Row(
                                            Modifier
                                                .background(Color(0xFF0E66FF))
                                                .clickable {
                                                    initialTime = i * 1000 // 5 44
                                                    val switchTime = i * 1000 // 5
                                                    val switchPeriod = switchTime / 51 // 250
                                                    val starTrigger =
                                                        if (cityState == CityState.Day) switchTime * 0.6 else switchTime * 0.4
                                                    val nextSkyState =
                                                        if (cityState == CityState.Day) SkyState.Stars else SkyState.Empty
                                                    var lastSwitch = 0
                                                    timerState = TimerState.Running
                                                    val windowFinalColor =
                                                        if (cityState == CityState.Day) Color(
                                                            0xFFFFFDD8
                                                        ) else Color(0xFF2F71CF)
                                                    cityState =
                                                        if (cityState == CityState.Day) CityState.Night else CityState.Day
                                                    val randomIndexes = IntRange(0, 43)
                                                        .shuffled()
                                                        .toMutableList()
                                                    scope.launch(context = Dispatchers.Default) {
                                                        var hasTime = true
                                                        println(switchTime)
                                                        while (hasTime) {
                                                            delay(10)

                                                            if (lastSwitch > switchPeriod) {
                                                                if (randomIndexes.isNotEmpty()) {
                                                                    windowsState[
                                                                        randomIndexes.removeAt(
                                                                            0
                                                                        )
                                                                    ].color = windowFinalColor
                                                                }
                                                                lastSwitch = 0
                                                            } else if (lastSwitch < switchPeriod) {
                                                                lastSwitch += 10
                                                            }
                                                            if (initialTime < starTrigger) {
                                                                skyState = nextSkyState
                                                            }
                                                            initialTime -= 10
                                                            println(initialTime)
                                                            hasTime = initialTime > 0
                                                        }
                                                        initialTime = 0
                                                        timerState = TimerState.Idle
                                                    }
                                                }
                                                .padding(horizontal = 15.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(m, style = MaterialTheme.typography.h4.copy(color = Color.White))
                                            Text(":", style = MaterialTheme.typography.h4.copy(color = Color.White))
                                            Text(s, style = MaterialTheme.typography.h4.copy(color = Color.White))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                            }
                        }
                    )
                }
            }
            // City
            val contentHeight = maxHeight - 128.dp
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(contentHeight)
                    .offset(y = timerOffset)
                    .align(Alignment.TopCenter)
                    .background(cityColor),
                contentAlignment = Alignment.BottomCenter
            ) {
                Image(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(x = sunOffsetX, y = sunOffsetY)
                        .zIndex(-1f)
                        .padding(top = 86.dp, start = 32.dp),
                    painter = painterResource(id = R.drawable.ic_sun),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(id = R.drawable.ic_city_back_1),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    painter = painterResource(id = R.drawable.ic_city_back_2),
                    contentDescription = null
                )
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .alpha(starsAlpha)
                        .padding(top = 64.dp),
                    painter = painterResource(id = R.drawable.ic_stars),
                    contentDescription = null
                )
                Image(
                    painter = painterResource(id = R.drawable.ic_house_front),
                    contentDescription = null
                )
                Windows(
                    modifier = Modifier.padding(vertical = 12.dp),
                    spaceBetween = 12.dp,
                    windows = windowsState
                )
            }
        }
    }
}

data class Window(var color: Color)

@Composable
fun Windows(windows: List<Window>, modifier: Modifier, spaceBetween: Dp) {
    Row(modifier = modifier) {
        for (i in 0..3) { // 0 44 (0, 11), (11, 22), 22
            val windowRow = windows.subList(i * 11, (i + 1) * 11)
            WindowColumn(windowRow, spaceBetween)
            if (i != 3) Spacer(modifier = Modifier.width(12.dp))
        }
    }
}

@Composable
fun WindowColumn(windowRow: List<Window>, spaceBetween: Dp) {
    Column {
        windowRow.forEach {
            Spacer(modifier = Modifier.height(spaceBetween))
            Surface(shape = MaterialTheme.shapes.small, modifier = Modifier.background(Color(0xFF2F71CF))) {
                Box(
                    modifier = Modifier
                        .background(it.color)
                        .size(20.dp)
                ) {}
            }
        }
    }
}

@Preview("Light Theme", widthDp = 360, heightDp = 640)
@Composable
fun LightPreview() {
    MyTheme {
        MyApp()
    }
}
