package com.example.donutchart

import androidx.compose.ui.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.donutchart.databinding.MainActivityLayoutBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainActivityLayoutBinding

    private val viewData = DonutChartDataCollection(
        listOf(
            DonutChartData(1200.0f, Color.Cyan, title = "Food & Groceries"),
            DonutChartData(1500.0f, Color.Blue, title = "Rent"),
            DonutChartData(300.0f, Color.Yellow, title = "Gas"),
            DonutChartData(700.0f, Color.DarkGray, title = "Online Purchases"),
            DonutChartData(300.0f, Color.Green, title = "Clothing")
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityLayoutBinding.inflate(layoutInflater)
        binding.donutChart.setContent {
            MaterialTheme {
                Scaffold(
                    topBar = {
                        Text(
                            "Fancy Donut Chart",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        )
                    }
                ) { paddingValues ->
                    // 2
                    DonutChart(modifier = Modifier.padding(paddingValues), data = viewData)
                }
            }
            setContentView(binding.root)
        }
    }
}

private const val TOTAL_ANGLE = 360.0f

data class DonutChartData(
    val amount: Float,
    val color: Color,
    val title: String,
)

data class DonutChartDataCollection(
    var items: List<DonutChartData>
) {
    internal var totalAmount: Float = items.sumOf { it.amount.toDouble() }.toFloat()
        private set
}

private data class DrawingAngles(val start: Float, val end: Float)

@Composable
fun DonutChart(
    data: DonutChartDataCollection,
    modifier: Modifier,
    chartSize: Dp = 350.dp,
    gapPercentage: Float = 0.04f,
) {
    val anglesList: MutableList<DrawingAngles> = remember { mutableListOf() }
    val gapAngle = data.calculateGapAngle(gapPercentage)

    // 1
    Box(
        modifier = modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
    ) {
        // 2
        Canvas(
            modifier = Modifier.size(chartSize),
            // 3
            onDraw = {
                val defaultStrokeWidth = 40.dp.toPx()
                anglesList.clear()
                var lastAngle = 0f
                data.items.forEachIndexed { ind, item ->
                    val sweepAngle = data.findSweepAngle(ind, gapPercentage)
                    anglesList.add(DrawingAngles(lastAngle, sweepAngle))
                    // 4
                    drawArc(
                        color = item.color,
                        startAngle = lastAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = Offset(defaultStrokeWidth / 2, defaultStrokeWidth / 2),
                        style = Stroke(defaultStrokeWidth, cap = StrokeCap.Butt),
                        size = Size(size.width - defaultStrokeWidth,
                            size.height - defaultStrokeWidth)
                    )
                    // 5
                    lastAngle += sweepAngle + gapAngle
                }
            }
        )
    }
}

/**
 * Calculate the gap width between the arcs based on [gapPercentage]. The percentage is applied
 * to the average count to determine the width in pixels.
 */
private fun DonutChartDataCollection.calculateGap(gapPercentage: Float): Float {
    if (this.items.isEmpty()) return 0f

    return (this.totalAmount / this.items.size) * gapPercentage
}

/**
 * Returns the total data points including the individual gap widths indicated by the
 * [gapPercentage].
 */
private fun DonutChartDataCollection.getTotalAmountWithGapIncluded(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    return this.totalAmount + (this.items.size * gap)
}

/**
 * Calculate the sweep angle of an arc including the gap as well. The gap is derived based
 * on [gapPercentage].
 */
private fun DonutChartDataCollection.calculateGapAngle(gapPercentage: Float): Float {
    val gap = this.calculateGap(gapPercentage)
    val totalAmountWithGap = this.getTotalAmountWithGapIncluded(gapPercentage)

    return (gap / totalAmountWithGap) * TOTAL_ANGLE
}

/**
 * Returns the sweep angle of a given point in the [DonutChartDataCollection]. This calculations
 * takes the gap between arcs into the account.
 */
private fun DonutChartDataCollection.findSweepAngle(
    index: Int,
    gapPercentage: Float
): Float {
    val amount = items[index].amount
    val gap = this.calculateGap(gapPercentage)
    val totalWithGap = getTotalAmountWithGapIncluded(gapPercentage)
    val gapAngle = this.calculateGapAngle(gapPercentage)
    return ((((amount + gap) / totalWithGap) * TOTAL_ANGLE)) - gapAngle
}