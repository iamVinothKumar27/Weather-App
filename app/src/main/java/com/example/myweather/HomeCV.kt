package com.example.myweather

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.MotionLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.ExperimentalMotionApi
import com.example.myweather.model.ForecastDay
import com.example.myweather.model.Hour
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.S)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeView(viewModel: MainViewmodel) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )

    val outerGradient = Brush.horizontalGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xFF45278B),
        )
    )
    val angularGradient = Brush.linearGradient(
        colors = listOf(
            Color(0x24612FAB) ,
            Color(0xde612FAB),
            Color(0x5C612FAB),
            Color(0x87612FAB),
        ),
        start = androidx.compose.ui.geometry.Offset(0f, 0f), // Starting point (top-left)
        end = androidx.compose.ui.geometry.Offset(1000f, 1000f) // Ending point (bottom-right, adjust for rotation)
    )
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .wrapContentHeight()
                    .background(outerGradient)
                ){
                Box(
                    modifier = Modifier
                        .clip(
                            RoundedCornerShape(
                                topStart = 250.dp,
                                topEnd = 0.dp,
                                bottomStart = 250.dp,
                                bottomEnd = 0.dp
                            )
                        ) // Clip to semi-circle shape (bottom half)
                        .background(angularGradient, alpha = 0.20f)
                )

                val listState = rememberLazyListState()

Row {
    AnimatedVisibility(
        viewModel.selectedIndex == 0,
        enter = slideInHorizontally(
            initialOffsetX = { -300 },
            animationSpec = tween(
                durationMillis = 400,
                easing = LinearEasing // interpolator
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -50 },
            animationSpec = tween(
                durationMillis = 50,
                easing = LinearEasing
            )
        )
    ) {
        LazyRow(
            state = listState, // Set the LazyListState
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            viewModel.data.value?.forecast?.forecastday?.get(0)?.hour?.forEachIndexed { index, data ->
                item {
                    WeatherItemHour(index, data)
                }
            }
        }
    }

    // LazyRow for Daily Weather
    AnimatedVisibility(
        viewModel.selectedIndex == 1,
        enter = slideInHorizontally(
            initialOffsetX = { -300 }, // small slide 300px
            animationSpec = tween(
                durationMillis = 400,
                easing = LinearEasing // interpolator
            )
        ),
        exit = slideOutHorizontally(
            targetOffsetX = { -100 },
            animationSpec = tween(
                durationMillis = 50,
                easing = LinearEasing
            )
        )

    ) {
        LazyRow(
            state = listState,
            modifier = Modifier.padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            viewModel.data.value?.forecast?.forecastday?.forEachIndexed { index, data ->
                item {
                    WeatherItemDay(index, data)
                }
            }
        }
    }
}
                BottomView(viewModel)
            }
        },
        sheetPeekHeight = 380.dp,
        sheetDragHandle = {
            TabRow(
                selectedTabIndex = (viewModel.selectedIndex ),
                contentColor = Color(0xFF45278B),
                containerColor = Color(0xFF45278B),
                indicator = {tabPositions->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[viewModel.selectedIndex]),
                        color = Color(0xFFc427fb)
                    )
                }
            ) {
                Tab(selected = viewModel.selectedIndex ==0, interactionSource = remember { MutableInteractionSource() },
                    modifier = Modifier.padding(vertical = 16.dp),
                    content = {
                    Text("Hourly foreCast", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }, onClick = {
                    viewModel.selectedIndex =0
                })
                Tab(selected = viewModel.selectedIndex ==1, interactionSource = remember { MutableInteractionSource() }, content = {
                    Text("Weekly foreCast", color = Color.White,  fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }, onClick = {
                   viewModel. selectedIndex =1
                })


            }
        }

        )
    {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Image(
                painter = painterResource(R.drawable.background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )

            Image(
                painter = painterResource(R.drawable.house),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
            val progress = animateFloatAsState(
                targetValue = scaffoldState.bottomSheetState.targetValue.ordinal.toFloat(),
                   animationSpec = tween(500)
              )
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(top = 60.dp),
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.spacedBy(4.dp)
//            ) {
//               val progress = animateFloatAsState(
//                   targetValue = scaffoldState.bottomSheetState.targetValue.ordinal.toFloat(),
//                   animationSpec = tween(500)
//               )
////                Text(
////                    viewModel.city,
////                    color = Color.White,
////                    fontSize = 34.sp
////                )
//                MotionLayoutText(viewModel.city,progress.value)
//
//                Text(
//                    "${viewModel.data.value?.current?.temp_c?.toInt()}°C",
//                    color = Color.White,
//                    fontSize = 60.sp
//                )
//
//                Text(
//                    "${viewModel.data.value?.current?.condition?.text}",
//                    color = colorResource(R.color.dark_secondary),
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//                Text(
//                    "H:${viewModel.data.value?.forecast?.forecastday?.get(0)?.day?.maxtemp_c}°   L:${viewModel.data.value?.forecast?.forecastday?.get(0)?.day?.mintemp_c}°",
//                    color = Color.White,
//                    fontSize = 20.sp,
//                    fontWeight = FontWeight.SemiBold
//                )
//            }
                MotionLayoutText(viewModel, progress = progress.value)


        }
    }
}



