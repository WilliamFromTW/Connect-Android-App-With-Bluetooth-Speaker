package com.example.bluetoothheadsetconnectivity

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_paired_device_item.view.*
import java.lang.reflect.Method

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

        if( isConnected(device) )
          holder.itemView.tvDeviceName.text = "設備 "+device.name +" 已連線"
        else
          holder.itemView.tvDeviceName.text = "設備 "+device.name +" 未連線"
        connect(device);
        holder.itemView.tvDeviceAddress.text = device.address
        holder.itemView.setOnClickListener {
            if( !isConnected(device) )
            connect(device)
        }
    }

    fun addItems(list: MutableSet<BluetoothDevice>) {
        devicesList.clear()
        list.forEach{
            if( it.name==null|| it.name.trim() == ""){
                if( it.address.toUpperCase().indexOf("F3")!=-1){
                   ;// devicesList.add(it);
                }
            }else if( it.name.toUpperCase().indexOf("RX")!=-1 || it.name.toUpperCase().indexOf("HL")!=-1){
                devicesList.add(it);
            }
        }
        notifyDataSetChanged()
    }
}