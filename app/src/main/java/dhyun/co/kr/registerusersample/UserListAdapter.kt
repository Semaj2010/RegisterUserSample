package dhyun.co.kr.registerusersample

import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.widget.CursorAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

data class ViewHolder(val pic:ImageView,
                      val name:TextView,
                      val tel:TextView,
                      val del:ImageView)

class UserListAdapter (context: Context?, cursor:Cursor?)
    : CursorAdapter(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER){

    val mCtxt = context

    override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?): View {
        val inflater = context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val mainView = inflater.inflate(R.layout.layout_user_list,parent,false)
        var holder:ViewHolder = ViewHolder(mainView.findViewById(R.id.profile) as ImageView,
                mainView.findViewById(R.id.name) as TextView,
                mainView.findViewById(R.id.tel_num) as TextView,
                mainView.findViewById(R.id.del_item) as ImageView)
        mainView.tag = holder
        return mainView
    }

    override fun bindView(view: View?, context: Context?, cursor: Cursor?) {
        val holder = view?.tag as ViewHolder

        holder.name.text = String.format("%s (%d)", cursor?.getString(1), cursor?.getInt(2))
        holder.tel.text = cursor?.getString(3)
        val picture: Drawable? = getPicture(cursor?.getString(4)?:"")?:context?.resources?.getDrawable(android.R.drawable.ic_menu_gallery)
        holder.pic.background = picture
        //save cursor id
        holder.del.tag = cursor?.getLong(0)
    }

    private fun getPicture(path:String): Drawable?{
        val img_id = path.toLong()
        if(img_id == 0L) return null
        val bitmap: Bitmap = MediaStore.Images.Thumbnails.getThumbnail(mCtxt?.contentResolver,img_id, MediaStore.Images.Thumbnails.MICRO_KIND,null)
        bitmap
        return BitmapDrawable(mCtxt?.resources,bitmap)
    }
}