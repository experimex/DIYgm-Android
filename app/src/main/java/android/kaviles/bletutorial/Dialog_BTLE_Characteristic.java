package android.kaviles.bletutorial;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import java.util.UUID;

/**
 * Created by Kelvin on 5/9/16.
 */
public class Dialog_BTLE_Characteristic extends DialogFragment implements DialogInterface.OnClickListener {

    private String title;
    private Service_BTLE_GATT service;
    private BluetoothGattCharacteristic characteristic;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setNegativeButton("Cancel", this).setPositiveButton("Yes", this);
        builder.setTitle("Confirm connection");
        builder.setMessage("Do you want to connect to this device?");

        return builder.create();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        // Find a way to check which button as pressed cancel or ok
//            Utils.toast(activity.getApplicationContext(), "Button " + Integer.toString(which) + " Pressed");

        switch (which) {
            case -1:
                // Yes
                service.setCharacteristicNotification(characteristic, true);
                break;
            case -2:
                // Cancel
                service.setCharacteristicNotification(characteristic, false);
                break;
            default:
                break;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setService(Service_BTLE_GATT service) {
        this.service = service;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }
}
