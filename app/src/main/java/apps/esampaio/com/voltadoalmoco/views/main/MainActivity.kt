package apps.esampaio.com.voltadoalmoco.views.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.work.*
import apps.esampaio.com.voltadoalmoco.databinding.ActivityMain2Binding
import apps.esampaio.com.voltadoalmoco.utils.extensions.checkIfRunning
import apps.esampaio.com.voltadoalmoco.views.main.viewmodel.MainViewModel
import apps.esampaio.com.voltadoalmoco.views.main.viewmodel.MainViewModelImpl
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMain2Binding;

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = MainViewModelImpl(this)
        this.binding = ActivityMain2Binding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.buttonStop.setOnClickListener { this.stop() }
        binding.editText.setOnEditorActionListener { textView: TextView?, actionId: Int, keyEvent: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                start()
                true;
            }
            false
        };

        viewModel.onTimeUpdate?.subscribe {
            if(isInStart()){
                transitionToEnd()
            }
            val remainingMinutes = it;
            updateRemainingMinutes(remainingMinutes)
        }
        //viewModel.onTimeUpdate?.publish()

    }

    private fun start() {
     transitionToEnd();
        var initDate = Date(System.currentTimeMillis());
        try{
            val dateFormatter = SimpleDateFormat("HH:mm")
            val parsedDate = dateFormatter.parse(binding.editText.text.toString());
            val inputTimeCalendar = Calendar.getInstance();
           inputTimeCalendar.time = parsedDate;
            val hour = inputTimeCalendar.get(Calendar.HOUR)
            val minutes = inputTimeCalendar.get(Calendar.MINUTE)


            val startTimeCalendar = Calendar.getInstance();
            startTimeCalendar.time = Date(System.currentTimeMillis())
            startTimeCalendar.set(Calendar.HOUR,hour)
            startTimeCalendar.set(Calendar.MINUTE,minutes);
            startTimeCalendar.set(Calendar.SECOND,0)

            initDate = startTimeCalendar.time;
        }catch (e: Exception){
            e.printStackTrace();
        }
        viewModel.start(initDate)
    }

    private fun isInStart() : Boolean{
        return binding.root.progress == 0.0f;
    }
    private fun stop(){
        transitionToStart();
        binding.editText.requestFocus()
        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        viewModel.stop();
    }

    private fun transitionToStart(){
        binding.editText.isEnabled = true
        binding.root.transitionToStart()
    }
    private fun transitionToEnd(){
        binding.editText.isEnabled = false
        binding.root.transitionToEnd()
    }

    private fun updateRemainingMinutes(minutes:Int){
        binding.countText.text = "$minutes";
    }

}


