package be.marche.apptravaux.avaloir

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import be.marche.apptravaux.R
import be.marche.apptravaux.databinding.RedirectActivityBinding

class RedirectActivity : AppCompatActivity() {

    private lateinit var binding: RedirectActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RedirectActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.bottomAppBar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.item1 -> {
                    Toast.makeText(this, "Clicked menu item 1", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
        binding.bottomAppBar.setNavigationOnClickListener {
            Toast.makeText(this, "Clicked navigation item", Toast.LENGTH_SHORT).show()
        }
    }


}
