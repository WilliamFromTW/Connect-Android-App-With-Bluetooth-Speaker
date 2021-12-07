package com.example.bluetoothheadsetconnectivity

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_paired_device_item.view.*
import java.lang.reflect.Method
import androidx.recyclerview.widget.DividerItemDecoration




class PairedDevicesAdapter(private val connect: (deviceToConnect: BluetoothDevice?) -> Unit) : RecyclerView.Adapter<PairedDevicesAdapter.DeviceViewHolder>() {

    private var devicesList: ArrayList<BluetoothDevice> = ArrayList()

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_paired_device_item, parent, false)
        return DeviceViewHolder(view)
    }

    override fun getItemCount(): Int {
        return devicesList.size

    }
    fun isConnected(device: BluetoothDevice): Boolean {

        return try {
            val m: Method = device.javaClass.getMethod("isConnected")
            m.invoke(device) as Boolean
        } catch (e: Exception) {
            throw IllegalStateException(e)
        }
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val device = devicesList[position]
        holder.itemView.tvDeviceName.text =  device.name.toString().trim()

        if( isConnected(device) ) {
            holder.itemView.btnStatus.setTextColor(Color.BLUE)
            holder.itemView.btnStatus.setBackgroundColor(Color.WHITE)
            holder.itemView.btnStatus.setText("已連線")
        }
        else {
            holder.itemView.btnStatus.setTextColor(Color.RED)
            holder.itemView.btnStatus.setBackgroundColor(Color.WHITE)
            holder.itemView.btnStatus.setText("未連線")
            connect(device);
        }
        holder.itemView.setOnClickListener {
            if( !isConnected(device) )
            connect(device)
        }
    }

    fun addItems(list: MutableSet<BluetoothDevice>) {
        devicesList.clear()
        list.forEach{
        //    checkMacAddressArrange(it.address.toString())

//                    if( checkMacAddressArrange("00026625B1EF")){
            if( checkMacAddressArrange(it.address.toString())){
                    devicesList.add(it);
            }else if( it.name.toUpperCase().indexOf("RX")!=-1 || it.name.toUpperCase().indexOf("HL")!=-1){
                if( !devicesList.contains(it))
                devicesList.add(it);
            }
        }
        notifyDataSetChanged()
    }

     fun checkHLdevice(aBluetoothDevice:BluetoothDevice):Boolean{
        if( checkMacAddressArrange(aBluetoothDevice.address.toString())){
           return true
        }else if( aBluetoothDevice.name.toUpperCase().indexOf("RX")!=-1 || aBluetoothDevice.name.toUpperCase().indexOf("HL")!=-1) {
            if (!devicesList.contains(aBluetoothDevice))
                return true
        }
        return false
    }

    fun checkMacAddressArrange(sMacAddress:String):Boolean{

//        System.out.println(sMacAddress.replace(":","") +" asdf length: "+sMacAddress.length)

        val a: Long = "26625B1EF".toLong(radix = 16)
        val b: Long = sMacAddress.replace(":","").substring(3).toLong(radix = 16)
        return b>=a && (b-2000)<=a;
    }
}