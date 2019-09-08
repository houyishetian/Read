package com.lin.read.filter.scan

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ScanBean(@Expose val webs: LinkedHashMap<String, List<ScanOptionItemBean>>, @Expose val details: Map<String, ScanDetailItemBean>) : Serializable

data class ScanDetailItemBean(@Expose val mainUrl: List<String>, @Expose val rolePathKey: String, @Expose val roleParamsKeys: Map<String, String>, @Expose val use4WordsList: List<String>?,
                              @Expose val hasNoKeys: Map<String, List<String>>?, @Expose val options: LinkedHashMap<String, MutableList<ScanOptionItemBean>>,
                              @Expose val inputs: LinkedHashMap<String, List<ScanInputItemBean>>?) : Serializable

sealed class ScanBaseItemBean : Serializable
data class ScanOptionItemBean(@Expose val id: String?, @Expose val name: String) : ScanBaseItemBean()
data class ScanInputItemBean(@Expose val id: String, @Expose val inputType: String, @Expose val hint: String, @Expose val unitText: String,
                             @Expose val defaultValue: Number = -1, @Expose val min: Number?, @Expose val max: Number?) : ScanBaseItemBean()

data class ScanInputData(val id: String, val value: Number, val min: Number?, val max: Number?) : Serializable
data class ScanDataBean(val webId: String, val webName: String, val mainUrl: List<String>, val rolePathValue: String, val roleParamPairs: Map<String, String?>, val inputDatas: List<ScanInputData>, var page: Int = 1) : Serializable
