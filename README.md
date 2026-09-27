# μWidgets

Micro Widgets for Compose Multiplatform.

1. Goals
  * Simple implementation
  * Simple to use
  * Useful especially to show a lot of details even on small (micro) devices
  * Useful especially for different kind of monitoring / debug info UI.
2. Usage
  * Dependency: `pl.mareklangiewicz:uwidgets:0.0.48` (Maven Central; targets: jvm, js, android)
  * Wrap your content in the platform implementation, or any widget fails with
    "No UWidgets implementation provided.":
    * jvm (desktop): `UWidgetsAwt { ... }` (native windows for `UWindow`)
    * android: `UWidgetsSki { ... }`
    * js (DOM, via compose-html): `UWidgetsDom { ... }`
  * A js app needs Compose UI on js even if it only renders compose-html, because the widgets take
    Compose UI `Modifier` (`Mod`) params: in DepsKt that is `LibCompose(withComposeUiOnJs = true)`.
    The bundle then ships skiko (wasm).
  * Examples: `uwidgets-demo` (the `UDemo()` composable) and the apps running it:
    `uwidgets-demo-app` (jvm, js) and `uwidgets-demo-app-andro` (android).
3. TODO
  * TODO later: publish UDemo/JS with github actions
    * https://docs.github.com/en/pages/getting-started-with-github-pages/configuring-a-publishing-source-for-your-github-pages-site#publishing-with-a-custom-github-actions-workflow
    * https://github.com/hfhbd/bootstrap-compose/pull/257/files
  * TODO later: mordant backend
    * https://github.com/ajalt/mordant
  * TODO someday maybe: gtk-kn backend
    * https://gitlab.com/gtk-kn/gtk-kn/-/boards
    * but I guess I would first try to wrap their canvas to have compose native widgets
    * and maybe second "subproject" would be to wrap gtk-kn widgets in uwidgets as separate option
