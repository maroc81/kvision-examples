package com.example

import kotlinx.coroutines.delay
import oshi.SystemInfo
import oshi.hardware.HardwareAbstractionLayer
import oshi.software.os.OperatingSystem
import oshi.util.FormatUtil
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Service to return operating system and hardware information to the front end
 */
actual class OshiService : IOshiService {

    /**
     * Populates a SystemInfoModel with values from the local system
     */
    override suspend fun getSystemInfoModel(): SystemInfoModel {
        val si = SystemInfo()
        val os = si.operatingSystem
        val hal = si.hardware

        var dt: LocalDateTime? = null
        try {
            // System boot time is in epoch in the local time system time zone
            dt = LocalDateTime.ofInstant(Instant.ofEpochSecond(os.systemBootTime), ZoneId.systemDefault())
        } catch (e: Exception) {
        }

        // Current ticks of all cpus
        val prevTicks = hal.processor.systemCpuLoadTicks
        // Current ticks of each individual cpu
        val prevProcTicks = hal.processor.processorCpuLoadTicks
        // Give time for ticks to change
        delay(1000)
        // Use last ticks to calculate load between ticks
        val cpuLoad = hal.processor.getSystemCpuLoadBetweenTicks(prevTicks)
        val loadPerCpu = hal.processor.getProcessorCpuLoadBetweenTicks(prevProcTicks)

        // Get the load average for 1, 5, and 15 minutes
        val loadAvg = hal.processor.getSystemLoadAverage(3)

        return SystemInfoModel(
                hostName = os.networkParams.hostName,
                os = os.toString(),
                bootTime = os.systemBootTime,
                bootDateTime = dt,
                uptime = os.systemUptime,
                upTimeString = FormatUtil.formatElapsedSecs(os.systemUptime),
                manufacturer = hal.computerSystem.manufacturer,
                model = hal.computerSystem.model,
                serial = hal.computerSystem.serialNumber,
                memoryTotal = hal.memory.total,
                memoryAvailable = hal.memory.available,
                cpuLoad = cpuLoad,
                cpuProcLoad = loadPerCpu.toList(),
                cpuLoadAvg0 = loadAvg[0],
                cpuLoadAvg1 = loadAvg[1],
                cpuLoadAvg2 = loadAvg[2],
                diskSummary = getDiskSummary(hal, os)
        )
    }

    /**
     * Creates and returns a list of disk summaries from mounted file systems
     */
    private fun getDiskSummary(hal: HardwareAbstractionLayer, os: OperatingSystem): List<DiskSummaryModel> {
        val disks = hal.diskStores
        val fs = os.fileSystem
        val diskSummary = mutableListOf<DiskSummaryModel>()

        // For now, create disk summary from file stores
        // TODO: map file stores from physical disk information from hal.diskStores
        //       so that we ignore things like loop mounts from snaps on linux
        fs.fileStores.forEach {
            diskSummary.add(DiskSummaryModel(it.label, it.mount, it.totalSpace, it.freeSpace))
        }
        return diskSummary
    }
}
