package com.discut.manga.data

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


fun String.generateHashKey(): String {
    val cacheKey = try {
        val mDigest = MessageDigest.getInstance("MD5")
        mDigest.update(toByteArray())
        bytesToHexString(mDigest.digest())
    } catch (e: NoSuchAlgorithmException) {
        hashCode().toString()
    }
    return cacheKey
}

private fun bytesToHexString(bytes: ByteArray): String {
    val sb = StringBuilder()
    for (i in bytes.indices) {
        val hex = Integer.toHexString(0xFF and bytes[i].toInt())
        if (hex.length == 1) {
            sb.append('0')
        }
        sb.append(hex)
    }
    return sb.toString()
}
