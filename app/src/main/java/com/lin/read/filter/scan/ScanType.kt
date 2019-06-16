package com.lin.read.filter.scan

import android.text.InputFilter
import com.lin.read.utils.ScanInputTypeEnum
import java.io.Serializable

data class ScanTypeInfo(var text: String, var checked: Boolean, var id: String?) : Serializable
data class ScanInputInfo @JvmOverloads constructor(var tag: String, var hint: String?, var unit: String, var inputType: ScanInputTypeEnum = ScanInputTypeEnum.INT, var inputFilters: Array<InputFilter>? = null) : Serializable