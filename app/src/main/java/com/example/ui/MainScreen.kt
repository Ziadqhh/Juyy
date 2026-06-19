package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.MarbleItem
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToReports: () -> Unit,
    onNavigateToStats: () -> Unit
) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val sortType by viewModel.sortType.collectAsStateWithLifecycle()

    var showClearDialog by remember { mutableStateOf(false) }

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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onPrimary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "حاسبة الرخام",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateToStats) {
                            Icon(Icons.Default.PieChart, contentDescription = "الإحصائيات", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = onNavigateToReports) {
                            Icon(Icons.Default.PictureAsPdf, contentDescription = "التقارير", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { showClearDialog = true }) {
                            Icon(Icons.Default.DeleteForever, contentDescription = "مسح الكل", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            InputFormCard(
                onAdd = { type, length, width, count ->
                    viewModel.addItem(type, length, width, count)
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            SummaryCard(items = items)
            Spacer(modifier = Modifier.height(16.dp))
            FiltersRow(
                query = searchQuery,
                onQueryChange = viewModel::setSearchQuery,
                sortType = sortType,
                onSortChange = viewModel::setSortType
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items, key = { it.id }) { item ->
                    MarbleItemCard(
                        item = item,
                        onDelete = { viewModel.deleteItem(it) },
                        onEdit = { viewModel.updateItem(it) } // edit will be handled inside card with a dialog
                    )
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("تأكيد المسح") },
            text = { Text("هل أنت متأكد من مسح جميع البيانات؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAll()
                    showClearDialog = false
                }) {
                    Text("نعم، مسح", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
fun InputFormCard(onAdd: (String, Double, Double, Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var type by remember { mutableStateOf("") }
            var lengthStr by remember { mutableStateOf("") }
            var widthStr by remember { mutableStateOf("") }
            var countStr by remember { mutableStateOf("") }
            var isError by remember { mutableStateOf(false) }

            val tfColors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedBorderColor = MaterialTheme.colorScheme.primary
            )

            OutlinedTextField(
                value = type,
                onValueChange = { type = it },
                placeholder = { Text("اسم الرخام (مثال: كرارة إيطالي)", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = tfColors
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = lengthStr,
                    onValueChange = { lengthStr = it },
                    placeholder = { Text("الطول (م)", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    colors = tfColors
                )
                OutlinedTextField(
                    value = widthStr,
                    onValueChange = { widthStr = it },
                    placeholder = { Text("العرض (م)", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = MaterialTheme.shapes.small,
                    colors = tfColors
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = countStr,
                onValueChange = { countStr = it },
                placeholder = { Text("العدد", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = MaterialTheme.shapes.small,
                colors = tfColors
            )
            if (isError) {
                Text("تأكد من إدخال قيم صحيحة وأكبر من الصفر", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val length = lengthStr.toDoubleOrNull()
                    val width = widthStr.toDoubleOrNull()
                    val count = countStr.toIntOrNull()
                    if (type.isNotBlank() && length != null && length > 0 && width != null && width > 0 && count != null && count > 0) {
                        onAdd(type.trim(), length, width, count)
                        type = ""
                        lengthStr = ""
                        widthStr = ""
                        countStr = ""
                        isError = false
                    } else {
                        isError = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = MaterialTheme.shapes.small
            ) {
                Text("إضافة للقياسات", fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SummaryCard(items: List<MarbleItem>) {
    val totalCount = items.sumOf { it.count }
    val totalArea = items.sumOf { it.totalArea }
    val uniqueTypes = items.distinctBy { it.type.lowercase(Locale.ROOT) }.size
    val largestItem = items.maxByOrNull { it.totalArea }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "إجمالي المساحة المنفذة",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", totalArea),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Black
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "م²",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
                Surface(
                    color = Color.White.copy(alpha = 0.4f),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Layers,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "عدد الأصناف",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$uniqueTypes نوع",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "إجمالي القطع",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "$totalCount قطعة",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "أكبر صنف",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                    Text(
                        text = largestItem?.type ?: "-",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersRow(
    query: String,
    onQueryChange: (String) -> Unit,
    sortType: SortType,
    onSortChange: (SortType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text("بحث...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.weight(1f),
            singleLine = true
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box {
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Default.Sort, contentDescription = "ترتيب")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text("حسب الاسم") },
                    onClick = { onSortChange(SortType.NAME); expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("حسب المساحة") },
                    onClick = { onSortChange(SortType.AREA); expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("حسب العدد") },
                    onClick = { onSortChange(SortType.COUNT); expanded = false }
                )
                DropdownMenuItem(
                    text = { Text("حسب الأحدث") },
                    onClick = { onSortChange(SortType.DATE); expanded = false }
                )
            }
        }
    }
}

@Composable
fun MarbleItemCard(
    item: MarbleItem,
    onDelete: (MarbleItem) -> Unit,
    onEdit: (MarbleItem) -> Unit
) {
    var showEdit by remember { mutableStateOf(false) }

    if (showEdit) {
        EditDialog(item = item, onDismiss = { showEdit = false }, onSave = { updated ->
            onEdit(updated)
            showEdit = false
        })
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = androidx.compose.foundation.shape.CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "${item.length.toInt()}x${item.width.toInt()}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = item.type,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                    )
                    Text(
                        text = "${item.count} قطع • قطعة ${String.format("%.1f", item.pieceArea)}م²",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format("%.2f", item.totalArea)} م²",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(modifier = Modifier.offset(x = 8.dp)) {
                    IconButton(onClick = { showEdit = true }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "تعديل", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = { onDelete(item) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EditDialog(item: MarbleItem, onDismiss: () -> Unit, onSave: (MarbleItem) -> Unit) {
    var type by remember { mutableStateOf(item.type) }
    var lengthStr by remember { mutableStateOf(item.length.toString()) }
    var widthStr by remember { mutableStateOf(item.width.toString()) }
    var countStr by remember { mutableStateOf(item.count.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تعديل الصنف") },
        text = {
            Column {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("النوع") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = lengthStr,
                    onValueChange = { lengthStr = it },
                    label = { Text("الطول (م)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = widthStr,
                    onValueChange = { widthStr = it },
                    label = { Text("العرض (م)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = countStr,
                    onValueChange = { countStr = it },
                    label = { Text("العدد") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val length = lengthStr.toDoubleOrNull()
                val width = widthStr.toDoubleOrNull()
                val count = countStr.toIntOrNull()
                if (type.isNotBlank() && length != null && length > 0 && width != null && width > 0 && count != null && count > 0) {
                    onSave(item.copy(type = type, length = length, width = width, count = count))
                }
            }) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}
