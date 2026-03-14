package com.cricut.androidassessment.ext

val Any.LOGGING_TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }