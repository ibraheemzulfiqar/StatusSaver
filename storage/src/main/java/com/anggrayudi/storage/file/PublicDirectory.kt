package com.anggrayudi.storage.file

import android.os.Environment
import java.io.File


enum class PublicDirectory(val folderName: String) {


    DOWNLOADS(Environment.DIRECTORY_DOWNLOADS),


    MUSIC(Environment.DIRECTORY_MUSIC),


    PODCASTS(Environment.DIRECTORY_PODCASTS),


    RINGTONES(Environment.DIRECTORY_RINGTONES),


    ALARMS(Environment.DIRECTORY_ALARMS),


    NOTIFICATIONS(Environment.DIRECTORY_NOTIFICATIONS),


    PICTURES(Environment.DIRECTORY_PICTURES),


    MOVIES(Environment.DIRECTORY_MOVIES),


    DCIM(Environment.DIRECTORY_DCIM),


    DOCUMENTS(Environment.DIRECTORY_DOCUMENTS);

    val file: File
        get() = Environment.getExternalStoragePublicDirectory(folderName)

    val absolutePath: String
        get() = file.absolutePath
}