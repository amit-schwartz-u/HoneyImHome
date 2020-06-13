package com.example.honeyimhome;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a dialog themed activity for alerting in the EditProfileActivity when some fields are not
 * filled correctly.
 */
public class EditNumberAlertDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(null);
        setContentView(R.layout.activity_number_dialog);
    }

    /**
     * Closes the window when clicked ok or outside the window
     * @param okButton - The ok button in the dialog frame.
     */
    public void onClickOk(View okButton){
        EditText numEditText = findViewById(R.id.et_phone_number);
        String phoneNumber = numEditText.getText().toString();
        MyPreferences.savePhoneNumberMyPref(this, phoneNumber);
        finish();
    }
}
