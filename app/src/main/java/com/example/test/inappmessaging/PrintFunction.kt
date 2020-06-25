package com.example.test.inappmessaging

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.ConnectivityManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiEnterpriseConfig
import android.net.wifi.WifiManager
import android.os.AsyncTask
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Log
import androidx.ads.identifier.AdvertisingIdClient
import androidx.ads.identifier.AdvertisingIdInfo
import androidx.annotation.RequiresApi
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.common.util.concurrent.FutureCallback
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.MoreExecutors
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class PrintFunction {

    private val TAG = PrintFunction::class.java.simpleName

    private var context: Context

    private var wifi: WifiManager? = null

    constructor(context: Context) {
        this.context = context
    }

    /*TelephonyManager을 이용해 얻을 수 있는 데이터*/
    /*READ_PHONE_STATE 퍼미션 획득 해야만 사용 가능한 데이터
    * SIM 카드 시리얼넘버
    * IMEI
    * 전화번호
    * 소프트웨어 버전넘버
    * !팝업을 이용한 퍼미션 권환 획득이후 수집 가능
    * */
    @SuppressLint("MissingPermission")
    fun printTelephoneInfo() {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        Log.d(TAG, "음성통화 상태 : [ getCallState ] >>> " + tm.callState)
        Log.d(TAG, "데이터통신 상태 : [ getDataState ] >>> " + tm.dataState)
        Log.d(TAG, "통신사 ISO 국가코드 : [ getNetworkCountryIso ] >>> " + tm.networkCountryIso)
        Log.d(TAG, "통신사 ISO 국가코드 : [ getSimCountryIso ] >>> " + tm.simCountryIso)
        Log.d(TAG, "망사업자 MCC+MNC : [ getNetworkOperator ] >>> " + tm.networkOperator)
        Log.d(TAG, "망사업자 MCC+MNC : [ getSimOperator ] >>> " + tm.simOperator)
        Log.d(TAG, "망사업자명 : [ getNetworkOperatorName ] >>> " + tm.networkOperatorName)
        Log.d(TAG, "망사업자명 : [ getSimOperatorName ] >>> " + tm.simOperatorName)
        Log.d(TAG, "SIM 카드 상태 : [ getSimState ] >>> " + tm.simState)

//        Log.d(TAG, "SIM 카드 시리얼넘버 : [ getSimSerialNumber ] >>> " + tm.simSerialNumber)
//        Log.d(TAG, "IMEI : [ getDeviceId ] >>>" + tm.deviceId)
//        Log.d(TAG, "전화번호 : [ getLine1Number ] >>> " + tm.line1Number)
//        Log.d(TAG, "소프트웨어 버전넘버 : [ getDeviceSoftwareVersion ] >>> " + tm.deviceSoftwareVersion)

        // 유니크한 단말 번호 >>> Android ID 사용
        val android_id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        Log.d(TAG, "Android_ID >>> $android_id")
    }

    /*광고id 획득*/
    fun getGoogleAdvertisingId() {
//        val result = GoogleAdvertisingIdTask().execute().get()
//        val result = GoogleAdvertisingIdTask().execute().get()
//        Log.d(TAG, "adid >>> $result")

        determineAdvertisingInfo()
    }

    fun printWifiInfo() {

        wifi = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        var filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)

        context.registerReceiver(mScanReceiver, filter)

        wifi?.let {
            var config = it.connectionInfo

            if(config != null) {
                val networkId = config.networkId
                val ssid = config.ssid

                Log.d(TAG, "\nbssid: ${config.bssid}")
//                Log.d(TAG, "\nbssid: ${config.frequency}")
                Log.d(TAG, "\nhiddenSSID: ${config.hiddenSSID}")
                Log.d(TAG, "\nipAddress: ${config.ipAddress}")
                Log.d(TAG, "\nlinkSpeed: ${config.linkSpeed}")
                Log.d(TAG, "\nmacAddress: ${config.macAddress}")
                Log.d(TAG, "\nrssi: ${config.rssi}")

                Log.d(TAG, "Network ID: $networkId SSID: $ssid")
            }

            it.startScan()
        }
    }

    fun printInstallationInfo() {
        val pm = context.packageManager
        val it = Intent(Intent.ACTION_MAIN, null)
        it.addCategory(Intent.CATEGORY_LAUNCHER)
        var apps = pm.queryIntentActivities(it, PackageManager.GET_META_DATA)

        for(i in 0 until apps.size) {
            Log.d(TAG, "packageName : ${apps[i].activityInfo.applicationInfo.packageName}, installTime : ${getInstallTime(apps[i].activityInfo.applicationInfo.packageName)}")
        }
    }

    private fun getInstallTime(packageName: String): String {
        var sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        var date = Date(context.packageManager.getPackageInfo(packageName, 0).firstInstallTime)
        return sdf.format(date)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun printNetworkState() {
        val connMgr = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var isWifiConn: Boolean = false
        var isMobileConn: Boolean = false

        var isBluetoothConn: Boolean = false
        var isDummyConn: Boolean = false
        var isEthernetConn: Boolean = false
        var isMobileDunConn: Boolean = false
        var isVPNConn: Boolean = false
        var isWiMaxConn: Boolean = false
        connMgr.allNetworks.forEach { network ->
            connMgr.getNetworkInfo(network).apply {
                when(type) {
                    ConnectivityManager.TYPE_WIFI -> { isWifiConn = isConnected }
                    ConnectivityManager.TYPE_MOBILE -> { isMobileConn = isConnected }
                    ConnectivityManager.TYPE_BLUETOOTH -> { isBluetoothConn = isConnected }
                    ConnectivityManager.TYPE_DUMMY -> { isDummyConn = isConnected }
                    ConnectivityManager.TYPE_ETHERNET -> { isEthernetConn = isConnected }
                    ConnectivityManager.TYPE_MOBILE_DUN -> { isMobileDunConn = isConnected }
                    ConnectivityManager.TYPE_VPN -> { isVPNConn = isConnected }
                    ConnectivityManager.TYPE_WIMAX -> { isWiMaxConn = isConnected }
                }
            }
        }

        Log.d(TAG, "Wifi connected: $isWifiConn")
        Log.d(TAG, "Mobile connected: $isMobileConn")
        Log.d(TAG, "Bluetooth connected: $isBluetoothConn")
        Log.d(TAG, "Dummy connected: $isDummyConn")
        Log.d(TAG, "Ethernet connected: $isEthernetConn")
        Log.d(TAG, "Mobile Dun connected: $isMobileDunConn")
        Log.d(TAG, "VPN connected: $isVPNConn")
        Log.d(TAG, "WiMax connected: $isWiMaxConn")
    }

    private var mScanReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                val results = wifi?.scanResults
                Log.i(TAG, "${results?.size} network found")

                results?.let {
                    for (result in results) {
                        Log.i(TAG, "SSID:${result.SSID} BSSID:${result.BSSID}")
                    }
                }
            } else if(action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                context?.sendBroadcast(Intent("wifi.ON_NETWORK_STATE_CHANGED"))
            }
        }
    }

    fun determineAdvertisingInfo() {

        if (AdvertisingIdClient.isAdvertisingIdProviderAvailable(context)) {
            val advertisingIdInfoListenableFuture = AdvertisingIdClient.getAdvertisingIdInfo(context)

            Futures.addCallback(advertisingIdInfoListenableFuture,
                object : FutureCallback<AdvertisingIdInfo> {
                    override fun onSuccess(adInfo: AdvertisingIdInfo?) {

                        adInfo?.let {
                            val id = it.id
                            val providerPackageName = it.providerPackageName
                            val isLimitTrackingEnabled = it.isLimitAdTrackingEnabled

                            Log.d(TAG, "id : $id")
                            Log.d(TAG, "providerPackageName : $providerPackageName")
                            Log.d(TAG, "isLimitTrackingEnabled : $isLimitTrackingEnabled")
                        }
                    }

                    override fun onFailure(t: Throwable) {
                        Log.e(TAG, "Failed to connect to Advertising ID provider.")
                        // Try to connect to the Advertising ID provider again, or fall
                        // back to an ads solution that doesn't require using the
                        // Advertising ID library.
                    }
                }, MoreExecutors.directExecutor())
        } else {
            // The Advertising ID client library is unavailable. Use a different
            // library to perform any required ads use cases.
        }
    }

    private inner class GoogleAdvertisingIdTask : AsyncTask<Void, Void, String>() {
        override fun doInBackground(vararg params: Void?): String {
            var adId: String = ""
            try {
                adId = AdvertisingIdClient.getAdvertisingIdInfo(context.applicationContext).get().id
                Log.d(TAG,"adid : $adId")
            } catch (ex: IllegalStateException) {
                ex.printStackTrace()
                Log.e(TAG,"IllegalStateException")
            } catch (ex: GooglePlayServicesRepairableException) {
                ex.printStackTrace()
                Log.e(TAG,"GooglePlayServicesRepairableException")
            } catch (ex: IOException) {
                ex.printStackTrace()
                Log.e(TAG,"IOException")
            } catch (ex: GooglePlayServicesNotAvailableException) {
                ex.printStackTrace()
                Log.e(TAG,"GooglePlayServicesNotAvailableException")
            }
            return adId
        }
    }
}