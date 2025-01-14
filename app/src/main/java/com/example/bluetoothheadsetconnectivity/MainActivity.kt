package com.example.bluetoothheadsetconnectivity

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothProfile.ServiceListener
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.reflect.Method
import java.util.*
import android.content.DialogInterface
import android.graphics.Color
import android.util.TypedValue
import androidx.appcompat.app.AlertDialog
import android.widget.TextView





class MainActivity : AppCompatActivity(), View.OnClickListener {

    private var isEnabled: Boolean = false
    private var REQUEST_ENABLE_BT = 0
    private var devices: MutableSet<BluetoothDevice>? = null
    private var device: BluetoothDevice? = null
    private var b: IBinder? = null
    private lateinit var a2dp: BluetoothA2dp  //class to connect to an A2dp device
    private lateinit var ia2dp: IBluetoothA2dp
    private lateinit var devicesAdapter: PairedDevicesAdapter
    private var firstBTSettings : Boolean = true
    private lateinit var myactivity : MainActivity

    private var mIsA2dpReady = false

    class MyTimerTask(val myactivity: MainActivity) : TimerTask() {
        override fun run() {
            myactivity.runOnUiThread(java.lang.Runnable {
                val mTextView = myactivity.findViewById<View>(R.id.btnShowPairedDevices);
                myactivity.onClick(mTextView)
            })
        }
    }

