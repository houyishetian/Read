package com.lin.read.filter.search

import java.io.Serializable

data class SearchWebBean @JvmOverloads constructor(val webName: String, val tag: String, val canDownload: Boolean = false, var default: Boolean = false) : Serializable {
    var checked: Boolean? = null
        get() = if (field == null) default else field
}