package com.skygames.goodpang.ui.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skygames.goodpang.R
import com.skygames.goodpang.ui.theme.SplashBackground
import com.skygames.goodpang.ui.theme.SplashCopyright
import com.skygames.goodpang.ui.theme.SplashTagline
import com.skygames.goodpang.ui.theme.SplashWordmark

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SplashBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(3.6f))

        Image(
            painter = painterResource(R.drawable.img_splash_box),
            contentDescription = null,
            modifier = Modifier.width(160.dp)
        )
        Text(
            text = stringResource(R.string.splash_app_name),
            color = SplashWordmark,
            fontSize = 34.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = stringResource(R.string.splash_tagline),
            color = SplashTagline,
            fontSize = 15.sp,
            modifier = Modifier.padding(top = 12.dp)
        )

        Spacer(modifier = Modifier.weight(2.3f))

        Image(
            painter = painterResource(R.drawable.img_splash_illustration),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        )
        Text(
            text = stringResource(R.string.splash_copyright),
            color = SplashCopyright,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 12.dp, bottom = 28.dp)
        )

        Spacer(modifier = Modifier.weight(0.6f))
    }
}
