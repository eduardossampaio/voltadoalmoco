package apps.esampaio.com.voltadoalmoco.views.main.viewmodel

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.Subject
import java.util.*



interface MainViewModel {
    val onTimeUpdate: Subject<Int>?

    fun update();

    fun start(initDate:Date);

    fun stop();

}