package com.example

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.treksoft.kvision.chart.*
import pl.treksoft.kvision.core.Col
import pl.treksoft.kvision.core.Color
import pl.treksoft.kvision.core.Component
import pl.treksoft.kvision.html.Div
import pl.treksoft.kvision.html.div
import pl.treksoft.kvision.html.h1
import pl.treksoft.kvision.html.h4
import pl.treksoft.kvision.panel.*
import pl.treksoft.kvision.utils.rem
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.sign

/**
 * Main status panel that provides an overview of system information and resource usage
 */
class StatusPanel : SimplePanel() {

    /**
     * Late initialized references to panels and charts that are populated
     * with system information retrieved from the backend service
     */
    private lateinit var systemInfoPanel: GridPanel
    private lateinit var memoryPanel: GridPanel
    private lateinit var memoryChart: Chart
    private lateinit var cpuPanel: GridPanel
    private lateinit var cpuChart: Chart
    private lateinit var diskPanel: GridPanel

    init {
        // Place the panel label across the top
        div {
            paddingTop = 1.rem
            marginBottom = 1.rem
            addCssClass("border-bottom")
            h1("Status", className = "h2")
        }

        // Create a flex panel to hold the status panel "cards" with spacing between
        // them and wrap to next row as needed
        flexPanel(spacing = 10, wrap = FlexWrap.WRAP) {

            // Add the system info card with a grid panel to hold a table
            // of system properties
            card("System Info", body = GridPanel(columnGap = 10) {
                systemInfoPanel = this
                visible = false
                var row = 0
                add(Div("Host Name"), 1, ++row)
                add(Div("Operating System"), 1, ++row)
                add(Div("Boot Time"), 1, ++row)
                add(Div("Up Time"), 1, ++row)
                add(Div("Manufacturer"), 1, ++row)
                add(Div("Model"), 1, ++row)
                add(Div("Serial"), 1, ++row)
            })

            // Add the memory usage card with the body held by a flex panel so the
            // chart can wrap to the next row if viewing on a mobile device
            card("Memory Usage", body = FlexPanel(alignItems = FlexAlignItems.CENTER, wrap = FlexWrap.WRAP) {
                memoryPanel = gridPanel(columnGap = 10) {
                    visible = false
                    var row = 0
                    add(Div("Memory Total"), 1, ++row)
                    add(Div("Memory Available"), 1, ++row)
                    add(Div("Memory Used"), 1, ++row)
                }
                memoryChart = chart(Configuration(ChartType.DOUGHNUT, listOf(DataSets(data = listOf()))))
            })

            // Add the cpu usage card with the body held by a flex panel so the
            // chart can wrap to the next row if viewing on a mobile device
            card("Cpu Usage", body = FlexPanel(alignItems = FlexAlignItems.CENTER, wrap = FlexWrap.WRAP) {
                cpuPanel = gridPanel(columnGap = 10) {
                    visible = false
                    var row = 0
                    add(Div("Cpu Utilization"), 1, ++row)
                    add(Div("Load Average"), 1, ++row)
                    add(Div("Per Cpu Utilization"), 1, ++row)
                }
                cpuChart = chart(Configuration(ChartType.DOUGHNUT, listOf(DataSets(data = listOf()))))
            })

            // Add the disk usage card which is initially empty since multiple disks are returned
            card("Disk Usage", body = Div {
                diskPanel = gridPanel(columnGap = 10){

                }
            })

        }

        // Launch the request to access the system info from the back end
        GlobalScope.launch {
            // Request the system info from the back end
            val osi = Model.getSystemInfoModel()

            // Add system info to panel
            with(systemInfoPanel) {
                var row = 0
                add(Div(osi.hostName), 2, ++row)
                add(Div(osi.os), 2, ++row)
                add(Div(osi.bootDateTime?.toString()), 2, ++row)
                add(Div(osi.upTimeString), 2, ++row)
                add(Div(osi.manufacturer), 2, ++row)
                add(Div(osi.model), 2, ++row)
                add(Div(osi.serial), 2, ++row)
                visible = true
            }

            val memoryTotal = osi.memoryTotal ?: 0
            val memoryAvailable = osi.memoryAvailable ?: 0
            val memoryUsed = memoryTotal - memoryAvailable
            with(memoryPanel) {
                var row = 0
                add(Div(memoryTotal.toHumanBytesString(2)), 2, ++row)
                add(Div(memoryAvailable.toHumanBytesString(2)), 2, ++row)
                add(Div(memoryUsed.toHumanBytesString(2)), 2, ++row)
                visible = true
            }

            val memoryUsedPercent = (memoryUsed / memoryTotal.toDouble()) * 100.0
            val memoryAvailPercent = (memoryAvailable / memoryTotal.toDouble()) * 100.0
            memoryChart.configuration = Configuration(ChartType.DOUGHNUT,
                    listOf(DataSets(
                            data = listOf(memoryUsedPercent.toInt(), memoryAvailPercent.toInt()),
                            backgroundColor = listOf(Color.name(Col.CORAL), Color.name(Col.GREEN))
                    )),
                    listOf("% Used", "% Available"),
                    options = ChartOptions(circumference = (2 * PI * 0.75), rotation = (PI * 0.75), cutoutPercentage = 50, maintainAspectRatio = false)

            )
            memoryChart.resizeChart()

            with(cpuPanel) {
                var row = 0
                add(Div(osi.cpuLoad?.toPercentString(2)), 2, ++row)
                add(Div("${osi.cpuLoadAvg0} / ${osi.cpuLoadAvg1} / ${osi.cpuLoadAvg2}"), 2, ++row)
                add(Div(osi.cpuProcLoad?.joinToString { it.toPercentString() }), 2, ++row)
                visible = true
            }

            val cpuUsedPerc = (osi.cpuLoad ?: 0.0) * 100.0
            val cpuAvailPerc = 100.0 - cpuUsedPerc
            cpuChart.configuration = Configuration(ChartType.DOUGHNUT,
                    listOf(DataSets(
                            data = listOf(cpuUsedPerc, cpuAvailPerc),
                            backgroundColor = listOf(Color.name(Col.YELLOW), Color.name(Col.LIGHTGRAY))
                    )),
                    listOf("% Used", "% Available"),
                    options = ChartOptions(circumference = (2 * PI * 0.75), rotation = (PI * 0.75), cutoutPercentage = 50, maintainAspectRatio = false)

            )

            with(diskPanel) {
                var row = 0
                osi.diskSummary?.forEach {
                    row++
                    add(Div(it.name), 1, row)
                    add(Div("${it.free?.toHumanBytesString()} free of ${it.size?.toHumanBytesString()}"), 2, row)
                }
            }
        }
    }

