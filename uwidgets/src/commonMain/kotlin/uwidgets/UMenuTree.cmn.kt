package pl.mareklangiewicz.uwidgets

import androidx.compose.runtime.*
import kotlinx.coroutines.*


data class UCallbackTree(
  val label: String? = null,
  val subtrees: List<UCallbackTree> = emptyList(),
  val callback: (suspend () -> Unit)? = null,
)

fun String.cbtree(vararg subtrees: UCallbackTree, callback: (suspend () -> Unit)? = null) =
  UCallbackTree(this, subtrees.toList(), callback)

// TODO: pass callback dispatcher as context receiver
// later: if it works: also pass around simple logger as context receiver instead of println/whateva
@Composable
fun UMenuTree(tree: UCallbackTree, dispatcher: CoroutineDispatcher) {
  val scope = rememberCoroutineScope()
  val subt = tree.subtrees
  require(subt.isEmpty() || tree.callback == null) { "Tree with sub trees and callback" }
  when {
    subt.isEmpty() -> UText(
      text = tree.label!!,
      mod = Mod.onUClick { scope.launch(dispatcher) { tree.callback?.invoke() } },
      mono = true,
    )

    else -> UColumn {
      tree.label?.let { UText(it, center = true, bold = true, mono = true) }
      for (t in subt) UMenuTree(t, dispatcher)
    }
  }
}

// TODO_later: UButton that appears clickable on JVM AND JS; use it in all places where it makes sense

@Composable
fun UMenuTreeWithFilter(tree: UCallbackTree, dispatcher: CoroutineDispatcher) {
  // TODO_NOW: filtering with Kim
  UColumn {
//            logw("ARARARA") // FIXME: see if correctly called (no looping) and remove
//            MyFilter()
    UMenuTree(tree, dispatcher)
  }
}

//@Composable
// private fun MyFilter() {
//    val model = LocalKim.current
//    val mod = Wide.border(1.dp, if (model.isFocused) Color.Red else Color.Gray).padding(1.dp)
//    DBox(mod) { DText(model.text) }
//}


