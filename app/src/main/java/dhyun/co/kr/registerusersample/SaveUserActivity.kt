package dhyun.co.kr.registerusersample

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.CursorLoader
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ImageView
import dhyun.co.kr.registerusersample.db.DBHandler_Anko
import dhyun.co.kr.registerusersample.db.UserInfo
import kotlinx.android.synthetic.main.activity_save_user.*

/**
 * Created by nanjui on 2016. 10. 29..
 */
class SaveUserActivity :AppCompatActivity(){
    val mDbHandler = DBHandler_Anko(this)
    val PICK_IMAGE:Int = 1010
    val REQ_PERMISSION = 1011
    var mSelectedImgId:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_save_user)
    }

    fun onClickImage(view:View?)
    {
        val check = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if(check != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_PERMISSION)
        }
        else{
            val i = Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(i, PICK_IMAGE)
        }
    }

    private fun showSetting()
    {
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val notGranted = kotlin.arrayOfNulls<String>(permissions.size)
        when(requestCode)
        {
            REQ_PERMISSION->{
                var index = 0
                for(i in 0 until permissions.size) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        val rationale = ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])
                        Log.d("NJ LEE", "rationale : $rationale")
                        if (!rationale) {
                            //Show Dialog that explain to grant permission
                            val dialogBuild = AlertDialog.Builder(this).setTitle("권한 설정")
                                    .setMessage("이미지 썸네일을 만들지 위해서 저장권한 필요합니다. 승인 하지 않으면 이미지를 설정 할 수 없습니다.")
                                    .setCancelable(true)
                                    .setPositiveButton("설정하러 가기"){
                                        dialog, whichButton -> showSetting()
                                    }


                            dialogBuild.create().show()
                            return
                        }
                        else
                        {
                            notGranted[index++] = permissions[i]
                        }
                    }
                }

                if(notGranted.isNotEmpty())
                {
                    ActivityCompat.requestPermissions(this, notGranted, REQ_PERMISSION)
                }
            }
        }
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode)
        {
            PICK_IMAGE->{
                val uri = data?.data
                uri?:return

                mSelectedImgId = getImageID(uri)
                if(mSelectedImgId == -1L ) return
                val bitmap:Bitmap = MediaStore.Images.Thumbnails.getThumbnail(contentResolver,mSelectedImgId,
                        MediaStore.Images.Thumbnails.MICRO_KIND,null)

                val sel_image:ImageView = findViewById(R.id.sel_image)
                sel_image.setImageBitmap(bitmap)
            }
        }
    }

    private fun getImageID(uri:Uri):Long{
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor.getColumnIndex(MediaStore.Images.Media._ID)

        if(column_index == -1)
            return -1

        cursor.moveToFirst()
        val id = cursor.getLong(column_index)
        cursor.close()
        return id
    }

    fun onClickSaveBtn(view: View)
    {
        val user = UserInfo(edit_name.text.toString(), edit_age.text.toString(),
                edit_tel.text.toString(), mSelectedImgId.toString())

        mDbHandler.addUser(user)
        mDbHandler.close()
        finish()
    }
}

