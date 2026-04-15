package com.eddy.mockebike.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.eddy.mockebike.R
import com.eddy.mockebike.presentation.ui.component.AnimatedImage
import com.eddy.mockebike.presentation.ui.component.Pas
import com.eddy.mockebike.presentation.ui.component.Speed
import com.eddy.mockebike.presentation.ui.component.VerticalSlider
import com.eddy.mockebike.presentation.ui.theme.DarkPrimary
import com.eddy.mockebike.presentation.ui.theme.MockEBikeTheme
import com.eddy.mockebike.presentation.ui.theme.Primary
import com.eddy.mockebike.presentation.ui.theme.Typography
import kotlinx.coroutines.delay
import java.time.LocalTime


@Composable
fun BikeRoute(
    bikeViewModel: BikeViewModel,
) {

    val bikeUiState by bikeViewModel.uiState.collectAsStateWithLifecycle()

    BikeScreen(
        speed = bikeUiState.speed,
        distance = bikeUiState.distance,
        gear = bikeUiState.gear,
        battery = bikeUiState.battery,
        onChangeProportionalFactor = bikeViewModel::changeProportionalFactor,
        onChangeTargetBattery = bikeViewModel::changeTargetBattery,
        onChangeGear = bikeViewModel::changeGear,
    )
}

@Composable
fun BikeScreen(
    speed: Float,
    distance: Float,
    gear: Int,
    battery: Float,
    onChangeProportionalFactor: (Float) -> Unit,
    onChangeTargetBattery: (Float) -> Unit,
    onChangeGear: (Int) -> Unit,
) {

    var proportionalFactorSliderValue by remember { mutableStateOf(1f) }
    var targetBatterySliderValue by remember { mutableStateOf(100f) }

    Column(
        modifier = Modifier.fillMaxSize() // 화면 전체 크기를 채움
    ) {
        Box(
            modifier = Modifier
                .weight(1f)  // 남은 공간을 모두 차지
                .fillMaxWidth()  // 너비는 부모의 너비로 채움
                .background(Primary)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedImage(
                    modifier = Modifier
                        .weight(1f),
                    speed = speed
                )
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())// 화면 전체 크기를 채움
                ) {
                    //비례값 조정
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(100.dp, 320.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        VerticalSlider(
                            modifier = Modifier.size(20.dp, 5.dp),
                            value = proportionalFactorSliderValue,
                            onValueChange = {
                                proportionalFactorSliderValue = it
                                onChangeProportionalFactor(it)
                            },
                            valueRange = 0f..1.5f,
//                            steps = 2, //간격 없음!
                        )

                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DarkPrimary)
                                .size(70.dp) //얘의 부모의 너비가 100이어서 가로가 짤린거임~!!@!!!
                                .aspectRatio(1f)
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Power",
                                color = Color.White,
                                style = Typography.displaySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text =
                            String.format(
                                "%.1f",
                                proportionalFactorSliderValue
                            ),
                            color = Color.White,
                            style = Typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    //배터리 조정
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                            .size(100.dp, 320.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally

                    ) {
                        VerticalSlider(
                            modifier = Modifier.size(20.dp, 10.dp),
                            value = targetBatterySliderValue,
                            onValueChange = {
                                targetBatterySliderValue = it
                                onChangeTargetBattery(it)
                            },
                            valueRange = 0f..100.0F,
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(DarkPrimary)
                                .size(70.dp) //얘의 부모의 너비가 100이어서 가로가 짤린거임~!!@!!!
                                .aspectRatio(1f)
                                .padding(5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Battery",
                                style = Typography.displaySmall,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                        Text(
                            text =
                            String.format(
                                "%.1f",
                                targetBatterySliderValue
                            ),
                            style = Typography.bodySmall,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState())// 화면 전체 크기를 채움
                    ,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically // 세로 방향으로 중앙 정렬
                    ) {
                        Text(
                            text = String.format("%.0f", battery) + "%",
                            color = Color.White,
                            style = Typography.bodyMedium,
                        )
                        Image(
                            painter = painterResource(id = R.drawable.img_battery),  // 리소스 이미지 사용
                            contentDescription = "Example Image",
                            colorFilter = ColorFilter.tint(
                                Color.White,
                                BlendMode.SrcIn
                            ), // 색상 및 혼합 모드 설정
                            modifier = Modifier.size(70.dp)
                        )
                    }

                    Speed(
                        modifier = Modifier.size(200.dp),
                        speed = speed
                    )

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Primary)
                            .padding(top = 10.dp, end = 20.dp, start = 20.dp, bottom = 10.dp),
                    ) {
                        Text(
                            text = "ODO  " + String.format("%.2f", distance) + "km",
                            color = Color.White,
                            style = Typography.bodyMedium,
                        )
                    }

                    Pas(
                        modifier = Modifier.padding(end = 10.dp),
                        selectedValue = gear,
//                    selected.toFloat(), //사용자로 하여금 바꾸고 싶은 값은 uistate로 하면 안됨
                        onSelect = { newIndex ->
//                        selected = newIndex
                            onChangeGear(newIndex)
                        }
                    )

                }


            }
        }
    }
}

@Composable
fun realTimeClock(): String {
    var currentTime by remember { mutableStateOf(LocalTime.now()) } // 현재 시간 상태

    // LaunchedEffect를 사용하여 일정 시간마다 상태 업데이트
    LaunchedEffect(Unit) {
        while (true) {
            currentTime = LocalTime.now() // 현재 시간 갱신
            delay(1000L) // 1초마다 시간 업데이트
        }
    }

    val timeText = "${currentTime.hour}:${currentTime.minute}:${currentTime.second}"

    return timeText
}


@Preview(
    showBackground = true,
    name = "Galaxy Tab A9+",
    device = "spec:width=1800dp,height=1100dp,dpi=260"
//    widthDp = 1920, heightDp = 1200
)
@Composable
fun BikePreview() {
    MockEBikeTheme {
        BikeScreen(
            speed = 22.5f,
            distance = 1.23f,
            gear = 2,
            battery = 88f,
            onChangeProportionalFactor = {},
            onChangeTargetBattery = {},
            onChangeGear = {},
        )
    }
}
