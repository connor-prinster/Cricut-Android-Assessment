package com.cricut.androidassessment.ext

fun String.stripCurlies(): String {
    return this.replace("{", "").replace("}", "")
}