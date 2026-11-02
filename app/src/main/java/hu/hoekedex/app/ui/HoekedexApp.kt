package hu.hoekedex.app.ui

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import hu.hoekedex.app.HoekedexUiState
import hu.hoekedex.app.HoekedexViewModel
import hu.hoekedex.app.data.Profile
import hu.hoekedex.app.data.ProfileImage
import java.util.Locale
import kotlin.math.round
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val DeepBordeaux = Color(0xFF4A0006)
private val DarkBordeaux = Color(0xFF311414)
private val CharcoalBlack = Color(0xFF181314)
private val WarmGrayBrown = Color(0xFF4A3A3A)
private val PaleRoseBordeaux = Color(0xFF8C5A5E)
private val CopperGoldBrown = Color(0xFFA86A45)
private val Accent = CopperGoldBrown
private val TextPrimary = CopperGoldBrown
private val TextSecondary = PaleRoseBordeaux
private val TextOnAccent = CharcoalBlack
private val GlassFill = DarkBordeaux.copy(alpha = 0.74f)
private val GlassStroke = WarmGrayBrown.copy(alpha = 0.70f)
private val FieldFill = CharcoalBlack.copy(alpha = 0.78f)

@Composable
fun HoekedexApp(viewModel: HoekedexViewModel = viewModel()) {
    val state = viewModel.uiState
    val snackbarHostState = remember { SnackbarHostState() }
    val selectedProfile = viewModel.selectedProfile

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.consumeError()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundBrush())
    ) {
        LandingScreen(
            state = state,
            profiles = viewModel.filteredProfiles,
            onQueryChange = viewModel::setQuery,
            onToggleFilters = { viewModel.setFiltersVisible(!state.filtersVisible) },
            onMinAgeChange = viewModel::setMinAge,
            onMaxAgeChange = viewModel::setMaxAge,
            onMinRatingChange = viewModel::setMinRating,
            onNationalityChange = viewModel::setNationality,
            onClearFilters = viewModel::clearFilters,
            onCreateProfile = viewModel::createProfile,
            onOpenProfile = viewModel::openProfile
        )

        AnimatedVisibility(
            visible = selectedProfile != null,
            enter = fadeIn() + scaleIn(initialScale = 0.98f),
            exit = fadeOut() + scaleOut(targetScale = 0.98f),
            modifier = Modifier.fillMaxSize()
        ) {
            selectedProfile?.let { profile ->
                ProfileDetailScreen(
                    profile = profile,
                    editMode = state.editMode,
                    onBack = viewModel::closeProfile,
                    onEdit = { viewModel.setEditMode(true) },
                    onCancelEdit = { viewModel.setEditMode(false) },
                    onSave = viewModel::saveProfile,
                    onDelete = viewModel::deleteProfile,
                    onAddImages = { uris -> viewModel.addImages(profile.id, uris) },
                    onDeleteImage = viewModel::deleteImage
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}

@Composable
private fun LandingScreen(
    state: HoekedexUiState,
    profiles: List<Profile>,
    onQueryChange: (String) -> Unit,
    onToggleFilters: () -> Unit,
    onMinAgeChange: (String) -> Unit,
    onMaxAgeChange: (String) -> Unit,
    onMinRatingChange: (Float) -> Unit,
    onNationalityChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onCreateProfile: () -> Unit,
    onOpenProfile: (Long) -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sidePadding = (maxWidth.value * 0.01f).dp
        val columnCount = if (maxWidth < 430.dp) 2 else 3
        val gridState = rememberLazyGridState()
        var topBarVisible by remember { mutableStateOf(true) }

        LaunchedEffect(gridState) {
            snapshotFlow {
                gridState.firstVisibleItemIndex * 1000 + gridState.firstVisibleItemScrollOffset
            }.collect { currentScroll ->
                topBarVisible = currentScroll < 18
            }
        }

        val topPadding by animateDpAsState(
            targetValue = when {
                !topBarVisible -> 20.dp
                state.filtersVisible -> 232.dp
                else -> 104.dp
            },
            label = "grid-top-padding"
        )

        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Fixed(columnCount),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = sidePadding,
                end = sidePadding,
                top = topPadding,
                bottom = 28.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (profiles.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyState(onCreateProfile = onCreateProfile)
                }
            }

            items(profiles, key = { it.id }) { profile ->
                var cardVisible by remember(profile.id) { mutableStateOf(false) }
                LaunchedEffect(profile.id) { cardVisible = true }

                AnimatedVisibility(
                    visible = cardVisible,
                    enter = fadeIn() + scaleIn(initialScale = 0.96f),
                    exit = fadeOut() + scaleOut(targetScale = 0.96f)
                ) {
                    ProfileCard(
                        profile = profile,
                        onClick = { onOpenProfile(profile.id) }
                    )
                }
            }
        }

        FloatingTopArea(
            visible = topBarVisible,
            state = state,
            onQueryChange = onQueryChange,
            onToggleFilters = onToggleFilters,
            onMinAgeChange = onMinAgeChange,
            onMaxAgeChange = onMaxAgeChange,
            onMinRatingChange = onMinRatingChange,
            onNationalityChange = onNationalityChange,
            onClearFilters = onClearFilters,
            onCreateProfile = onCreateProfile,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 10.dp)
                .fillMaxWidth(0.98f)
                .zIndex(3f)
        )
    }
}

