package com.minew.wristbanddemo

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.minew.wristband.ble.bean.WristbandModule
import com.minew.wristband.ble.manager.MinewWristbandManager

class OperationViewModel : ViewModel() {

    private val manager = MinewWristbandManager.getInstance(DemoApp.getInstance())

    val deviceInfoLiveData = MutableLiveData<ReadDeviceInfo>()

    fun readDeviceInfo(module:WristbandModule) {
        val readDeviceInfo = ReadDeviceInfo()
        if (module.firmwareVersionCode <= 2) {
            deviceInfoLiveData.value = readDeviceInfo
            return
        }
        manager.readAlarmGear(module) { gear ->
            readDeviceInfo.gear = gear
            if (module.hasTemperatureSensor()) {
                manager.readTempAlarmValue(module) {alarmValue ->
                    readDeviceInfo.alarmValue = alarmValue
                    manager.readTempeMeasureInterval(module) {interval ->
                        readDeviceInfo.measureInterval = interval
                        deviceInfoLiveData.value = readDeviceInfo
                    }
                }
            } else {
                deviceInfoLiveData.value = readDeviceInfo
            }

        }
    }
}

data class ReadDeviceInfo( var gear:Int?= null, var alarmValue:Float?= null, var measureInterval:Int?= null) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readValue(Int::class.java.classLoader) as? Int) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(gear)
        parcel.writeValue(alarmValue)
        parcel.writeValue(measureInterval)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReadDeviceInfo> {
        override fun createFromParcel(parcel: Parcel): ReadDeviceInfo {
            return ReadDeviceInfo(parcel)
        }

        override fun newArray(size: Int): Array<ReadDeviceInfo?> {
            return arrayOfNulls(size)
        }
    }
}