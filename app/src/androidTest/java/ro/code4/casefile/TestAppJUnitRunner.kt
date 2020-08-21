
package ro.code4.casefile

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

class TestAppJUnitRunner : AndroidJUnitRunner() {

    override fun newApplication(cl: ClassLoader?, className: String?, context: Context?): Application {
        return super.newApplication(cl, App::class.java.name, context)
    }
}