    fun setIsA2dpReady(ready: Boolean) {
        mIsA2dpReady = ready
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            requestMultiplePermissions.launch(arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT))
        }
        else{
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }

        setOnClickListener()
        myactivity = this

        // check HL device


    }

    override fun onStart() {
        super.onStart()

        devices = BluetoothAdapter.getDefaultAdapter().bondedDevices

        if (!checkHLdevice() && BluetoothAdapter.getDefaultAdapter().isEnabled ) {
            if( firstBTSettings) {
                    firstBTSettings = false
                val dialog = AlertDialog.Builder(this).setMessage("請配對輔聽器")
                    .setPositiveButton("OK"){
                            dialog, which -> dialog.cancel()
                        val intent = Intent()
                        //   val uri: Uri = Uri.fromParts("package", packageName, null)
                        //   intent.data = uri
                        intent.action = Settings.ACTION_BLUETOOTH_SETTINGS
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 23.0f)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)

                val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
                textView!!.textSize = 23f
            }
            else{
                val dialog = AlertDialog.Builder(this).setMessage("無輔聽產品,APP將結束")
                    .setPositiveButton("OK"){
                    // 此為 Lambda 寫法
                        dialog, which -> dialog.cancel()
                        finishAffinity();
                        System.exit(0);
                    }
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 23.0f)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)

                val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
                textView!!.textSize = 23f
            }
        }
    }

    private var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            //granted
                System.out.println("asdfasdf");
            enableBluetooth()
            if( firstBTSettings)
            if (!checkHLdevice() && BluetoothAdapter.getDefaultAdapter().isEnabled ) {
                firstBTSettings = false
                val dialog = AlertDialog.Builder(this).setMessage("請配對輔聽器")
                    .setPositiveButton("OK"){
                            dialog, which -> dialog.cancel()
                        val intent = Intent()
                        //   val uri: Uri = Uri.fromParts("package", packageName, null)
                        //   intent.data = uri
                        intent.action = Settings.ACTION_BLUETOOTH_SETTINGS
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                        intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    .show()
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 23.0f)
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)

                val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
                textView!!.textSize = 23f

                }
            val task = MyTimerTask(myactivity)
            Timer().schedule(task, Date(), 10000)
        }else{
            //deny
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.d("test006", "${it.key} = ${it.value}")

                enableBluetooth()
                if( firstBTSettings)
                if (!checkHLdevice() && BluetoothAdapter.getDefaultAdapter().isEnabled ) {
System.out.println("asdfasdfasdfasdf")
//                    firstBTSettings = false
                    val dialog = AlertDialog.Builder(this).setMessage("請配對輔聽器")
                        .setPositiveButton("OK"){
                                dialog, which -> dialog.cancel()
                            val intent = Intent()
                            //   val uri: Uri = Uri.fromParts("package", packageName, null)
                            //   intent.data = uri
                            intent.action = Settings.ACTION_BLUETOOTH_SETTINGS
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
                            intent.flags = Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                        }
                        .show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 23.0f)
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.WHITE)

                    val textView = dialog.findViewById<View>(android.R.id.message) as TextView?
                    textView!!.textSize = 23f

                }
                val task = MyTimerTask(myactivity)
                Timer().schedule(task, Date(), 10000)
            }
        }
    private fun setOnClickListener() {
        btnShowPairedDevices.setOnClickListener(this)
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun connectUsingBluetoothA2dp(
        deviceToConnect: BluetoothDevice?
    ) {
        try {
            val c2 = Class.forName("android.os.ServiceManager")
            val m2: Method = c2.getDeclaredMethod("getService", String::class.java)
            b = m2.invoke(c2.newInstance(), "bluetooth_a2dp") as IBinder?
            if (b == null) {
                // For Android 4.2 Above Devices
                device = deviceToConnect
                //establish a connection to the profile proxy object associated with the profile
                val profileProxy = BluetoothAdapter.getDefaultAdapter().getProfileProxy(
                    this,
                    // listener notifies BluetoothProfile clients when they have been connected to or disconnected from the service
                    object : ServiceListener {
                        override fun onServiceDisconnected(profile: Int) {
                            setIsA2dpReady(false)
                            disConnectUsingBluetoothA2dp(device)
                        }

                        override fun onServiceConnected(
                            profile: Int,
                            proxy: BluetoothProfile
                        ) {
                            a2dp = proxy as BluetoothA2dp
                            try {
                                //establishing bluetooth connection with A2DP devices
                                a2dp.javaClass
                                    .getMethod("connect", BluetoothDevice::class.java)
                                    .invoke(a2dp, deviceToConnect)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            setIsA2dpReady(true)
                        }
                    }, BluetoothProfile.A2DP
                )
            } else {
                val c3 =
                    Class.forName("android.bluetooth.IBluetoothA2dp")
                val s2 = c3.declaredClasses
                val c = s2[0]
                val m: Method = c.getDeclaredMethod("asInterface", IBinder::class.java)
                m.isAccessible = true
                ia2dp = m.invoke(null, b) as IBluetoothA2dp
                ia2dp.connect(deviceToConnect)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("PrivateApi", "DiscouragedPrivateApi")
    fun disConnectUsingBluetoothA2dp(
        deviceToConnect: BluetoothDevice?
    ) {
        try {
            // For Android 4.2 Above Devices
          //      System.out.println("asdfasdf"+deviceToConnect.toString());
            if (b == null) {
                try {
                    //disconnecting bluetooth device
                    a2dp.javaClass.getMethod(
                        "disconnect",
                        BluetoothDevice::class.java
                    ).invoke(a2dp, deviceToConnect)
                    BluetoothAdapter.getDefaultAdapter()
                        .closeProfileProxy(BluetoothProfile.A2DP, a2dp)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                ia2dp.disconnect(deviceToConnect)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        disConnectUsingBluetoothA2dp(device)
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    fun checkHLdevice():Boolean{
        devices?.forEach{
            if( checkMacAddressArrange(it.address.toString())){
                return true
            }else if( it.name.toUpperCase().indexOf("RX")!=-1 || it.name.toUpperCase().indexOf("HL")!=-1) {
                return true
            }
        }
        return false
    }

    fun checkMacAddressArrange(sMacAddress:String):Boolean{
//        System.out.println(sMacAddress.replace(":","") +" asdf length: "+sMacAddress.length)
        val a: Long = "26625B1EF".toLong(radix = 16)
        val b: Long = sMacAddress.replace(":","").substring(3).toLong(radix = 16)
        return b>=a && (b-2000)<=a;
    }

    override fun onClick(v: View?) {
        when (v?.id) {

            R.id.btnShowPairedDevices -> {
                //getting paired devices
                devices = BluetoothAdapter.getDefaultAdapter().bondedDevices

                setRecyclerview(
                    this.devices!!,
                    ::connectUsingBluetoothA2dp
                )
            }

        }
    }

    private fun triggerBTSettins(){

    }

    private fun enableBluetooth() {
        //Checking if bluetooth is on or off
        devices = BluetoothAdapter.getDefaultAdapter().bondedDevices
        if (BluetoothAdapter.getDefaultAdapter().isEnabled) {

            isEnabled = true
        }else {
            //turn bluetooth on
            BluetoothAdapter.getDefaultAdapter().enable();
//            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
  //          startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            isEnabled = true
        }


    }

    private fun setRecyclerview(
        devices: MutableSet<BluetoothDevice>,
        connect: (deviceToConnect: BluetoothDevice?) -> Unit
    ) {

        rvPairedDevices.layoutManager = LinearLayoutManager(this)
        devicesAdapter = PairedDevicesAdapter(connect)
        devicesAdapter.addItems(devices)
        //rvPairedDevices.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))

        rvPairedDevices.apply {
            adapter = devicesAdapter
        }

    }
}