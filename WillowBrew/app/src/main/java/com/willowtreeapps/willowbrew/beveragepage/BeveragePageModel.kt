package com.willowtreeapps.willowbrew.beveragepage

data class BeveragePageModel(
        val glassDrawable: Int,
        val percent: String,
        val beverageType: String,
        val cardDrawable: Int,
        val name: String,
        val changeDate: String,
        val needsRefill: Boolean,
        val description: String
) {

}