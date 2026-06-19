package com.example.ui

import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MarbleItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val totalArea = items.sumOf { it.totalArea }

    val reportText = remember(items) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date())
        
        buildString {
            append("تقرير حاسبة الرخام للمشرفين\n")
            append("التاريخ والوقت: $dateString\n\n")
            append("--- الأصناف ---\n")
            items.forEachIndexed { index, item ->
                append("${index + 1}. ${item.type} | الأبعاد: ${item.length}×${item.width} | العدد: ${item.count} | المساحة: ${String.format("%.2f", item.totalArea)} م²\n")
            }
            append("\n======================\n")
            append("الإجمالي النهائي للمساحة: ${String.format("%.2f", totalArea)} م²\n")
            append("======================\n")
        }
    }

    Scaffold(
        topBar = {
            Surface(
                color = MaterialTheme.colorScheme.background,
                shadowElevation = 0.dp,
                modifier = Modifier.fillMaxWidth(),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "عودة", tint = MaterialTheme.colorScheme.onBackground)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "التقارير",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = reportText,
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { shareTextResult(context, reportText) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Share, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("مشاركة كنص")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { generateAndSharePdf(context, items, totalArea) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Icon(Icons.Default.PictureAsPdf, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("حفظ ومشاركة كـ PDF")
            }
        }
    }
}

private fun shareTextResult(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TITLE, "تقرير حاسبة الرخام")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "مشاركة التقرير"))
}

private fun generateAndSharePdf(context: Context, items: List<MarbleItem>, totalArea: Double) {
    try {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 approx
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        paint.textSize = 14f
        paint.isAntiAlias = true

        var yPos = 50f
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault())
        val dateString = sdf.format(Date())

        canvas.drawText("تقرير حاسبة الرخام للمشرفين", 50f, yPos, paint)
        yPos += 30f
        canvas.drawText("التاريخ والوقت: $dateString", 50f, yPos, paint)
        yPos += 40f

        canvas.drawText("--- الأصناف ---", 50f, yPos, paint)
        yPos += 30f

        for ((index, item) in items.withIndex()) {
            val line = "${index + 1}. ${item.type} | الأبعاد: ${item.length}×${item.width} | العدد: ${item.count} | المساحة: ${String.format("%.2f", item.totalArea)} م²"
            canvas.drawText(line, 50f, yPos, paint)
            yPos += 25f
        }

        yPos += 20f
        paint.isFakeBoldText = true
        canvas.drawText("الإجمالي النهائي للمساحة: ${String.format("%.2f", totalArea)} م²", 50f, yPos, paint)

        document.finishPage(page)

        val file = File(context.cacheDir, "MarbleReport.pdf")
        document.writeTo(FileOutputStream(file))
        document.close()

        val uri: Uri = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName + ".provider",
            file
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "مشاركة التقرير PDF"))

    } catch (e: Exception) {
        Toast.makeText(context, "Error generating PDF: ${e.message}", Toast.LENGTH_LONG).show()
    }
}
