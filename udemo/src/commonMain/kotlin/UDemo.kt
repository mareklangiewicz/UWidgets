package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.umath.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*

@Composable
fun UDemo(udemo2size: Int = 400) = UAlign(USTRETCH, USTRETCH) {
    UTabs(
        "UDemo 0" to { UDemo0() },
        "UDemo 1" to { UDemo1() },
        "UDemo 2" to { UDemo2(udemo2size.dp.squared) },
    )
}

@Composable
fun UDemo0() = URow {
    UColumn(DpSize(30.dp, 800.dp)) { UDemoTexts(5, growFactor = 0) }
    UColumn {
        var switch1 by remember { mutableStateOf(USTART) }
        var switch2 by remember { mutableStateOf(USTART) }
        var switch3 by remember { mutableStateOf(USTART) }
        var switch4 by remember { mutableStateOf(USTART) }
        UAlign(USTART, USTART) {
            UColumn {
                UText("Align switchers:")
                val options = UAlignmentType.values().map { it.css }.toTypedArray()
                UTabs(*options) { idx, tab -> switch1 = UAlignmentType.css(tab) }
                UTabs(*options) { idx, tab -> switch2 = UAlignmentType.css(tab) }
                UTabs(*options) { idx, tab -> switch3 = UAlignmentType.css(tab) }
                UTabs(*options) { idx, tab -> switch4 = UAlignmentType.css(tab) }
            }
        }
        UAlign(horizontal = switch1, vertical = switch2) {
            UBox { UBox { URow { UDemoTexts(3) } } }
            UBox { UBox { URow { UDemoTexts(15) } } }
            UTheme(lightBluishUColors()) {
                UBox { UBox { UBox { UDemoTexts() } } }
            }
            UAlign(horizontal = switch3, vertical = switch4) {
                UBox { UBox { UColumn { UDemoTexts(10, growFactor = 3) } } }
            }
        }
    }
}

@Composable
private fun UDemoTexts(
    count: Int = 20,
    boxed: Boolean = true,
    center: Boolean = true,
    bold: Boolean = true,
    mono: Boolean = true,
    growFactor: Int = 1,
) {
    require(boxed || !center)
    repeat(count) { i ->
        val c = 'A' + i
        val s = "$c".repeat(1 + i * growFactor)
        if (boxed) UBoxedText(s, center, bold, mono)
        else UText(s, bold, mono)
    }
}

@Composable fun UDemo1() = URow(withHorizontalScroll = true, withVerticalScroll = true) {
    UTheme(lightUColors()) { SomeMenuTree() }
    UTheme(darkUColors()) { SomeMenuTree() }
    UTheme(lightBluishUColors()) { SomeMenuTree() }
}

@Composable fun UDemo2(size: DpSize) = UColumn(size, withHorizontalScroll = true, withVerticalScroll = true) {
    UDemoTexts(40, growFactor = 4)
}

@Composable fun SomeMenuTree() {
    UMenuTree(
        "XYZ".cbtree(
            "AAA".cbtree(
                "aaa1".cbtree { println("aaa1") },
                "aaa2".cbtree { println("aaa2") },
            ),
            "BBB".cbtree(
                "bbb1".cbtree { println("bbb1") },
                "bbb2".cbtree { println("bbb2") },
                "bCCC".cbtree(
                    "ccc".cbtree { println("ccc") }
                )
            ),
        ),
        Dispatchers.Default
    )
}
