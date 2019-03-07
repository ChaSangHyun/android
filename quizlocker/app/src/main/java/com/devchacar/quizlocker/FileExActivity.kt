package com.devchacar.quizlocker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_file_ex.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

class FileExActivity : AppCompatActivity() {

    // 데이터 저장에 사용할 파일이름
    val filename = "data.txt"

    val MY_PERMISSION_REQUEST = 999
    var granted:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_ex)

        checkPermission()

        // 저장 버튼이 클릭된 경우
        saveButton.setOnClickListener {
            val text = textField.text.toString()

            when {
                TextUtils.isEmpty(text) -> {
                    Toast.makeText(applicationContext, "텍스트가 비어있습니다.", Toast.LENGTH_LONG).show()
                }
                !isExternalStorageWritable() -> {
                    Toast.makeText(applicationContext, "외부 저장장치가 없습니다.", Toast.LENGTH_LONG).show()
                }
                else -> {
                    //saveToInnerStorage(text, filename)
                    //saveToExternalStorage(text, filename)
                    saveToExternalCustomDirectory(text)
                }
            }
        }

        // 불러오기 버튼 클릭
        loadButton.setOnClickListener {
            try {
                //textField.setText(loadFromInnerStorage(filename))

                // 외부 저장소 앱전용 디렉토리의 파일에서 읽어온 데이터로 textField 의 텍스트를 설정
                //textField.setText(loadFromExternalStorage(filename))

                // 외부 저장소"/sdcard/data.txt"에 데이터를 불러온다
                textField.setText(loadFromExternalCustomDirectory())
            } catch (e: FileNotFoundException){
                Toast.makeText(applicationContext, "저장된 텍스트가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 내부
    // 내부저장소 파일을 텍스트에 저장
    fun saveToInnerStorage(text: String, filename: String){
        // 내부 저장소의 전달된 파일이름의 파일 출력 스트림을 가져온다
        val fileOutputStream = openFileOutput(filename, Context.MODE_PRIVATE)

        fileOutputStream.write(text.toByteArray())

        fileOutputStream.close()
    }

    // 내부저장소 파일의 텍스트를 불러온다
    fun loadFromInnerStorage(filename: String):String {
        val fileInputStream = openFileInput(filename)

        return fileInputStream.reader().readText()
    }

    // 외부 저장장치를 사용할 수 있고 쓸수 있는지 체크하는 함수
    fun isExternalStorageWritable():Boolean{
        when {
            Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED -> return true
            else -> return false
        }
    }

    // 외부
    // 외부저장장치에서 앱 전용데이터로 사용할 파일 객체를 반환하는 함수
    fun getAppDataFileFromExternalStorage(filename: String): File {
        val dir = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        } else {
            File(Environment.getExternalStorageDirectory().absolutePath + "/Documents")
        }

        dir?.mkdirs()
        return File("${dir.absolutePath}${File.separator}${filename}")
    }

    // 외부 저장소에 저장
    fun saveToExternalStorage(text: String, filename: String) {
        val fileOutputStream = FileOutputStream(getAppDataFileFromExternalStorage(filename))
        fileOutputStream.write(text.toByteArray())
        fileOutputStream.close()
    }

    // 외부 저장장치에서 불러오기
    fun loadFromExternalStorage(filename:String):String {
        return FileInputStream(getAppDataFileFromExternalStorage(filename)).reader().readText()
    }

    // 외부 저장소 임의의 장소
    // 권한 체크 및 요청 함수
    fun checkPermission(){
        val permissionCheck = ContextCompat.checkSelfPermission(this@FileExActivity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        when{
            permissionCheck != PackageManager.PERMISSION_GRANTED -> {
                ActivityCompat.requestPermissions(this@FileExActivity,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSION_REQUEST)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults:IntArray) {
        when (requestCode) {
            MY_PERMISSION_REQUEST -> {
                when{
                    grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        granted = true
                    }
                    else -> {
                        granted = false
                    }
                }
            }
        }
    }

    fun saveToExternalCustomDirectory(text:String, filepath:String = "/sdcard/data.txt"){
        when {
            granted -> {
                val fileOutputStream = FileOutputStream(File(filepath))
                fileOutputStream.write(text.toByteArray())
                fileOutputStream.close()
            }
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadFromExternalCustomDirectory(filepath:String = "/sdcard/data.txt"):String {
        when {
            granted -> {
                return FileInputStream(File(filepath)).reader().readText()
            }
            else -> {
                Toast.makeText(applicationContext, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
                return ""
            }
        }
    }

}