@OptIn(ExperimentalMotionApi::class)
@Composable
fun MotionLayoutText(viewModel: MainViewmodel, progress: Float) {
    MotionLayout(
        start = ConstraintSet {
            val city = createRefFor("city")
            val temp = createRefFor("temp")
            val condition = createRefFor("condition")
            val minMax = createRefFor("minMax")

            // City positioned slightly from the top
            constrain(city) {
                    top.linkTo(parent.top, margin = 50.dp) // A bit from the top
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.top)
            }

            // Temp positioned below city
            constrain(temp) {
                top.linkTo(city.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

            // Condition positioned below temp
            constrain(condition) {
                top.linkTo(temp.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

            // MinMax positioned below condition
            constrain(minMax) {
                top.linkTo(condition.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        end = ConstraintSet {
            val city = createRefFor("city")
            val temp = createRefFor("temp")
            val condition = createRefFor("condition")
            val minMax = createRefFor("minMax")

            // City moves towards the top
            constrain(city) {
                top.linkTo(parent.top, margin = 20.dp) // City moves closer to the top
                start.linkTo(parent.start,) // Align to start with margin
                end.linkTo(parent.end,) // Align to end with margin
            // Align to end with margin
            }

            // Temp still positioned below city
            constrain(temp) {
                top.linkTo(city.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

            // Condition positioned below temp
            constrain(condition) {
                top.linkTo(temp.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }

            // MinMax positioned below condition
            constrain(minMax) {
                top.linkTo(condition.bottom, margin = 8.dp)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
        },
        modifier = Modifier.fillMaxSize(),
        progress = progress // Set the initial progress to 0 to start at the "start" constraint set
    )
    {
        Text(
            text = viewModel.city,
            color = Color.White,
            fontSize = 34.sp,
            modifier = Modifier.layoutId("city")
        )
        Text(
            text = "${viewModel.data.value?.current?.temp_c?.toInt()}°C",
            color = Color.White,
            fontSize = 60.sp,
            modifier = Modifier.layoutId("temp")
        )
        Text(
            text = "${viewModel.data.value?.current?.condition?.text}",
            color = colorResource(R.color.dark_secondary),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.layoutId("condition")
        )
        Text(
            text = "H:${viewModel.data.value?.forecast?.forecastday?.get(0)?.day?.maxtemp_c}°   L:${viewModel.data.value?.forecast?.forecastday?.get(0)?.day?.mintemp_c}°",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.layoutId("minMax")
        )
    }
}




@Composable
fun UV(uv: Double?) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )
    Column(
        modifier  = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center

    ){
        Column(modifier = Modifier.weight(0.6f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.darksun),
                    contentDescription = null,
                )
                Text("UV INDEX", color = colorResource(R.color.tertiary))
            }

            var uvMode = ""
            if (uv != null) {
                if(uv <=3) uvMode = "Low"
                else if(uv <=6) uvMode = "Moderate"
                else uvMode = "High"
            }
            Column {
                Text(
                    uv?.toInt().toString(),
                    color = colorResource(R.color.white),
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 10.dp),
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    uvMode,
                    color = colorResource(R.color.white),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 10.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier =Modifier.padding(top = 20.dp).fillMaxWidth().height(5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8A2BE2),
                            Color(0xFFFF69B4)
                        )
                    )),
            )

        }


    }
}


@Composable
fun Sunrise(sunrise: String, sunset: String) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )
    Column(
        modifier  = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ){
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.sunrise),
                    contentDescription = null,
                )
                Text("SUNRISE", color = colorResource(R.color.tertiary))
            }
            Text(sunrise, color = colorResource(R.color.white), fontSize = 18.sp, modifier = Modifier.padding(top =10.dp), fontWeight = FontWeight.SemiBold)

            Box(
                modifier =Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.sun),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
            }
            Text("sunset :${sunset}", color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.padding(top =2.dp))

        }
    }
}

