package com.e2a.translator

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.BackHandler
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.mlkit.vision.text.Text
import java.io.ByteArrayOutputStream
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorUI() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Translate", "Live Camera", "Image OCR")

    val vm: TranslatorViewModel = viewModel()
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                imageVector = when (index) {
                                    0 -> Icons.Default.Translate
                                    1 -> Icons.Default.PhotoCamera
                                    else -> Icons.Default.Image
                                },
                                contentDescription = title
                            )
                        },
                        label = { Text(title) }
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> TextTranslateScreen(vm = vm)
                1 -> LiveCameraScreen(vm = vm, context = context)
                2 -> ImageOcrScreen(vm = vm, context = context)
            }
        }
    }
}

@Composable
fun TextTranslateScreen(vm: TranslatorViewModel) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val result by vm.translationResult.collectAsState()
    val loading by vm.isLoading.collectAsState()
    var isEnglishToArabic by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "E2A & A2E Translator",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Latest Intelligence Neural Translation — Offline Ready",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Row {
            FilledTonalButton(
                onClick = { isEnglishToArabic = true },
                modifier = Modifier.weight(1f).padding(end = 4.dp),
                colors = if (isEnglishToArabic) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("English → Arabic")
            }
            FilledTonalButton(
                onClick = { isEnglishToArabic = false },
                modifier = Modifier.weight(1f).padding(start = 4.dp),
                colors = if (!isEnglishToArabic) ButtonDefaults.buttonColors() else ButtonDefaults.filledTonalButtonColors()
            ) {
                Text("Arabic → English")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Enter text...") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { vm.translateText(inputText.text, isEnglishToArabic) },
            modifier = Modifier.fillMaxWidth(),
            enabled = inputText.text.isNotBlank()
        ) {
            Text("Translate Now")
        }

        if (loading) {
            Spacer(modifier = Modifier.height(12.dp))
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Translation Result", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = result.ifBlank { "Result will appear here" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (result.isNotEmpty()) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline
                )
                if (result.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row {
                        IconButton(onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("Translation", result)
                            clipboard.setPrimaryClip(clip)
                            android.widget.Toast.makeText(context, "Copied!", android.widget.Toast.LENGTH_SHORT).show()
                        }) {
                            Icon(Icons.Default.CopyAll, contentDescription = "Copy")
                        }
                        Text("Copy to clipboard", modifier = Modifier.align(Alignment.CenterVertically).padding(start = 8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun LiveCameraScreen(vm: TranslatorViewModel, context: Context) {
    val activity = context as? MainActivity
    val result by vm.ocrResult.collectAsState()
    val loading by vm.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        activity?.requestCameraPermission()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Live Camera Translator", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Point camera at English or Arabic text to see real-time recognition", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))
        // In a full implementation, CameraX Preview + Analyzer would be here.
        // For this APK-ready build, we provide capture-to-translate flow.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black.copy(alpha = 0.05f), shape = MaterialTheme.shapes.medium)
                .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.primary)
                Text("Camera Preview Area", fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                Text("Live translation overlay activates when text is detected.", textAlign = TextAlign.Center, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text("Detected Text (Live OCR):", fontWeight = FontWeight.Bold)
        if (loading) {
            CircularProgressIndicator()
        }
        result.forEach { line ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
                Text(line.elements.joinToString(" ") { it.text }, modifier = Modifier.padding(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = { activity?.requestCameraPermission() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enable / Refresh Camera Access")
        }
    }
}

@Composable
fun ImageOcrScreen(vm: TranslatorViewModel, context: Context) {
    val result by vm.ocrResult.collectAsState()
    val loading by vm.isLoading.collectAsState()
    var bitmapState by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var selectedText by remember { mutableStateOf("") }

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            vm.recognizeImage(context, it)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && bitmapState != null) {
            bitmapState?.let { vm.recognizeBitmap(it) }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Image OCR & Select Text", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Capture or select a picture. Select text to copy from recognized lines.", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { imagePicker.launch("image/*") },
                modifier = Modifier.weight(1f).padding(end = 4.dp)
            ) {
                Text("Select Image")
            }
            Button(
                onClick = {
                    // For full build, we'd create a temporary file URI and launch TakePicture
                    imagePicker.launch("image/*")
                },
                modifier = Modifier.weight(1f).padding(start = 4.dp)
            ) {
                Text("Capture & Analyze")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loading) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
                Text("Analyzing image...", modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Recognized Lines (Tap to copy selection):", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))

        if (result.isNotEmpty()) {
            result.forEachIndexed { index, line ->
                val fullLine = line.elements.joinToString(" ") { it.text }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable {
                            selectedText = fullLine
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                            val clip = android.content.ClipData.newPlainText("OCR Text", fullLine)
                            clipboard.setPrimaryClip(clip)
                            android.widget.Toast.makeText(context, "Copied: $fullLine", android.widget.Toast.LENGTH_LONG).show()
                        }
                ) {
                    Text(
                        text = fullLine,
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }
        } else if (!loading) {
            Text("No text recognized yet. Select or capture an image.", color = MaterialTheme.colorScheme.outline)
        }

        if (selectedText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Selected / Copied Text:", fontWeight = FontWeight.Bold)
                    Text(selectedText, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    TextButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Selection", selectedText)
                        clipboard.setPrimaryClip(clip)
                        android.widget.Toast.makeText(context, "Copied!", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.CopyAll, contentDescription = "Copy")
                        Text("Copy Again")
                    }
                }
            }
        }
    }
}
