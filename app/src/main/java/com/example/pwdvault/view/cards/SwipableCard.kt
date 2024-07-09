package com.example.pwdvault.view.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.SwipeableState
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.pwdvault.R
import com.example.pwdvault.view.cards.Util.isNotNull
import com.example.pwdvault.view.cards.Util.isNull
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalWearMaterialApi::class)
@Preview
@Composable
fun SwipeableCardPreview() {
    SwipeableCard(
        onClick = {},
        onLongClick = {},
        startAction = {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxHeight()
            )
        },
        endAction = {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxHeight()
            )
        },
        content = {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primary)
                    .fillMaxHeight()
            )
        }
    )
}

private enum class SwipeableType {
    Start,
    End,
    Both;

    fun anchors(sizePx: Float): Map<Float, Int> = when (this) {
        Start -> mapOf(0f to 0, sizePx to 1)
        End -> mapOf(0f to 0, -sizePx to 1)
        Both -> mapOf(0f to 0, -sizePx to 1, sizePx to -1)
    }
}

private object Util {
    fun Any?.isNull() = this == null
    fun Any?.isNotNull() = !this.isNull()
}


@OptIn(ExperimentalWearMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun SwipeableCard(
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    swipeableState: SwipeableState<Int> = rememberSwipeableState(0),
    startAction: @Composable (BoxScope.() -> Unit)? = null,
    endAction: @Composable (BoxScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val sizePx = with(LocalDensity.current) { dimensionResource(id = R.dimen.swipeable_card_size).toPx() }
    val coroutineScope = rememberCoroutineScope()
    val type = when {
        startAction.isNotNull() && endAction.isNull() -> SwipeableType.Start
        startAction.isNull() && endAction.isNotNull() -> SwipeableType.End
        startAction.isNotNull() && endAction.isNotNull() -> SwipeableType.Both
        else -> null
    }
    Box(
        modifier = Modifier
            .let {
                when (type) {
                    null -> it
                    else -> it.swipeable(
                        state = swipeableState,
                        anchors = type.anchors(sizePx),
                        thresholds = { _, _ ->
                            FractionalThreshold(
                                fraction = 0.3f
                            )
                        },
                        orientation = Orientation.Horizontal
                    )
                }
            }
            .height(IntrinsicSize.Max)
            .background(
                color = MaterialTheme.colorScheme.secondary,
                shape = shape
            )
            .combinedClickable(
                onClick = {
                    coroutineScope.launch {
                        swipeableState.animateTo(0)
                    }
                    onClick()
                },
                onLongClick = onLongClick,
            )
    ) {
        startAction?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                content = it
            )
        }
        endAction?.let {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd),
                content = it
            )
        }
        Card(
            modifier = modifier
                .fillMaxHeight()
                .offset {
                    IntOffset(swipeableState.offset.value.roundToInt(), 0)
                },
            shape = shape,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background
            ),
            elevation = elevation,
            border = border,
            content = content
        )
    }
}