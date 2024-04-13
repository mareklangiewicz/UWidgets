package pl.mareklangiewicz.udemo

import androidx.compose.runtime.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.unit.*
import pl.mareklangiewicz.udata.*
import pl.mareklangiewicz.text.*
import pl.mareklangiewicz.uspek.*
import pl.mareklangiewicz.uwidgets.*
import pl.mareklangiewicz.uwidgets.UBinType.*
import kotlin.math.*

// TODO_later: use context receiver for UComposeScope
@Suppress("CanBeVal")
suspend fun UComposeScope.MyExaminedLayoutUSpekFun() = with(density) {

  ureports.clear()

  "On MyExaminedLayout" so {
    var type by mutableStateOf(UBOX)
    var rigidSizeDp by mutableStateOf(400.dp.square)
    val rigidSizePx = rigidSizeDp.toSize().copyRoundToIntSize()
    var withSon1Cyan by mutableStateOf(false)
    var withSon2Red by mutableStateOf(false)
    var withSon3Green by mutableStateOf(false)
    var withSon4Blue by mutableStateOf(false)
    setContent {
      USkikoBox {
        MyExaminedLayout(
          type = type,
          contentSize = rigidSizeDp,
          withSon1Cyan = withSon1Cyan,
          withSon2Red = withSon2Red,
          withSon3Green = withSon3Green,
          withSon4Blue = withSon4Blue,
          onUReport = ureports::invoke,
        )
      }
    }
    awaitIdle()

    "With rigid father type UBOX" so {

      "With no children" so {

        "only root rigid father is composed measured and placed" so {
          true eq ureports.all {
            it.key.startsWith("rigid father") && it.key.containsAny("compose", "measure", "place")
          }
        }

        "rigid father gets measured with fixed constraints" so {
          ureports[1].hasKeyAndData("rigid father measure in", rigidSizePx.copyToAllConstraints())
          ureports[2].hasKeyAndData("rigid father measured", rigidSizePx)
        }
        "rigid father gets placed without children" so {
          ureports[3].hasKeyAndData("rigid father place in", rigidSizePx)
          ureports[4].hasKeyAndData("rigid father placed count", 0)
        }
      }

      "When cyan son gets enabled" so {
        withSon1Cyan = true
        awaitIdle()

        "cyan son is composed" so { ureports[5].hasKeyAndData("cyan son inner compose", UBOX) }

        "rigid father starts measure 2nd time with the same constraints" so { ureports.eqAt(6, 1) }

        val cyanSonSizePx = 150.dp.square.toSize().copyRoundToIntSize()
        "cyan son gets measured" so {
          ureports[7].hasKeyAndData("cyan son outer measure in", rigidSizePx.copyToMaxConstraints())
          ureports[8].hasKeyAndData("cyan son inner measure in", cyanSonSizePx.copyToAllConstraints())
          ureports[9].hasKeyAndData("cyan son inner measured", cyanSonSizePx)
          ureports[10].hasKeyAndData("cyan son outer measured", cyanSonSizePx.copyToUPlaceableData())
        }

        "rigid father gets remeasured and place in the same way as before" so {
          ureports.eqAt(11, 2)
          ureports.eqAt(12, 3)
        }

        "cyan son gets placed on bottom left side" so {
          ureports[13].hasPlacedCoordinates("cyan son outer") {
            size == cyanSonSizePx && boundsInParent.left == 0f && boundsInParent.bottom.roundToInt() == rigidSizePx.height
          }
          ureports[14].hasKeyAndData("cyan son inner place in", cyanSonSizePx)
          ureports[15].hasKeyAndData("cyan son inner placed count", 0)
        }

        "rigid father is placed with one child" so { ureports[16].hasKeyAndData("rigid father placed count", 1) }

        "When blue son stretched both ways gets enabled" so {
          withSon4Blue = true
          awaitIdle()

          "blue son is composed" so { ureports[17].hasKeyAndData("blue son inner compose", UBOX) }

          "rigid father starts remeasure" so { ureports.eqAt(18, 1) }

          "blue son gets measured" so {
            ureports[19].hasKeyAndData("blue son outer measure in", rigidSizePx.copyToAllConstraints())
            ureports[20].hasKeyAndData("blue son inner measure in", rigidSizePx.copyToAllConstraints())
            ureports[21].hasKeyAndData("blue son inner measured", rigidSizePx)
            ureports[22].hasKeyAndData("blue son outer measured", rigidSizePx.copyToUPlaceableData())
          }
          "rigid father is measured and place in the same way" so {
            ureports.eqAt(23, 2)
            ureports.eqAt(24, 3)
          }
          "cyan son outer gets placed again the same way" so { ureports.eqAt(25, 13) }
          "cyan son inner placing is skipped" so { false eq ureports[26].key.startsWith("cyan") }
          // probably because compose notice there was nothing inside to actually place

          "blue son gets placed with fixed rigid father size" so {
            ureports[26].hasPlacedCoordinates("blue son outer") { size == rigidSizePx && positionInParent == Offset.Zero }
            ureports[27].hasKeyAndData("blue son inner place in", rigidSizePx)
            ureports[28].hasKeyAndData("blue son inner placed count", 0)
          }
          "rigid father is placed with two children" so { ureports[29].hasKeyAndData("rigid father placed count", 2) }
          "no other reports" so { ureports.size eq 30 }
        }

        "When green son stretched horizontally gets enabled" so {
          withSon3Green = true
          awaitIdle()

          "green son is composed" so { ureports[17].hasKeyAndData("green son inner compose", UBOX) }

          "rigid father starts remeasure" so { ureports.eqAt(18, 1) }

          val greenSonSizePx = 60.dp.square.toSize().copyRoundToIntSize()
          val greenSonActualSizePx = IntSize(rigidSizePx.width, greenSonSizePx.height)
          "green son gets measured" so {
            ureports[19].hasKeyAndData("green son outer measure in", rigidSizePx.copyToAllConstraints(minH = 0))
            ureports[20].hasKeyAndData("green son inner measure in", greenSonActualSizePx.copyToAllConstraints())
            ureports[21].hasKeyAndData("green son inner measured", greenSonActualSizePx)
            ureports[22].hasKeyAndData("green son outer measured", greenSonActualSizePx.copyToUPlaceableData())
          }
          "rigid father is measured and place in the same way" so {
            ureports.eqAt(23, 2)
            ureports.eqAt(24, 3)
          }
          "cyan son outer gets placed again the same way" so { ureports.eqAt(25, 13) }
          "cyan son inner placing is skipped" so { false eq ureports[26].key.startsWith("cyan") }
          // probably because compose notice there was nothing inside to actually place

          "green son gets placed stretched horizontally" so {
            ureports[26].hasPlacedCoordinates("green son outer") {
              size == greenSonActualSizePx
                && boundsInParent.left == 0f
                && boundsInParent.bottom.roundToInt() == rigidSizePx.height
            }
            ureports[27].hasKeyAndData("green son inner place in", greenSonActualSizePx)
            ureports[28].hasKeyAndData("green son inner placed count", 0)
          }
          "rigid father is placed with two children" so { ureports[29].hasKeyAndData("rigid father placed count", 2) }
          "no other reports" so { ureports.size eq 30 }
        }
        // TODO: other types UROW UCOLUMN
      }
    }
  }
}
