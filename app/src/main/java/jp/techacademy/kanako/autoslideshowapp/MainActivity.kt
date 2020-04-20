package jp.techacademy.kanako.autoslideshowapp

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.os.Handler
import kotlin.concurrent.schedule

class MainActivity(var cnt: Int) : AppCompatActivity(),  View.OnClickListener{

    private val PERMISSIONS_REQUEST_CODE = 100

    private var mTimer: Timer? = null

    // タイマー用の時間のための変数
    private var mTimerSec = 0.0
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
              // パーミッションの許可状態を確認する
              if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                  // 許可されている
                  getContentsInfo()
              } else {
                  // 許可されていないので許可ダイアログを表示する
                  requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                      PERMISSIONS_REQUEST_CODE)
              }
              // Android 5系以下の場合
          } else {
              getContentsInfo()
          }
        //ボタン追加
        next_button.setOnClickListener(this)
        back_button.setOnClickListener(this)

        //再生・停止ボタン
        fun slide() {

        }
        pause_button.setOnClickListener{
            if (pause_button.text == "再生"){
                mTimer = Timer()

                mTimer!!.schedule(object : TimerTask()){
                    fun run(){
                        mHandler.post{
                            slide()
                            pause_button.text = "停止"
                        }
                    }
                }


            }
        }



    }

    //進むボタンと戻るボタン   corsorをメンバ変数に変更
    override fun onClick(v: View) {
        if (v.id == R.id.next_button){
            if (cursor!!.moveToNext()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }

        }else if (v.id == R.id.back_button){
            if (cursor!!.moveToPrevious()) {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
                val id = cursor.getLong(fieldIndex)
                val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                imageView.setImageURI(imageUri)
            }

        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )

//開いた時最初の画像表示
        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }

//次の画像を表示
        if (cursor!!.moveToNext()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID)
            val id = cursor.getLong(fieldIndex)
            val imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }



    }


}



//スライドさせる画像は、Android端末に保存されているGallery画像を表示させてください（つまり、ContentProviderの利用）
//画面にはImageViewと3つのボタン（進む、戻る、再生/停止）を配置してください
//進むボタンで1つ先の画像を表示し、戻るボタンで1つ前の画像を表示します
//最後の画像の表示時に、進むボタンをタップすると、最初の画像が表示されるようにしてください
//最初の画像の表示時に、戻るボタンをタップすると、最後の画像が表示されるようにしてください
//再生ボタンをタップすると2秒後に自動送りが始まり、2秒毎にスライドさせてください
//自動送りの間は、進むボタンと戻るボタンはタップ不可にしてください
//再生ボタンをタップすると、ボタンの表示が「停止」になり、停止ボタンをタップするとボタンの表示が「再生」になるようににしてください
//停止ボタンをタップすると自動送りが止まり、進むボタンと戻るボタンをタップ可能にしてください
//ユーザがパーミッションの利用を「拒否」した場合にも、アプリの強制終了やエラーが発生しないようにして下さい。