@Composable
fun WeatherItemHour(index:Int, time :Hour) {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.getDefault())
    val outputFormat = SimpleDateFormat("hh:a", Locale.getDefault())
    val rainPossibility = time.will_it_rain
    var painter = 0
    if((index in 0..6) || (index in 19..23) ){// night case
        if(rainPossibility==1){
            painter = R.drawable.night_rain
        }
        else{
            painter = R.drawable.night_no_rain
        }
    }
    else {
        if(rainPossibility ==1) painter = R.drawable.morning_rain
        else painter = R.drawable.morning_no_rain
    }

    val date = inputFormat.parse(time.time)
    val formattedTime = outputFormat.format(date)
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0x3248319d))
            .border(width = 2.dp, shape = RoundedCornerShape(30.dp), color = Color(0x961f1d47))
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        Text(
            formattedTime.uppercase(),
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 10.dp)

        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(painter),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(30.dp),
                contentScale = ContentScale.FillBounds

            )
            val rainPercent = time.chance_of_rain

                Text(
                    if(rainPercent>0f) "${rainPercent}%" else "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = Color(0xff40cbd8),
                    modifier = Modifier.padding(bottom = 10.dp)

                )

        }
        Text(
            "${time.temp_c}°c",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = Color.White

        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeatherItemDay(index:Int, data: ForecastDay) {

    val rainPossibility = data.day.daily_will_it_rain
    var painter = 0
    if((index in 0..6) || (index in 19..23) ){// night case
        if(rainPossibility==1){
            painter = R.drawable.night_rain
        }
        else{
            painter = R.drawable.night_no_rain
        }
    }
    else {
        if(rainPossibility ==1) painter = R.drawable.morning_rain
        else painter = R.drawable.morning_no_rain
    }

    val date = LocalDate.parse(data.date)
    val day = date.dayOfWeek.name.take(3).uppercase()
    Column(
        modifier = Modifier
            .wrapContentWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0x3248319d))
            .border(width = 2.dp, shape = RoundedCornerShape(30.dp), color = Color(0x961f1d47))
            .padding(vertical = 16.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {


        Text(
            day,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(bottom = 10.dp)

        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(painter),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .size(30.dp),
                contentScale = ContentScale.FillBounds

            )
            val rainPercent = data.day.daily_chance_of_rain

                Text(
                    if(rainPercent>0f) "${rainPercent}%" else "",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    color = Color(0xff40cbd8),
                    modifier = Modifier.padding(bottom = 10.dp)

                )

        }
        Text(
            "${data.day.avgtemp_c}°c",
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            color = Color.White

        )
    }
}


