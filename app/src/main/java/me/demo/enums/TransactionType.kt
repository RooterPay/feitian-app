package me.demo.enums

import android.content.Context
import me.dvabi.terminal.R

enum class TransactionType(private val titleResId: Int, private val code: Int) {
    PURCHASE(R.string.purchase, 0),
    CASH(R.string.cash, 1),
    CASHBACK(R.string.cashback, 9),
    REFUND(R.string.refund, 20);

    fun getTitle(context: Context): String = context.getString(titleResId)
    fun getCode() = code

}