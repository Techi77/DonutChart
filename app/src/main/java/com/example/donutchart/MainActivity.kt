package com.example.donutchart


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.Color.Companion.Yellow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.donutchart.databinding.MainActivityLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        val dataForPie = mapOf(
            Pair(Transparent, 150),
            Pair(Red, 20),
            Pair(Blue, 70),
            Pair(Yellow, 170),
            Pair(Green, 50),
        )
        binding.donutChart.setContent {
            MaterialTheme {
                PieChart(
                    data = dataForPie,
                    backgroundColor = White
                )
            }
        }
        binding.donutChart.rotation = 270F
    }


}

@Composable
fun PieChart(
    data: Map<Color, Int>,
    backgroundColor: Color,
    radiusOuter: Dp = 140.dp,
    chartBarWidth: Dp = 46.dp,
) {
    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }
    var lastValue = 0f
    Box(
        modifier = Modifier
            .size((radiusOuter.value * 2f).dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.chart_background),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(radiusOuter * 2f)
                .clip(CircleShape)
        )
        Canvas(
            modifier = Modifier
                .size(radiusOuter * 2f)
                .padding(chartBarWidth / 2)
        ) {
            floatValue.forEachIndexed { index, value ->
                drawArc(
                    color = data.keys.toList()[index],
                    startAngle = lastValue,
                    sweepAngle = value,
                    useCenter = false,
                    style = Stroke(chartBarWidth.toPx(), cap = StrokeCap.Butt)
                )
                lastValue += value
            }
            drawCircle(
                color=backgroundColor,
                radius = (radiusOuter-chartBarWidth).toPx()
            )
        }
    }
}
