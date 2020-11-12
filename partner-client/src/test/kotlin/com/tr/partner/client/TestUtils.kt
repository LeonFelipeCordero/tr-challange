package com.tr.partner.client

import java.nio.file.Files
import java.nio.file.Paths

object TestUtils {

    fun fileToString(filePath: String): String =
            Files.readString(Paths.get(this.javaClass.getResource(filePath).toURI()))
}
