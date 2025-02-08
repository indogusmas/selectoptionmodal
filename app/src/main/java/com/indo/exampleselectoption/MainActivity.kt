package com.indo.exampleselectoption

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.indo.selectoptionmodal.SelectDialogView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        findViewById<Button>(R.id.btn_show).setOnClickListener {
            var list : MutableList<String> = mutableListOf()
            for (i in 1..10){
                list.add("Pilihan${i}")
            }
            for (j in 1..10){
                list.add("Select${j}")
            }

            val dialog = SelectDialogView.Builder<String>()
                .setItems(list)
                .setTitle("Select Your Option")
                .setOnItemSelected { selected ->
                    Toast.makeText(this,"${selected}",Toast.LENGTH_LONG).show()
                }
                .build(this)

            dialog.show()
        }
    }
}