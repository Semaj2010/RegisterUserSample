package paytime.co.kr.registerusersample

import android.app.Application
import paytime.co.kr.registerusersample.db.MyObjectBox

class BasicApplication : Application() {

    val boxStore = MyObjectBox.builder().androidContext(this).build()
}