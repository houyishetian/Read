package com.lin.read.utils

import com.lin.read.filter.scan.VarPair
import com.lin.read.filter.scan.toPair

class TempUtil {
    companion object {
        fun <T> parseListElelemtToVarPair(list: List<T>): List<VarPair<T, Boolean>> = list.map { it toPair false }
    }
}