package com.e2a.translator

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TranslatorUI() {
    val vm: TranslatorViewModel = viewModel()

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

        TextTranslateScreen(vm = vm)
    }
}

@Composable
fun TextTranslateScreen(vm: TranslatorViewModel) {
    val context = LocalContext.current
    var inputText by remember { mutableStateOf(TextFieldValue("")) }
    val result by vm.translationResult.collectAsState()
    val loading by vm.isLoading.collectAsState()
    var isEnglishToArabic by remember { mutableStateOf(true) }

    Row(modifier = Modifier.fillMaxWidth()) {
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

    OutlinedTextField(
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
        CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Translation", result)
                        clipboard.setPrimaryClip(clip)
                        android.widget.Toast.makeText(context, "Copied!", android.widget.Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Copy")
                    }
                    Text("Copy to clipboard")
                }
            }
        }
    }
}
