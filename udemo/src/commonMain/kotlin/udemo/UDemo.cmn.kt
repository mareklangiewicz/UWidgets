package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.utheme.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UAlignmentType.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import androidx.compose.ui.Modifier as Mod

@Composable
fun UDemo() = UAllStretch {
    UColumn {
        val udemo2size = ustate(100)
        val (hscroll, vscroll) = ustates(true, true)
        UAllStartRow {
            USwitch(udemo2size, "100" to 100, "200" to 200, "400" to 400, "800" to 800)
            USwitch(hscroll, "hscroll on", "hscroll off")
            USwitch(vscroll, "vscroll on", "vscroll off")
        }
        UTabs(
            "UDemo Temp" to { UDemoTemp() },
            "UDemo 0" to { UDemo0() },
            "UDemo 1" to { UDemo1(hscroll.value, vscroll.value) },
            "UDemo 2" to { UDemo2(udemo2size.value.dp.square, hscroll.value, vscroll.value) },
            "UDemo 3 USkikoBox" to { UDemo3(udemo2size.value.dp.square, hscroll.value, vscroll.value) },
        )
    }
}

// FIXME NOW: remove this temporary code
@Composable fun UDemoTemp() = UAllStartColumn {
    var rendering by ustate("DOM and Canvas")
    UTabs("DOM and Canvas", "DOM", "Canvas") { idx, tab -> rendering = tab }
    URow {
        if ("DOM" in rendering) UBackgroundBox(Mod.usize(160.dp, 320.dp)) { UDemoTempContent() }
        if ("Canvas" in rendering) USkikoBox(DpSize(160.dp, 320.dp)) { UDemoTempContent() }
    }
}

@Composable fun UDemoTempContent() {
    UBox(Mod
        .ustyleBlank(
            margin = 4.dp,
            backgroundColor = Color.Blue,
            borderColor = Color.Red,
            borderWidth = 4.dp,
            padding = 4.dp,
        )
        .usize(130.dp, 300.dp)
        .onUClick { println("out box onuclick1") }
        .onUClick { println("out box onuclick2") }
    ) {
        UBox(Mod.usize(10.dp, 80.dp).uborder(width = 4.dp)) {}
        return@UBox
        UColumn {
            UChildrenMod({ onUClick { println("children") } }) {
                UBox(Mod.usize(100.dp.square)) {
//                    UChildrenMod({ onUClick { println("nb 1 children") } }) {
//                        UBox(Mod.usize(90.dp.square).onUClick { println("newborn 1") }) {
//                            UText("jkl", mono = true)
//                        }
//                    }
                }
                UBox(Mod.usize(100.dp.square)) {
//                    UBox(Mod.usize(90.dp.square).onUClick { println("newborn 2") }) {
//                        UText("jkl")
//                    }
                }
            }
        }
    }
}

@Composable fun UDemo0() = UAllStretch {
    UColumn {
        var switch1 by ustate(USTART)
        var switch2 by ustate(USTART)
        var switch3 by ustate(USTART)
        var switch4 by ustate(USTART)
        var rendering by ustate("DOM")
        UAllStartColumn {
            UText("Align switches:")
            val options = UAlignmentType.values().map { it.css }.toTypedArray()
            UTabs(*options) { idx, tab -> switch1 = UAlignmentType.css(tab) }
            UTabs(*options) { idx, tab -> switch2 = UAlignmentType.css(tab) }
            UTabs(*options) { idx, tab -> switch3 = UAlignmentType.css(tab) }
            UTabs(*options) { idx, tab -> switch4 = UAlignmentType.css(tab) }
            UText("Rendering switch:")
            UTabs("DOM", "Canvas", "DOM and Canvas") { idx, tab -> rendering = tab }
        }
        URow {
            if ("DOM" in rendering) UBackgroundBox { UDemo0Content(switch1, switch2, switch3, switch4) }
            if ("Canvas" in rendering) USkikoBox { UDemo0Content(switch1, switch2, switch3, switch4) }
        }
    }
}

@Composable private fun UDemo0Content(switch1: UAlignmentType, switch2: UAlignmentType, switch3: UAlignmentType, switch4: UAlignmentType) {
    UAllStretch {
        UBox {
            UAlign(horizontal = switch1, vertical = switch2) {
                UColumn {
                    UBox { UBox { URow { UDemoTexts(3) } } }
                    UBox { UBox { URow { UDemoTexts(10) } } }
                    UTheme(lightBluishUColors()) { UBox { UBox { UBox { UDemoTexts() } } } }
                    UTheme(m3UColors()) { UBox { UBox { UBox { UDemoTexts() } } } }
                    UAlign(horizontal = switch3, vertical = switch4) {
                        UBox { UBox { UColumn { UDemoTexts(10, growFactor = 3) } } }
                    }
                }
            }
        }
    }
}

@Composable fun UDemoTexts(
    count: Int = 20,
    center: Boolean = true,
    bold: Boolean = true,
    mono: Boolean = true,
    growFactor: Int = 1,
) {
    repeat(count) { i ->
        val c = 'A' + i
        val s = "$c".repeat(1 + i * growFactor)
        UText(s, center = center, bold = bold, mono = mono)
    }
}

@Composable fun UDemo1(withHorizontalScroll: Boolean = true, withVerticalScroll: Boolean = true) =
    URow(Mod.uscroll(withHorizontalScroll, withVerticalScroll)) {
        // FIXME: adjust default colors etc. so states like selected, clickable, etc are visible
        UTheme(lightUColors()) { SomeTree() }
        UTheme(darkUColors()) { SomeTree() }
        UTheme(lightBluishUColors()) { SomeTree() }
        UTheme(lightUColors()) {
            UColumn {
                val enabled = ustate(false)
                USwitch(enabled, "enabled: yes", "enabled: noo")
                UBoxEnabledIf(enabled.value) { SomeTree() }
                // FIXME later: disabling on JVM
            }
        }
    }

@Composable fun UDemo2(size: DpSize, withHorizontalScroll: Boolean = true, withVerticalScroll: Boolean = true) =
    UColumn(Mod.usize(size).uscroll(withHorizontalScroll, withVerticalScroll)) {
        UDemoTexts(growFactor = 4)
    }

@Composable private fun SomeTree() = UColumn {
    val selected = ustate(false)
    USwitch(selected, "selected: yes", "selected: noo")
    UBox(selected = selected.value) {
        UMenuTree(
            "XYZ".cbtree(
                "AAA".cbtree(
                    "aaa1".cbtree { ulogw("aaa1") },
                    "aaa2".cbtree { ulogw("aaa2") },
                ),
                "BBB".cbtree(
                    "bbb1".cbtree { ulogw("bbb1") },
                    "bbb2".cbtree { ulogw("bbb2") },
                    "bCCC".cbtree(
                        "ccc".cbtree { ulogw("ccc") }
                    )
                ),
            ),
            Dispatchers.Default
        )
    }
}

@Composable fun UDemo3(size: DpSize, withHorizontalScroll: Boolean, withVerticalScroll: Boolean) {
    USkikoBox {
        UAllStretch {
            UDemo3TabsSki(size, withHorizontalScroll, withVerticalScroll)
        }
    }
}
