package com.iamkatrechko.clipboardmanager.domain.util

import android.database.Cursor

/** Является ли курсор null, либо пустым */
fun Cursor?.isNullOrEmpty(): Boolean =
        this == null || this.count == 0
