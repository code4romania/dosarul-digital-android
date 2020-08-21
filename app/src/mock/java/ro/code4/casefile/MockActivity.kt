package ro.code4.casefile
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

class MockActivity : AppCompatActivity() {

    var binding: Any? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layout)
    }

    companion object {
        var layout: Int = 0
    }
}