    /**
     * Extension function to create a bootstrap "card" and add it as an item in a flex panel.
     * The card is styled specifically for the desired layout and looks for this status panel.
     *
     * @param title The title to place in the status card
     * @param body The component to place in the card body div
     */
    private fun FlexPanel.card(title: String, body: Component) {
        // When adding an item to a flex panel, a FlexWrapper (i.e. a div) is created
        // and the component specified by child is added to the wrapper div.
        // In order to make the wrapper div expand to the size of the row
        // in the flex panel, we set the class on the wrapper div to "card". If
        // we set it on the child div, the card would be a different height
        // than the other cards in the flex panel.  CSS class "mb-3" is also
        // set on the wrapper div to add spacing between rows when the flex
        // panel wraps.
        this.add(classes = setOf("card", "mb-3"), child = Div {
            h4(title, className = "card-header")
            div(classes = setOf("card-body")) {
                add(body)
            }
        })
    }

    /**
     * Extension function to produce a percent string from a double
     * with the specified number of [digits] after the decimal.
     */
    private fun Double.toPercentString(digits: Int = 1) : String {
        val perc = this * 100.0
        return "${perc.asDynamic().toFixed(digits.coerceIn(0, 10))}%"
    }

    /**
     * Extension function to produce a human readable bytes string
     * with the specified number of [digits] after the decimal.
     *
     * Adapted from java code taken from stack overflow
     * https://stackoverflow.com/a/23442686/9943190
     */
    private fun Long.toHumanBytesString(digits: Int = 1): String? {
        val bytes = this
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val suffixes = "KMGTPE"
        var ci = 0
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci++
            i -= 10
        }
        val valueKb = (value * bytes.sign) / 1024.0
        return "${valueKb.asDynamic().toFixed(digits.coerceIn(0, 10))} ${suffixes[ci]}B"
    }
}
