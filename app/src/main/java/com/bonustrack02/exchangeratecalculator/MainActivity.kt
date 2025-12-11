package com.bonustrack02.exchangeratecalculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bonustrack02.exchangeratecalculator.network.ExchangeRateService
import com.bonustrack02.exchangeratecalculator.network.RetrofitObject
import com.bonustrack02.exchangeratecalculator.ui.theme.ExchangeRateCalculatorTheme
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val SOURCE_CURRENCY = "USD"

data class CountryOption(
    @StringRes val nameResId: Int,
    @StringRes val currencyResId: Int,
    @StringRes val fullStringResId: Int,
    val currencyCode: String
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExchangeRateCalculatorTheme {
                ExchangeRateCalculatorScreen()
            }
        }
    }
}

@Composable
fun ExchangeRateCalculatorScreen(modifier: Modifier = Modifier) {
    var remittanceAmount by remember { mutableStateOf("") }
    var displayedReceivedAmount by remember { mutableStateOf(0.0) }
    var currentExchangeRate by remember { mutableStateOf(0.0) }
    var lastUpdateTime by remember { mutableStateOf("N/A") }
    var showErrorDialog by remember { mutableStateOf(false) }

    val exchangeRateService = remember { RetrofitObject.getInstance().create(ExchangeRateService::class.java) }

    val countryOptions = listOf(
        CountryOption(R.string.recipient_country_korea_name, R.string.recipient_country_korea_currency, R.string.recipient_country_korea, "KRW"),
        CountryOption(R.string.recipient_country_japan_name, R.string.recipient_country_japan_currency, R.string.recipient_country_japan, "JPY"),
        CountryOption(R.string.recipient_country_philippines_name, R.string.recipient_country_philippines_currency, R.string.recipient_country_philippines, "PHP")
    )
    var selectedCountry by remember { mutableStateOf(countryOptions[0]) }

    LaunchedEffect(selectedCountry) {
        launch {
            try {
                val response = exchangeRateService.getExchangeRates(
                    currencies = selectedCountry.currencyCode,
                    source = SOURCE_CURRENCY
                )
                if (response.isSuccessful) {
                    val exchangeRateResponse = response.body()
                    exchangeRateResponse?.let {
                        val rate = it.quotes[SOURCE_CURRENCY + selectedCountry.currencyCode]
                        if (rate != null) {
                            currentExchangeRate = rate
                            // Recalculate displayed amount if user already entered remittance
                            if (remittanceAmount.isNotEmpty()) {
                                val amount = remittanceAmount.toDoubleOrNull() ?: 0.0
                                displayedReceivedAmount = amount * currentExchangeRate
                            }
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                            lastUpdateTime = sdf.format(Date(it.timestamp * 1000))
                        } else {
                            Log.e("ExchangeRate", "Exchange rate not found for ${selectedCountry.currencyCode}")
                            currentExchangeRate = 0.0
                            lastUpdateTime = "Error"
                        }
                    }
                } else {
                    Log.e("ExchangeRate", "API Error: ${response.code()} - ${response.message()}")
                    currentExchangeRate = 0.0
                    lastUpdateTime = "Error"
                }
            } catch (e: Exception) {
                Log.e("ExchangeRate", "Network Error: ${e.localizedMessage}")
                currentExchangeRate = 0.0
                lastUpdateTime = "Error"
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val radioGroupItemContentWidth = 150.dp

                countryOptions.forEach { countryOption ->
                    Row(
                        modifier = Modifier
                            .width(radioGroupItemContentWidth)
                            .clickable { selectedCountry = countryOption }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        RadioButton(
                            selected = (selectedCountry == countryOption),
                            onClick = { selectedCountry = countryOption }
                        )
                        Text(
                            text = stringResource(id = countryOption.nameResId),
                        )
                        Text(text = stringResource(id = countryOption.currencyResId))
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(text = stringResource(R.string.exchange_rate_calculation), fontSize = 32.sp)

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.width(300.dp)
            ) {
                InfoRow(
                    label = stringResource(R.string.remittance_country),
                    value = stringResource(R.string.remittance_country_value)
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = stringResource(R.string.recipient_country),
                    value = stringResource(selectedCountry.fullStringResId)
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = stringResource(R.string.exchange_rate),
                    value = stringResource(
                        R.string.exchange_rate_value,
                        currentExchangeRate,
                        selectedCountry.currencyCode,
                        SOURCE_CURRENCY
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                InfoRow(
                    label = stringResource(R.string.inquiry_time),
                    value = lastUpdateTime
                )
                Spacer(modifier = Modifier.height(16.dp))
                RemittanceAmountRow(
                    remittanceAmount = remittanceAmount,
                    onValueChange = { remittanceAmount = it })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.received_amount, displayedReceivedAmount, selectedCountry.currencyCode),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                val amount = remittanceAmount.toDoubleOrNull() ?: -1.0
                if (amount <= 0 || amount > 10000) {
                    showErrorDialog = true
                } else {
                    displayedReceivedAmount = amount * currentExchangeRate
                }
            }) {
                Text(text = stringResource(id = R.string.calculate))
            }

            if (showErrorDialog) {
                AlertDialog(
                    onDismissRequest = { showErrorDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showErrorDialog = false }) {
                            Text("확인")
                        }
                    },
                    title = { Text("오류") },
                    text = { Text("송금액이 바르지 않습니다") }
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row {
        Text(label, modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
        Text(stringResource(id = R.string.colon))
        Text(value)
    }
}

@Composable
fun RemittanceAmountRow(remittanceAmount: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.remittance_amount),
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.End
        )
        Text(stringResource(id = R.string.colon))
        OutlinedTextField(
            value = remittanceAmount,
            onValueChange = onValueChange,
            modifier = Modifier.width(100.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.remittance_unit))
    }
}

@Preview(showBackground = true)
@Composable
fun ExchangeRateCalculatorScreenPreview() {
    ExchangeRateCalculatorTheme {
        ExchangeRateCalculatorScreen()
    }
}