@Composable
fun VisibilityItem(visKm: Double?) {

    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )
    Column(
        modifier  = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ){
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.visibility_icon),
                    contentDescription = null,
                )
                Text("VISIBILITY", color = colorResource(R.color.tertiary))
            }
            Text("${visKm?.toInt()} Km", color = colorResource(R.color.white), fontSize = 34.sp)
        }
        Text("Similar to the Actual Temperature", color = colorResource(R.color.white), fontSize = 14.sp)
    }
}
@Composable
fun Wind(windKph: Double?) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )

    Column(
        modifier = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.humidity),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text("WIND", color = colorResource(R.color.tertiary))
        }

        Box (contentAlignment = Alignment.Center){
            Image(
                painter = painterResource(R.drawable.circle_outline),
                contentDescription = null,
                modifier = Modifier
                    .padding(horizontal = 10.dp)
                    .padding(top = 0.dp)
                    .size(110.dp)
            )
            Column{
                Text("${windKph}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,)
                Text("km/h",color = Color.White,fontSize = 12.sp , fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
@Composable
fun Humidity(humidity: Int?, dew_point: Double?) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )
    Column(
        modifier  = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ){
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.humidity),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text("HUMIDITY", color = colorResource(R.color.tertiary),modifier = Modifier.padding(bottom =5.dp))
            }
            Text("${humidity}%", color = colorResource(R.color.white), fontSize = 34.sp)
        }
        Text("The dew point is ${dew_point?.toInt()} right now", color = colorResource(R.color.white), fontSize = 14.sp)
    }
}
@Composable
fun Temperature(feelslikeC: Double?) {
    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )
    Column(
        modifier  = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ){
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.temperature),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text("FEELS LIKE", color = colorResource(R.color.tertiary), modifier = Modifier.padding(bottom = 5.dp))
            }
            Text("${feelslikeC?.toInt()}°C", color = colorResource(R.color.white), fontSize = 34.sp)
        }
        Text("Similar to the Actual Temperature", color = colorResource(R.color.white), fontSize = 14.sp)
    }
}

@Composable
fun RainFall(precipMm: Double?, expectedPrecipMm :Double?) {

    val gradient = Brush.linearGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xff1C1B33)
        )
    )
    Column(
        modifier  = Modifier
            .size(164.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(gradient)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top

    ){
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.rainfall),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text("RAINFALL", color = colorResource(R.color.tertiary))
            }
            Text("${precipMm}mm", color = colorResource(R.color.white), fontSize = 34.sp)
        }
        Text("${expectedPrecipMm}mm expected in next 24h", color = colorResource(R.color.white), fontSize = 14.sp)
    }
}

@Composable
fun BottomView(viewModel: MainViewmodel){
    val gradient = Brush.horizontalGradient(
        listOf(
            Color(0xff2E335A),
            Color(0xFF45278B),
        )
    )
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .background(gradient)
            .padding(32.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item{
            UV(viewModel.data.value?.current?.uv)
        }
        item{
            Sunrise(viewModel.data.value?.forecast?.forecastday?.get(0)?.astro?.sunrise ?:"",
                viewModel.data.value?.forecast?.forecastday?.get(0)?.astro?.sunset?:"", )
        }
        item {
            if(viewModel.data.value?.current?.precipMm ==0.0){
                Wind(viewModel.data.value?.current?.wind_kph)
            }
            else {
                RainFall(
                    viewModel.data.value?.current?.precipMm,
                    viewModel.data.value?.forecast?.forecastday?.get(0)?.day?.totalprecip_mm
                )
            }
        }
        item{
            Temperature(
                viewModel.data.value?.current?.feelslike_c
            )
        }
        item{
            Humidity(
                humidity = viewModel.data.value?.current?.humidity,
                dew_point =  viewModel.data.value?.current?.dewpoint_c
                )
        }
        item{
            VisibilityItem(
                viewModel.data.value?.current?.vis_km
            )
        }
    }
}
