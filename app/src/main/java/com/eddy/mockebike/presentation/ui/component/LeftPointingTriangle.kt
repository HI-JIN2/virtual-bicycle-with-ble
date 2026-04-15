package com.eddy.mockebike.presentation.ui.component

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun LeftPointingTriangle(
    modifier: Modifier = Modifier,
    color: Color = Color.Gray,
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        val path = Path().apply {
            moveTo(width * 0.8f, height * 0.1f)  // 우상단
            lineTo(width * 0.8f, height * 0.9f)  // 우하단
            lineTo(width * 0.2f, height * 0.5f)  // 좌중앙
            close()  // 시작점으로
        }

        // 삼각형 채우기
        drawPath(
            path = path,
            color = color
        )
    }
}

// 사용 예시
@Preview(showBackground = true)
@Composable
fun TriangleExample() {
    LeftPointingTriangle(
        modifier = Modifier.size(100.dp),
    )
}
