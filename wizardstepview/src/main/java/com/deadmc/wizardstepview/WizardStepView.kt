package com.deadmc.wizardstepview

/**
 * Created by adanilov on 28.08.2017.
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View


open class WizardStepView : View {

    val TAG = this.javaClass.simpleName
    private val ctx: Context
    //default colors
    private val DEFAULT_ACTIVE_COLOR = R.color.activeColor
    private val DEFAULT_INACTIVE_COLOR = R.color.inactiveColor
    private val DEFAULT_TEXT_COLOR = R.color.inactiveColor
    //default sizes
    private val DEFAULT_STEP_CIRCLE_RADIUS = 16f
    private val DEFAULT_STEP_LINE_HEIGHT = 16f
    //colors
    private var activeColor = ContextCompat.getColor(context, DEFAULT_ACTIVE_COLOR)
    private var inactiveColor = ContextCompat.getColor(context, DEFAULT_INACTIVE_COLOR)
    private var textColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
    //points
    private val cirlePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    //sizes
    private var elementHeight = 0 //center Y of element
    private var elementWidth = 0 //whole width of element
    private var cirleRadius = 0 //radius of step cirles
    private var lineHeight = 0 //heigth of line between cirles
    private var screenPart = 0  //part of screen to center views
    //current state
    private var stepsCount = 3
    private var currentStep = 1
    private var newStep = 1
    //animation
    private var animationProgress = 0f
    private var animationSpeed = 0.1f




    constructor(ctx: Context) : super(ctx) {
        this.ctx = ctx
        init()
        defaultInit()
    }

    constructor(ctx: Context, attrs: AttributeSet) : super(ctx, attrs) {
        this.ctx = ctx
        initAttributes(attrs)
        init()
    }

    constructor(ctx: Context, attrs: AttributeSet, defStyleAttr: Int) : super(ctx, attrs, defStyleAttr) {
        this.ctx = ctx
        initAttributes(attrs)
        init()
    }

    fun init() {
        Log.e(TAG, "init started")
        linePaint.strokeWidth = cirleRadius * 0.5f
        isClickable = true

    }

    fun defaultInit() {
        cirleRadius = dp2px(DEFAULT_STEP_CIRCLE_RADIUS).toInt()
        lineHeight = dp2px(DEFAULT_STEP_LINE_HEIGHT).toInt()
    }


    fun initAttributes(attrs: AttributeSet) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.WizardStepView, 0, 0)
        try {
            //colors
            activeColor = attr.getColor(R.styleable.WizardStepView_activeColor, ContextCompat.getColor(context, DEFAULT_ACTIVE_COLOR))
            inactiveColor = attr.getColor(R.styleable.WizardStepView_activeColor, ContextCompat.getColor(context, DEFAULT_INACTIVE_COLOR))
            textColor = attr.getColor(R.styleable.WizardStepView_activeColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
            //sizes
            cirleRadius = attr.getDimension(R.styleable.WizardStepView_cirleRadius, dp2px(DEFAULT_STEP_CIRCLE_RADIUS)).toInt()
            lineHeight = attr.getDimension(R.styleable.WizardStepView_lineHeight, dp2px(DEFAULT_STEP_LINE_HEIGHT)).toInt()
        } finally {
            attr.recycle()
        }
    }

    private fun dp2px(dp: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.e(TAG, "onDraw started")
        drawLines(canvas)
        drawCircle(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, cirleRadius * 3)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(cirleRadius * 3))
        elementHeight = height / 2
        screenPart = width / (2 * stepsCount)
        Log.e(TAG, "onDraw started, elementHeight = $elementHeight, cirleRadius = $cirleRadius, width = $width")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        elementHeight = height / 2
        invalidate()
    }

    fun drawCircle(canvas: Canvas?) {
        Log.e(TAG, "drawCircle started")
        for (i in 1..stepsCount) {
            if (i <= currentStep)
                cirlePaint.color = activeColor
            else
                cirlePaint.color = inactiveColor
            drawCirle(canvas, i)
        }
    }

    fun drawCirle(canvas: Canvas?, i: Int) {
        var offset = screenPart * 2f * (i - 1) + screenPart
        canvas?.drawCircle(offset, elementHeight.toFloat(), cirleRadius.toFloat(), cirlePaint)
    }

    fun drawLines(canvas: Canvas?) {
        if (currentStep == newStep) {
            for (i in 2..stepsCount) {
                if (i <= currentStep)
                    linePaint.color = activeColor
                else
                    linePaint.color = inactiveColor
                drawLine(canvas, i)
            }
        } else {
            for (i in 2..stepsCount) {
                linePaint.color = inactiveColor
                drawLine(canvas, i)
            }

            if (newStep - 1 > 2) {
                for (i in 2..newStep - 1) {
                    linePaint.color = activeColor
                    drawLine(canvas, i)
                }
            }

            drawLineProgress(canvas,currentStep+1,animationProgress)
            animationProgress+=animationSpeed
            if (animationProgress > 1.0f) {
                currentStep++
                animationProgress = 0f
            }

        }

        if (currentStep < newStep)
            invalidate()
    }

    fun drawLine(canvas: Canvas?, i: Int) {
        var startX = screenPart * 2f * (i - 2) + screenPart + cirleRadius * 0.95f
        var stopX = screenPart * 2f * (i - 1) + screenPart + cirleRadius
        canvas?.drawLine(startX, elementHeight.toFloat(), stopX, elementHeight.toFloat(), linePaint)
    }

    fun drawLineProgress(canvas: Canvas?, i: Int, progress:Float) {
        var startX = screenPart * 2f * (i - 2) + screenPart + cirleRadius * 0.95f
        var predictionStopX = (screenPart * 2f * (i - 1) + screenPart + cirleRadius)*progress
        var animation = (predictionStopX - startX)*progress
        var stopX = startX+animation
        canvas?.drawLine(startX, elementHeight.toFloat(), stopX, elementHeight.toFloat(), linePaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isClickable)
            return super.onTouchEvent(event)
        when (event?.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                solveClick(event.x)
            }
        }
        return true
    }

    fun solveClick(x: Float) {
        var position = 1
        for (i in 1..stepsCount) {
            if (x < screenPart * 2 * i) {
                position = i
                break
            }
        }
        setCurrentPosition(position)
    }

    fun setCurrentPosition(position: Int) {
        newStep = position
        invalidate()
    }
}

