package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.test.junit4.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.playgrounds.*
import pl.mareklangiewicz.ulog.*
import pl.mareklangiewicz.umath.*
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.uwidgets.UContainerType.*
import kotlin.math.*

fun ComposeContentTestRule.MyExaminedLayoutUSpekFun() = with(density) {

    val ureports = UReports { ulogw("rspek ${it.ustr}") } // rspek so I can filter logs with uspek/rspek/spek

    operator fun UReports.get(idx: Int) = history[idx]

    fun UReports.eqAt(vararg indices: Int) {
        val expected = this[indices[0]]
        for (i in indices.drop(1)) this[i].hasKeyAndData(expected.second, expected.third)
    }

    ureports.history.clear()

    "On MyExaminedLayout" o {
        var type by mutableStateOf(UBOX)
        var rigidSizeDp by mutableStateOf(400.dp.square)
        val rigidSizePx = rigidSizeDp.toSize().copyRoundToIntSize()
        var withSon1Cyan by mutableStateOf(false)
        var withSon2Red by mutableStateOf(false)
        var withSon3Green by mutableStateOf(false)
        var withSon4Blue by mutableStateOf(false)
        setContent {
            MyExaminedLayout(
                type = type,
                size = rigidSizeDp,
                withSon1Cyan = withSon1Cyan,
                withSon2Red = withSon2Red,
                withSon3Green = withSon3Green,
                withSon4Blue = withSon4Blue,
                onUReport = ureports::invoke
            )
        }
        waitForIdle()

        "With rigid father type UBOX" o {

            "With no children" o {

                "only root rigid father is measured and placed" o { ureports.history.size eq 3 }

                "rigid father gets measured with fixed constraints" o {
                    ureports[0].hasKeyAndData("rigid father measure with", rigidSizePx.copyToAllConstraints())
                    ureports[1].hasKeyAndData("rigid father measured", rigidSizePx.copyToUPlaceableData())
                }
                "rigid father is placed and attached" o {
                    ureports[2].hasPlacement("rigid father") { size == rigidSizePx && isAttached }
                }
            }

            "When cyan son gets enabled" o {
                withSon1Cyan = true
                waitForIdle()

                "rigid father starts measure again with the same constraints" o { ureports.eqAt(3, 0) }

                val cyanSonSizePx = 160.dp.square.toSize().copyRoundToIntSize()
                "cyan son gets measured" o {
                    ureports[4].hasKeyAndData("cyan son outer measure with", rigidSizePx.copyToMaxConstraints())
                    ureports[5].hasKeyAndData("cyan son inner measure with", cyanSonSizePx.copyToAllConstraints())
                    ureports[6].hasKeyAndData("cyan son inner measured", cyanSonSizePx.copyToUPlaceableData())
                    ureports[7].hasKeyAndData("cyan son outer measured", cyanSonSizePx.copyToUPlaceableData())
                }

                "rigid father gets remeasured and placed the same way as before" o {
                    ureports.eqAt(8, 1)
                    ureports.eqAt(9, 2)
                }

                "cyan son gets placed on bottom left side" o {
                    ureports[10].hasPlacement("cyan son outer") {
                        size == cyanSonSizePx && boundsInParent.left == 0f && boundsInParent.bottom.roundToInt() == rigidSizePx.height
                    }
                    ureports[11].hasPlacement("cyan son inner") {
                        size == cyanSonSizePx && boundsInParent.left == 0f && boundsInParent.bottom.roundToInt() == rigidSizePx.height
                    }
                }

                "rigid father starts measure again with the same constraints" o { ureports[12] eq ureports[0] }

                "When blue son stretched both ways gets enabled" o {
                    withSon4Blue = true
                    waitForIdle()

                    "blue son gets measured" o {
                        ureports[13].hasKeyAndData("blue son outer measure with", rigidSizePx.copyToAllConstraints())
                        ureports[14].hasKeyAndData("blue son inner measure with", rigidSizePx.copyToAllConstraints())
                        ureports[15].hasKeyAndData("blue son inner measured", rigidSizePx.copyToUPlaceableData())
                        ureports[16].hasKeyAndData("blue son outer measured", rigidSizePx.copyToUPlaceableData())
                    }
                    "rigid father gets remeasured and placed the same way" o {
                        ureports.eqAt(17, 1)
                        ureports.eqAt(18, 2)
                    }
                    "cyan son gets placed again the same way" o {
                        ureports.eqAt(19, 10)
                        ureports.eqAt(20, 11)
                    }
                    "blue son gets placed with fixed rigid father size" o {
                        ureports[21].hasPlacement("blue son outer") { size == rigidSizePx && positionInParent == Offset.Zero }
                        ureports[22].hasPlacement("blue son inner") { size == rigidSizePx && positionInParent == Offset.Zero }
                    }
                    "no other reports" o { ureports.history.size eq 23 }
                }

                "When green son stretched horizontally gets enabled" o {
                    withSon3Green = true
                    waitForIdle()

                    val greenSonSizePx = 60.dp.square.toSize().copyRoundToIntSize()
                    "green son gets measured" o {
                        ureports[13].hasKeyAndData("green son outer measure with", rigidSizePx.copyToAllConstraints(minH = 0))
                        ureports[14].hasKeyAndData("green son inner measure with", rigidSizePx.copyToAllConstraints(minH = greenSonSizePx.height, maxH = greenSonSizePx.height))
                        ureports[15].hasKeyAndData("green son inner measured", rigidSizePx.copyToUPlaceableData(h = greenSonSizePx.height))
                        ureports[16].hasKeyAndData("green son outer measured", rigidSizePx.copyToUPlaceableData(h = greenSonSizePx.height))
                    }
                    "rigid father gets remeasured and placed the same way" o {
                        ureports.eqAt(17, 1)
                        ureports.eqAt(18, 2)
                    }
                    "cyan son gets placed again the same way" o {
                        ureports.eqAt(19, 10)
                        ureports.eqAt(20, 11)
                    }
                    "green son gets placed stretched horizontally" o {
                        ureports[21].hasPlacement("green son outer") {
                            size.width == rigidSizePx.width
                                && size.height == greenSonSizePx.height
                                && boundsInParent.left == 0f
                                && boundsInParent.bottom.roundToInt() == rigidSizePx.height
                        }
                        ureports[22].hasPlacement("green son inner") {
                            size.width == rigidSizePx.width
                                && size.height == greenSonSizePx.height
                                && boundsInParent.left == 0f
                                && boundsInParent.bottom.roundToInt() == rigidSizePx.height
                        }
                    }
                    "no other reports" o { ureports.history.size eq 23 }
                }
                // TODO: other types UROW UCOLUMN
            }
        }
    }
}
