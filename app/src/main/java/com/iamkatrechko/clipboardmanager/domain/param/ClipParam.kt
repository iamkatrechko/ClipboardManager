package com.iamkatrechko.clipboardmanager.domain.param

import android.os.Parcel
import android.os.Parcelable
import com.iamkatrechko.clipboardmanager.domain.param.values.OrderType

/**
 * Параметры запроса списка записей
 * @author iamkatrechko
 *         Date: 06.11.2017
 */
data class ClipParam(
        /** Идентификатор категории записей */
        val categoryId: Long? = null,
        /** Тип сортировки */
        val order: OrderType = OrderType.BY_DATE_ASC,
        /** Текст запроса */
        val queryText: String? = null,
        /** Отображение только избранных записей */
        val onlyFav: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readValue(Long::class.java.classLoader) as? Long,
            OrderType.values()[parcel.readInt()],
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(categoryId)
        parcel.writeInt(order.ordinal)
        parcel.writeString(queryText)
        parcel.writeByte(if (onlyFav) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ClipParam> {
        override fun createFromParcel(parcel: Parcel): ClipParam {
            return ClipParam(parcel)
        }

        override fun newArray(size: Int): Array<ClipParam?> {
            return arrayOfNulls(size)
        }
    }
}