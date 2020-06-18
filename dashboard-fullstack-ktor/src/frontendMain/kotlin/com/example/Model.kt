package com.example

object Model {

    private val oshiService = OshiService()

    suspend fun getSystemInfoModel() = oshiService.getSystemInfoModel()
}
