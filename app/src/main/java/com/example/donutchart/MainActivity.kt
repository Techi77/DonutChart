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
        binding.donutChart.setContent {
            MaterialTheme {
                PieChart(
                    data = mapOf(
                        Pair("Sample-1", 150),
                        Pair("Sample-2", 120),
                        Pair("Sample-3", 110),
                        Pair("Sample-4", 170),
                        Pair("Sample-5", 120),
                    )
                )
            }
        }
    }


}

@Composable
fun PieChart(
    data: Map<String, Int>,
    radiusOuter: Dp = 40.dp,
    chartBarWidth: Dp = 16.dp,
    backgroundColor: Color= White
) {

    val totalSum = data.values.sum()
    val floatValue = mutableListOf<Float>()
    data.values.forEachIndexed { index, values ->
        floatValue.add(index, 360 * values.toFloat() / totalSum.toFloat())
    }
    val colors = listOf(
        Transparent,
        Red,
        Blue,
        Yellow,
        Green
    )
    var lastValue = 0f
    Box(
        modifier = Modifier
            .size((radiusOuter.value * 2f).dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.chart_background),
            contentDescription = "avatar",
            contentScale = ContentScale.Crop,            // crop the image if it's not a square
            modifier = Modifier
                .size(radiusOuter * 2f)
                .clip(CircleShape)                       // clip to the circle shape
        )
        Canvas(
            modifier = Modifier
                .size(radiusOuter * 2f)
                .padding(chartBarWidth / 2)
        ) {
            // draw each Arc for each data entry in Pie Chart
            floatValue.forEachIndexed { index, value ->
                drawArc(
                    color = colors[index],
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
