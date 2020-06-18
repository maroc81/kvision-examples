@file:ContextualSerialization(LocalDateTime::class)

package com.example


import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.types.LocalDateTime

/**
 * System info model class to store a subset of data provided by
 * OSHI operating system and hardware abstraction layer interfaces
 */
@Serializable
data class SystemInfoModel(
        val hostName: String? = "",
        val os: String? = "",
        val bootTime: Long? = 0,
        val bootDateTime: LocalDateTime? = null,
        val uptime: Long? = 0,
        val upTimeString: String? = "",
        val manufacturer: String ? = "",
        val model: String? = "",
        val serial: String? = "",
        val cpu: String? = "",
        val memoryTotal: Long? = 0,
        val memoryAvailable: Long? = 0,
        val cpuLoad: Double? = 0.0,
        val cpuProcLoad: List<Double>? = listOf(),
        val cpuLoadAvg0: Double? = 0.0,
        val cpuLoadAvg1: Double? = 0.0,
        val cpuLoadAvg2: Double? = 0.0,
        val diskSummary: List<DiskSummaryModel>? = listOf()
)

/**
 * Disk summary model taken from OSHI file store
 */
@Serializable
data class DiskSummaryModel(
        val name: String? = "",
        val mount: String? = "",
        val size: Long? = 0,
        val free: Long? = 0
)