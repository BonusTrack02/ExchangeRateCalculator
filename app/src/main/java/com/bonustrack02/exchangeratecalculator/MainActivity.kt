package com.bonustrack02.exchangeratecalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.bonustrack02.exchangeratecalculator.ui.theme.ExchangeRateCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExchangeRateCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ExchangeRateCalculatorScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeRateCalculatorScreen(modifier: Modifier = Modifier) {
    var remittanceAmount by remember { mutableStateOf("100") }
    val receivedAmount = (remittanceAmount.toDoubleOrNull() ?: 0.0) * 1130.05

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = stringResource(R.string.exchange_rate_calculation), fontSize = 32.sp)

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.width(300.dp)
        ) {
            InfoRow(label = stringResource(R.string.remittance_country), value = stringResource(R.string.remittance_country_value))
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = stringResource(R.string.recipient_country), value = stringResource(R.string.recipient_country_value))
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = stringResource(R.string.exchange_rate), value = stringResource(R.string.exchange_rate_value))
            Spacer(modifier = Modifier.height(8.dp))
            InfoRow(label = stringResource(R.string.inquiry_time), value = stringResource(R.string.inquiry_time_value))
            Spacer(modifier = Modifier.height(16.dp))
            RemittanceAmountRow(remittanceAmount = remittanceAmount, onValueChange = { remittanceAmount = it })
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.received_amount, receivedAmount),
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { /*TODO*/ }) {
            Text(text = stringResource(id = R.string.calculate))
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
        Text(stringResource(R.string.remittance_amount), modifier = Modifier.width(80.dp), textAlign = TextAlign.End)
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
