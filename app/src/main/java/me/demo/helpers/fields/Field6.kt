package me.demo.helpers.fields

import me.demo.helpers.subfields.SubFieldO
import me.demo.helpers.subfields.SubFieldP

data class Field6(
    val subFieldE: String,
    val subFieldI: String,
    val subFieldO: SubFieldO,
    val subFieldP: SubFieldP,
) {
    override fun toString(): String {
        val temp = "6$subFieldE$subFieldI$subFieldO$subFieldP"
        return temp
    }
}