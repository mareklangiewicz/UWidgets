package utheme

import androidx.compose.ui.graphics.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import pl.mareklangiewicz.utheme.*

@Composable fun m3UColors(
    uboxContent: Color = MaterialTheme.colorScheme.primary,
    uboxBaseBackground: Color = MaterialTheme.colorScheme.background,
    uboxTintBackground: Color = MaterialTheme.colorScheme.onBackground,
    uboxTintBorder: Color = MaterialTheme.colorScheme.secondary.copy(alpha = .1f),
) = UColors(uboxContent, uboxBaseBackground, uboxTintBackground, uboxTintBorder)

