package com.zhuwenhao.flipped.bandwagon.entity

import com.google.gson.annotations.SerializedName

data class BandwagonInfo(@SerializedName("vm_type") val vmType: String,
                         @SerializedName("ve_status") val veStatus: String,
                         @SerializedName("vz_status") val vzStatus: VZStatus,
                         @SerializedName("vz_quota") val vzQuota: VZQuota,
                         @SerializedName("ssh_port") val sshPort: String,
                         @SerializedName("load_average") val loadAverage: String,
                         val hostname: String,
                         @SerializedName("node_location") val nodeLocation: String,
                         @SerializedName("plan_monthly_data") val planMonthlyData: Long,
                         @SerializedName("monthly_data_multiplier") val monthlyDataMultiplier: Double,
                         @SerializedName("plan_disk") val planDisk: Long,
                         @SerializedName("plan_ram") val planRam: Long,
                         @SerializedName("plan_swap") val planSwap: Long,
                         val os: String,
                         @SerializedName("data_counter") val dataCounter: Long,
                         @SerializedName("data_next_reset") val dataNextReset: Long,
                         @SerializedName("ip_addresses") val ipAddresses: List<String>,
                         @SerializedName("mem_available_kb") val memAvailableKB: Long,
                         @SerializedName("swap_total_kb") val swapTotalKB: Long,
                         @SerializedName("swap_available_kb") val swapAvailableKB: Long,
                         @SerializedName("ve_used_disk_space_b") val veUsedDiskSpaceB: Long,
                         val error: Int) {

    val status: String
        get() = if (vmType == "kvm") veStatus else vzStatus.status

    val usedRam: Long
        get() = if (vmType == "kvm") {
            planRam - memAvailableKB * 1024
        } else {
            if (vzStatus.oomguarPages == "-")
                vzStatus.oomguarPages = "0"
            vzStatus.oomguarPages.toLong() * 4 * 1024
        }

    val totalRam: Long
        get() = planRam

    val ramPercent: Int
        get() = (usedRam / totalRam.toDouble() * 100).toInt()

    val usedSwap: Long
        get() = if (vmType == "kvm") {
            swapTotalKB * 1024 - swapAvailableKB * 1024
        } else {
            if (vzStatus.swapPages == "-")
                vzStatus.swapPages = "0"
            vzStatus.swapPages.toLong() * 4 * 1024
        }

    val totalSwap: Long
        get() = if (vmType == "kvm") swapTotalKB * 1024 else planSwap

    val swapPercent: Int
        get() = (usedSwap / totalSwap.toDouble() * 100).toInt()

    val usedDisk: Long
        get() = if (vmType == "kvm") veUsedDiskSpaceB else vzQuota.occupiedKB * 1024

    val totalDisk: Long
        get() = planDisk

    val diskPercent: Int
        get() = (usedDisk / totalDisk.toDouble() * 100).toInt()

    val usedData: Long
        get() = (dataCounter * monthlyDataMultiplier).toLong()

    val totalData: Long
        get() = (planMonthlyData * monthlyDataMultiplier).toLong()

    val dataPercent: Int
        get() = (usedData / totalData.toDouble() * 100).toInt()

    data class VZStatus(val status: String,
                        @SerializedName("load_average") val loadAverage: String,
                        @SerializedName("nproc") val npRoc: String,
                        @SerializedName("oomguarpages") var oomguarPages: String,
                        @SerializedName("swappages") var swapPages: String)

    data class VZQuota(@SerializedName("occupied_kb") val occupiedKB: Long)
}