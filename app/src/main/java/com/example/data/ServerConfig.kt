package com.example.data

object ServerConfig {
    // Hardcoded single server details - cannot be edited anywhere in the app
    const val SERVER_NAME: String = "[SERVER_NAME]"
    const val SERVER_IP: String = "185.189.15.42"
    const val SERVER_PORT: Int = 7777
    const val SERVER_DISPLAY: String = "[SERVER_IP]:[PORT]"
    const val SERVER_REAL_ENDPOINT: String = "185.189.15.42:7777"
    const val FILE_HOST_URL: String = "[FILE_HOST_URL]"
    
    // Total size of the SA-MP + GTA SA mobile data files (~700 MB)
    const val TOTAL_FILE_SIZE_BYTES: Long = 700L * 1024L * 1024L // 700 MB
    const val TOTAL_SIZE_DISPLAY: String = "700MB"
    
    const val SA_MP_VERSION: String = "0.3.7-R3"
    const val WATERMARK_YEAR: String = "2026"
}

