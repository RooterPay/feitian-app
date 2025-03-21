package com.rooterpay.utils

enum class Currency {
    EUR, USD
}

fun currencyFormatter(currency: Currency, amount: String): String {
    if(currency == Currency.EUR) return "$amount €"
    else if(currency == Currency.USD) return "$ $amount"
    else return amount
}
