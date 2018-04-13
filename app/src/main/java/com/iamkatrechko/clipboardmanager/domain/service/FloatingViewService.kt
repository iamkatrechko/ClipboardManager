package com.iamkatrechko.clipboardmanager.domain.service

import android.app.Service
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.PixelFormat
import android.support.v7.widget.LinearLayoutManager
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.content.systemService
import com.iamkatrechko.clipboardmanager.R
import com.iamkatrechko.clipboardmanager.data.repository.CategoryRepository
import com.iamkatrechko.clipboardmanager.data.repository.ClipboardRepository
import com.iamkatrechko.clipboardmanager.databinding.FloatViewBinding
import com.iamkatrechko.clipboardmanager.domain.service.experiment.CursorClipsRepo
import com.iamkatrechko.clipboardmanager.domain.util.ClipUtils
import com.iamkatrechko.clipboardmanager.view.adapter.ClipsAdapter
import com.iamkatrechko.clipboardmanager.view.extension.TAG
import com.iamkatrechko.clipboardmanager.view.extension.showToast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo

/**
 * Плавающий виджет со списком заметок
 * @author iamkatrechko
 *         Date: 01.11.2016
 */
class FloatingViewService : Service() {

    /** Менеджер экрана  */
    private val windowManager by lazy { systemService<WindowManager>() }

    /** Биндинг разметки */
    private lateinit var binding: FloatViewBinding

    /** Адаптер списка записей  */
    private var clipsAdapter: ClipsAdapter = ClipsAdapter()
    /** Адаптер выпадающего списка  */
    private var spinnerAdapter: ArrayAdapter<CharSequence>? = null
    /** Список подписчиков */
    private val disposables = CompositeDisposable()

    override fun onCreate() {
        Log.i(TAG, "onCreate")
        binding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.float_view, null, false)

        // TODO Добавить пустой view
        binding.recyclerView.layoutManager = LinearLayoutManager(baseContext)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.adapter = clipsAdapter

        clipsAdapter.onClickListener = { clipId ->
            val clip = ClipboardRepository.getInstance().getClip(this@FloatingViewService, clipId)
            ClipUtils.sendClipToMyAccessibilityService(this@FloatingViewService, clip?.text)
            showToast("Id = $clipId")
        }
        binding.ivClose.setOnClickListener {
            windowManager?.removeView(binding.root)
            stopSelf()
        }

        CursorClipsRepo.getInstance()
                .getClips(this)
                .subscribe({ clips ->
                    clipsAdapter.setClips(clips)
                }, {
                    Log.e(TAG, "Ошибка", it)
                    showToast("Ошибка")
                })
                .addTo(disposables)

        val categoriesTitles = CategoryRepository.getInstance().getCategories(this).map { it.title }.toTypedArray()
        spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoriesTitles)
        spinnerAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = spinnerAdapter

        val displayMetrics = DisplayMetrics()
        windowManager?.defaultDisplay?.getMetrics(displayMetrics)
        val myParams = WindowManager.LayoutParams(
                Math.round(320 * displayMetrics.density),
                Math.round(320 * displayMetrics.density),
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT)
        myParams.gravity = Gravity.TOP or Gravity.START
        myParams.x = 300
        myParams.y = 600

        try {
            //for moving the picture on touch and slide
            binding.root.setOnTouchListener(object : View.OnTouchListener {
                internal var paramsT = myParams
                private var initialX: Int = 0   //Откуда стартовал левый угол
                private var initialY: Int = 0
                private var initialTouchX: Float = 0.toFloat()    //Откуда стартовало движение от нажатия
                private var initialTouchY: Float = 0.toFloat()
                private val touchStartTime: Long = 0
                private var lastX = 0f
                private val lastY = 0f

                override fun onTouch(v: View, event: MotionEvent): Boolean {
                    //Log.d(TAG, "onTouch");
                    //remove face bubble on long press
                    if (System.currentTimeMillis() - touchStartTime > ViewConfiguration.getLongPressTimeout() && initialTouchX == event.x) {
                        //windowManager.removeView(mainView);
                        //stopSelf();
                        return false
                    }
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d(TAG, "ACTION_DOWN - " + event.rawX + " : " + event.rawY)
                            //touchStartTime = System.currentTimeMillis();
                            initialX = myParams.x
                            initialY = myParams.y
                            initialTouchX = event.rawX
                            initialTouchY = event.rawY
                        }
                        MotionEvent.ACTION_UP -> Log.d(TAG, "ACTION_UP - " + event.rawX + " : " + event.rawY)
                        MotionEvent.ACTION_MOVE -> {
                            //Log.d(TAG, "ACTION_MOVE - " + event.getRawX() + " : " + event.getRawY());

                            lastX = v.x

                            myParams.x = initialX + (event.rawX - initialTouchX).toInt()
                            myParams.y = initialY + (event.rawY - initialTouchY).toInt()        //>0
                            windowManager?.updateViewLayout(binding.root, myParams)

                            /*Point size = new Point();
                            windowManager.getDefaultDisplay().getSize(size);*/
                            val posLeft = myParams.x.toFloat()
                            val posRight = posLeft + binding.root.layoutParams.height

                            val posTop = myParams.y.toFloat()
                            val posBottom = posTop + binding.root.layoutParams.width

                            if (posTop < 0) {
                                Log.d("ERROR", "YYYYYYYYYY")
                            }
                            if (posLeft < 0) {
                                Log.d("ERROR", "XXXXXXXXXX")
                            }
                        }
                    }/*if (lastX == v.getX()){
                                Log.d("ACTION_MOVE", "=========================");
                            }else{
                                Log.d("ACTION_MOVE", "!!!!!!!!!!!!!!!!!!!!!!!!!");
                            }*/
                    return false
                }
            })
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка плавающего окна", e)
        }

        windowManager?.addView(binding.root, myParams)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        return Service.START_NOT_STICKY
    }

    override fun onBind(intent: Intent) = null

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy")
        disposables.dispose()
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        Log.i(TAG, "onTaskRemoved")
    }
}
