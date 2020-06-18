package com.example

import pl.treksoft.kvision.annotations.KVService

@KVService
interface IOshiService {
    suspend fun getSystemInfoModel() : SystemInfoModel
}
