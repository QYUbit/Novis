package com.qyub.novis.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.qyub.novis.ui.theme.MirroredIcon
import kotlinx.coroutines.launch

@Composable
fun Carousel() {
    // List of items for the carousel
    val items = listOf("Page 1", "Page 2", "Page 3")
    val coroutineScope = rememberCoroutineScope()

    val pagerState = rememberPagerState(pageCount = { items.size })

    Column {
        // Carousel
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) { page ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(
                        color = when (page) {
                            0 -> Color.Red
                            1 -> Color.Green
                            else -> Color.Blue
                        },
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                Text(
                    text = items[page],
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        //HorizontalPagerIndicator(
        //    pagerState = pagerState,
        //    modifier = Modifier
        //        .align(Alignment.CenterHorizontally)
        //        .padding(16.dp),
        //    activeColor = Color.Blue
        //)

        // Buttons to Navigate
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            IconButton (
                onClick = {
                    coroutineScope.launch {
                        val previousPage = (pagerState.currentPage - 1 + items.size) % items.size
                        pagerState.animateScrollToPage(previousPage)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier.size(96.dp)
            ) {
                Icon(
                    imageVector = MirroredIcon.KeyboardArrowLeft,
                    contentDescription = null,
                    tint = LocalContentColor.current,
                    modifier = Modifier.size(72.dp)
                )
            }
            IconButton(
                onClick = {
                    coroutineScope.launch {
                        val previousPage = (pagerState.currentPage + 1) % items.size
                        pagerState.animateScrollToPage(previousPage)
                    }
                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White
                ),
                modifier = Modifier.size(96.dp)
            ) {
                Icon(
                    imageVector = MirroredIcon.KeyboardArrowRight,
                    contentDescription = null,
                    tint = LocalContentColor.current,
                    modifier = Modifier.size(72.dp)
                )
            }
        }
    }
}