@Composable
private fun FloatingTopArea(
    visible: Boolean,
    state: HoekedexUiState,
    onQueryChange: (String) -> Unit,
    onToggleFilters: () -> Unit,
    onMinAgeChange: (String) -> Unit,
    onMaxAgeChange: (String) -> Unit,
    onMinRatingChange: (Float) -> Unit,
    onNationalityChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    onCreateProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it } + fadeIn(),
        exit = slideOutVertically { -it } + fadeOut(),
        modifier = modifier
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(66.dp)
                    .glass(RoundedCornerShape(24.dp))
                    .padding(9.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchInput(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f)
                )
                GlassIconButton(
                    icon = Icons.Default.FilterList,
                    contentDescription = "Szűrők",
                    onClick = onToggleFilters,
                    selected = state.filtersVisible
                )
                GlassIconButton(
                    icon = Icons.Default.Add,
                    contentDescription = "Új profil",
                    onClick = onCreateProfile,
                    accent = true
                )
            }

            AnimatedVisibility(
                visible = state.filtersVisible,
                enter = fadeIn() + slideInVertically { -it / 2 },
                exit = fadeOut() + slideOutVertically { -it / 2 }
            ) {
                FilterPanel(
                    state = state,
                    onMinAgeChange = onMinAgeChange,
                    onMaxAgeChange = onMaxAgeChange,
                    onMinRatingChange = onMinRatingChange,
                    onNationalityChange = onNationalityChange,
                    onClearFilters = onClearFilters,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun SearchInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(18.dp))
            .background(FieldFill)
            .border(1.dp, WarmGrayBrown.copy(alpha = 0.82f), RoundedCornerShape(18.dp))
            .padding(horizontal = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(9.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (value.isBlank()) {
                Text(
                    text = "Keresés név alapján",
                    color = TextSecondary.copy(alpha = 0.74f),
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                cursorBrush = SolidColor(Accent),
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 15.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FilterPanel(
    state: HoekedexUiState,
    onMinAgeChange: (String) -> Unit,
    onMaxAgeChange: (String) -> Unit,
    onMinRatingChange: (Float) -> Unit,
    onNationalityChange: (String) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .glass(RoundedCornerShape(24.dp))
            .animateContentSize()
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassTextField(
                label = "Min. kor",
                value = state.minAge,
                onValueChange = onMinAgeChange,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
            GlassTextField(
                label = "Max. kor",
                value = state.maxAge,
                onValueChange = onMaxAgeChange,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(1f)
            )
        }
        GlassTextField(
            label = "Nemzetiség",
            value = state.nationality,
            onValueChange = onNationalityChange,
            modifier = Modifier.fillMaxWidth()
        )
        Column {
            Text(
                text = "Minimum rating: ${formatRating(state.minRating)}",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Slider(
                value = state.minRating,
                onValueChange = onMinRatingChange,
                valueRange = 0f..5f,
                steps = 9
            )
        }
        GlassActionButton(
            text = "Szűrők törlése",
            icon = Icons.Default.Close,
            onClick = onClearFilters,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EmptyState(onCreateProfile: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 420.dp)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .glass(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PhotoLibrary,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(34.dp)
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Nincs profil",
            color = TextPrimary,
            fontSize = 23.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "A + gombbal létrehozható az első adatlap.",
            color = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(18.dp))
        GlassActionButton(
            text = "Új profil",
            icon = Icons.Default.Add,
            onClick = onCreateProfile
        )
    }
}

@Composable
private fun ProfileCard(
    profile: Profile,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(0.72f)
            .glass(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        LocalProfileImage(
            path = profile.thumbnailPath,
            placeholderText = profile.displayName,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(86.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, CharcoalBlack.copy(alpha = 0.90f))
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(DarkBordeaux.copy(alpha = 0.86f))
                .padding(horizontal = 10.dp, vertical = 9.dp)
        ) {
            Text(
                text = profile.displayName,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${profile.age?.toString() ?: "-"} év  •  Rating ${formatRating(profile.rating)}",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileDetailScreen(
    profile: Profile,
    editMode: Boolean,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSave: (Profile) -> Unit,
    onDelete: (Profile) -> Unit,
    onAddImages: (List<Uri>) -> Unit,
    onDeleteImage: (ProfileImage) -> Unit
) {
    BackHandler(onBack = onBack)

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = onAddImages
    )

    var confirmDelete by remember { mutableStateOf(false) }
    var draftName by remember(profile.id, editMode) { mutableStateOf(profile.name) }
    var draftAge by remember(profile.id, editMode) { mutableStateOf(profile.age?.toString().orEmpty()) }
    var draftNationality by remember(profile.id, editMode) { mutableStateOf(profile.nationality) }
    var draftRating by remember(profile.id, editMode) { mutableStateOf(profile.rating) }
    var draftDescription by remember(profile.id, editMode) { mutableStateOf(profile.description) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(appBackgroundBrush())
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 88.dp,
                bottom = 34.dp
            ),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                DetailHero(profile = profile)
            }

            item {
                GlassPanel {
                    if (editMode) {
                        ProfileEditor(
                            name = draftName,
                            age = draftAge,
                            nationality = draftNationality,
                            rating = draftRating,
                            description = draftDescription,
                            onNameChange = { draftName = it },
                            onAgeChange = { draftAge = it.filter { char -> char.isDigit() }.take(3) },
                            onNationalityChange = { draftNationality = it },
                            onRatingChange = { draftRating = it },
                            onDescriptionChange = { draftDescription = it }
                        )
                    } else {
                        ProfileReadOnly(profile = profile)
                    }
                }
            }

            item {
                GallerySection(
                    profile = profile,
                    editMode = editMode,
                    onAddClick = { imagePicker.launch("image/*") },
                    onDeleteImage = onDeleteImage
                )
            }

            item {
                DetailActions(
                    editMode = editMode,
                    onEdit = onEdit,
                    onCancel = onCancelEdit,
                    onSave = {
                        val roundedRating = round(draftRating * 2f) / 2f
                        onSave(
                            profile.copy(
                                name = draftName,
                                age = draftAge.toIntOrNull()?.coerceIn(0, 150),
                                nationality = draftNationality,
                                rating = roundedRating,
                                description = draftDescription
                            )
                        )
                    },
                    onDelete = { confirmDelete = true }
                )
            }
        }

        GlassIconButton(
            icon = Icons.Default.ArrowBack,
            contentDescription = "Vissza",
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(start = 16.dp, top = 12.dp)
                .zIndex(3f)
        )
    }

    if (confirmDelete) {
        DeleteConfirmDialog(
            profileName = profile.displayName,
            onDismiss = { confirmDelete = false },
            onConfirm = {
                confirmDelete = false
                onDelete(profile)
            }
        )
    }
}

@Composable
private fun DetailHero(profile: Profile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.86f)
            .glass(RoundedCornerShape(30.dp))
    ) {
        LocalProfileImage(
            path = profile.thumbnailPath,
            placeholderText = profile.displayName,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(132.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, CharcoalBlack.copy(alpha = 0.93f))
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(20.dp)
        ) {
            Text(
                text = profile.displayName,
                color = TextPrimary,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${profile.age?.toString() ?: "-"} év  •  ${profile.nationality.ifBlank { "Nincs nemzetiség" }}  •  Rating ${formatRating(profile.rating)}",
                color = TextSecondary,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ProfileReadOnly(profile: Profile) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        InfoRow(label = "Név", value = profile.displayName)
        InfoRow(label = "Életkor", value = profile.age?.let { "$it év" } ?: "-")
        InfoRow(label = "Nemzetiség", value = profile.nationality.ifBlank { "-" })
        InfoRow(label = "Rating", value = formatRating(profile.rating))
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Leírás",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Text(
                text = profile.description.ifBlank { "Nincs leírás." },
                color = TextPrimary,
                fontSize = 15.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
private fun ProfileEditor(
    name: String,
    age: String,
    nationality: String,
    rating: Float,
    description: String,
    onNameChange: (String) -> Unit,
    onAgeChange: (String) -> Unit,
    onNationalityChange: (String) -> Unit,
    onRatingChange: (Float) -> Unit,
    onDescriptionChange: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        GlassTextField(
            label = "Név",
            value = name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GlassTextField(
                label = "Életkor",
                value = age,
                onValueChange = onAgeChange,
                keyboardType = KeyboardType.Number,
                modifier = Modifier.weight(0.8f)
            )
            GlassTextField(
                label = "Nemzetiség",
                value = nationality,
                onValueChange = onNationalityChange,
                modifier = Modifier.weight(1.2f)
            )
        }
        Column {
            Text(
                text = "Rating: ${formatRating(rating)}",
                color = TextSecondary,
                fontSize = 13.sp
            )
            Slider(
                value = rating,
                onValueChange = onRatingChange,
                valueRange = 0f..5f,
                steps = 9
            )
        }
        GlassTextField(
            label = "Leírás",
            value = description,
            onValueChange = onDescriptionChange,
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 112.dp)
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 13.sp
        )
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GallerySection(
    profile: Profile,
    editMode: Boolean,
    onAddClick: () -> Unit,
    onDeleteImage: (ProfileImage) -> Unit
) {
    var previewImage by remember(profile.id) { mutableStateOf<ProfileImage?>(null) }

    GlassPanel {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Galéria",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                if (editMode) {
                    GlassIconButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Kép hozzáadása",
                        onClick = onAddClick,
                        accent = true,
                        modifier = Modifier.size(42.dp)
                    )
                }
            }

            if (profile.images.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(116.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(FieldFill)
                        .border(1.dp, WarmGrayBrown.copy(alpha = 0.70f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhotoLibrary,
                            contentDescription = null,
                            tint = TextSecondary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Nincs kép hozzáadva",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                    val imageSize = ((maxWidth.value - 16f) / 3f).dp
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        maxItemsInEachRow = 3
                    ) {
                        profile.images.forEach { image ->
                            Box(
                                modifier = Modifier
                                    .size(imageSize)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(FieldFill)
                                    .border(
                                        1.dp,
                                        WarmGrayBrown.copy(alpha = 0.72f),
                                        RoundedCornerShape(18.dp)
                                    )
                                    .clickable { previewImage = image }
                            ) {
                                LocalProfileImage(
                                    path = image.path,
                                    placeholderText = profile.displayName,
                                    modifier = Modifier.fillMaxSize()
                                )
                                if (editMode) {
                                    GlassIconButton(
                                        icon = Icons.Default.Close,
                                        contentDescription = "Kép törlése",
                                        onClick = { onDeleteImage(image) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(5.dp)
                                            .size(32.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    previewImage?.let { image ->
        ImagePreviewDialog(
            image = image,
            profileName = profile.displayName,
            onDismiss = { previewImage = null }
        )
    }
}

@Composable
private fun DetailActions(
    editMode: Boolean,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (editMode) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassActionButton(
                    text = "Mentés",
                    icon = Icons.Default.Save,
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    accent = true
                )
                GlassActionButton(
                    text = "Mégse",
                    icon = Icons.Default.Close,
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                )
            }
            GlassActionButton(
                text = "Profil törlése",
                icon = Icons.Default.Delete,
                onClick = onDelete,
                modifier = Modifier.fillMaxWidth(),
                danger = true
            )
        } else {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GlassActionButton(
                    text = "Szerkesztés",
                    icon = Icons.Default.Edit,
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    accent = true
                )
                GlassActionButton(
                    text = "Törlés",
                    icon = Icons.Default.Delete,
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    danger = true
                )
            }
        }
    }
}

@Composable
private fun ImagePreviewDialog(
    image: ProfileImage,
    profileName: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CharcoalBlack.copy(alpha = 0.97f))
                .padding(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .glass(RoundedCornerShape(26.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                LocalProfileImage(
                    path = image.path,
                    placeholderText = profileName,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            GlassIconButton(
                icon = Icons.Default.Close,
                contentDescription = "Képnézet bezárása",
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
            )
        }
    }
}

@Composable
private fun DeleteConfirmDialog(
    profileName: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkBordeaux,
        titleContentColor = TextPrimary,
        textContentColor = TextSecondary,
        title = { Text("Profil törlése") },
        text = { Text("Biztosan végleg törlöd ezt a profilt: $profileName?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Törlés", color = CopperGoldBrown)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Mégse", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun GlassPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glass(RoundedCornerShape(24.dp))
            .padding(16.dp),
        content = { content() }
    )
}

@Composable
private fun GlassTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 12.sp
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = if (singleLine) 46.dp else 96.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(FieldFill)
                .border(1.dp, WarmGrayBrown.copy(alpha = 0.70f), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                maxLines = if (singleLine) 1 else 6,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                cursorBrush = SolidColor(Accent),
                textStyle = TextStyle(
                    color = TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 21.sp
                ),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun GlassIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    selected: Boolean = false
) {
    val shape = RoundedCornerShape(18.dp)
    val background = when {
        accent -> Brush.linearGradient(listOf(CopperGoldBrown, PaleRoseBordeaux))
        selected -> Brush.linearGradient(listOf(PaleRoseBordeaux, DeepBordeaux))
        else -> Brush.linearGradient(listOf(DarkBordeaux.copy(alpha = 0.92f), WarmGrayBrown.copy(alpha = 0.46f)))
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .shadow(16.dp, shape, clip = false)
            .clip(shape)
            .background(background)
            .border(1.dp, GlassStroke, shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (accent) TextOnAccent else TextPrimary,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun GlassActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    danger: Boolean = false
) {
    val shape = RoundedCornerShape(18.dp)
    val brush = when {
        danger -> Brush.linearGradient(listOf(DeepBordeaux, DarkBordeaux))
        accent -> Brush.linearGradient(listOf(CopperGoldBrown, PaleRoseBordeaux))
        else -> Brush.linearGradient(listOf(DarkBordeaux.copy(alpha = 0.92f), WarmGrayBrown.copy(alpha = 0.46f)))
    }

    Row(
        modifier = modifier
            .height(48.dp)
            .shadow(14.dp, shape, clip = false)
            .clip(shape)
            .background(brush)
            .border(1.dp, GlassStroke, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (accent) TextOnAccent else TextPrimary,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            color = if (accent) TextOnAccent else TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun LocalProfileImage(
    path: String?,
    placeholderText: String,
    contentScale: ContentScale = ContentScale.Crop,
    modifier: Modifier = Modifier
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = path) {
        value = withContext(Dispatchers.IO) {
            path?.let { decodeBitmap(it) }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!,
            contentDescription = null,
            contentScale = contentScale,
            modifier = modifier
        )
    } else {
        ProfileImagePlaceholder(
            text = placeholderText,
            modifier = modifier
        )
    }
}

@Composable
private fun ProfileImagePlaceholder(text: String, modifier: Modifier = Modifier) {
    val initial = text.trim().firstOrNull()?.uppercaseChar()?.toString() ?: "+"
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colors = listOf(DarkBordeaux, DeepBordeaux, CharcoalBlack),
                start = Offset.Zero,
                end = Offset(900f, 1200f)
            )
        ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial,
            color = CopperGoldBrown.copy(alpha = 0.92f),
            fontSize = 44.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun Modifier.glass(shape: Shape): Modifier {
    return this
        .shadow(18.dp, shape, clip = false)
        .clip(shape)
        .background(GlassFill)
        .border(1.dp, GlassStroke, shape)
}

private fun appBackgroundBrush(): Brush {
    return Brush.verticalGradient(
        colors = listOf(
            CharcoalBlack,
            DarkBordeaux,
            DeepBordeaux,
            WarmGrayBrown,
            CharcoalBlack
        )
    )
}

private fun formatRating(value: Float): String {
    return String.format(Locale.US, "%.1f", value.coerceIn(0f, 5f))
}

private fun decodeBitmap(path: String, maxSize: Int = 1400): ImageBitmap? {
    val bounds = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
    }
    BitmapFactory.decodeFile(path, bounds)

    var sampleSize = 1
    while (
        bounds.outWidth / sampleSize > maxSize ||
        bounds.outHeight / sampleSize > maxSize
    ) {
        sampleSize *= 2
    }

    val options = BitmapFactory.Options().apply {
        inSampleSize = sampleSize.coerceAtLeast(1)
    }

    return BitmapFactory.decodeFile(path, options)?.asImageBitmap()
}
