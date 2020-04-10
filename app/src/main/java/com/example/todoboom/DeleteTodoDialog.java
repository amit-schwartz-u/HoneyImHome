package com.example.todoboom;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * This is a dialog themed activity for alerting in the Main Activity when some done
 * todo item is long pressed.
 */
public class DeleteTodoDialog extends AppCompatActivity {
    int todoItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_todo_dialog);
        todoItemPosition = getIntent().getIntExtra("position", 0);
    }

    /**
     * Closes the window when clicked ok or outside the window
     *
     * @param okButton - The ok button in the dialog frame.
     */
    public void onClickOk(View okButton) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("shouldDelete", true);
        intent.putExtra("position", todoItemPosition);
        startActivity(intent);
        finish();
    }

    public void onClickCancel(View view) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("shouldDelete", false);
        startActivity(intent);
        finish();
    }
}
