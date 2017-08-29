package com.deadmc.wizardstepview

/**
 * WizardStepView
 * https://github.com/DEADMC/WizardStepView
 * Created by DEADMC on 28.08.2017.
 */
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View


open class WizardStepView : View {

    //public
    var clickListener:WizardClickListener? = null

    //base
    private val TAG = this.javaClass.simpleName
    private val ctx: Context
    //default colors
    private val DEFAULT_ACTIVE_COLOR = R.color.activeColor
    private val DEFAULT_INACTIVE_COLOR = R.color.inactiveColor
    private val DEFAULT_TEXT_COLOR = R.color.textColor
    //default sizes
    private val DEFAULT_STEP_CIRCLE_RADIUS = 16f
    private val DEFAULT_STEP_LINE_HEIGHT = 16f
    //colors
    private var activeColor = ContextCompat.getColor(context, DEFAULT_ACTIVE_COLOR)
    private var inactiveColor = ContextCompat.getColor(context, DEFAULT_INACTIVE_COLOR)
    private var textActiveColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
    private var textInactiveColor = ContextCompat.getColor(context, DEFAULT_TEXT_COLOR)
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
    private var animationProgressForward = 0f
    private var animationProgressBackward = 1f
    private var animationSpeed = 0.05f


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

    fun setCurrentPosition(position: Int) {
        newStep = position
        invalidate()
    }





    protected fun init() {
        Log.e(TAG, "init started")
        linePaint.strokeWidth = cirleRadius * 0.5f
        isClickable = true
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    protected fun defaultInit() {
        cirleRadius = dp2px(DEFAULT_STEP_CIRCLE_RADIUS).toInt()
        lineHeight = dp2px(DEFAULT_STEP_LINE_HEIGHT).toInt()
    }


    protected fun initAttributes(attrs: AttributeSet) {
        val attr = context.obtainStyledAttributes(attrs, R.styleable.WizardStepView, 0, 0)
        try {
            //colors
            activeColor = attr.getColor(R.styleable.WizardStepView_activeColor, ContextCompat.getColor(context, DEFAULT_ACTIVE_COLOR))
            inactiveColor = attr.getColor(R.styleable.WizardStepView_activeColor, ContextCompat.getColor(context, DEFAULT_INACTIVE_COLOR))
            textActiveColor = attr.getColor(R.styleable.WizardStepView_activeColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
            textInactiveColor = attr.getColor(R.styleable.WizardStepView_inactiveColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
            //sizes
            cirleRadius = attr.getDimension(R.styleable.WizardStepView_cirleRadius, dp2px(DEFAULT_STEP_CIRCLE_RADIUS)).toInt()
            lineHeight = attr.getDimension(R.styleable.WizardStepView_lineHeight, dp2px(DEFAULT_STEP_LINE_HEIGHT)).toInt()
        } finally {
            attr.recycle()
        }
    }

    protected fun dp2px(dp: Float): Float {
        val displayMetrics = context.resources.displayMetrics
        return Math.round(dp * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawLines(canvas)
        drawCircle(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, cirleRadius * 3)
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(cirleRadius * 3))
        elementHeight = height / 2
        screenPart = width / (2 * stepsCount)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        elementHeight = height / 2
        invalidate()
    }

    protected fun drawCircle(canvas: Canvas?) {
        for (i in 1..stepsCount) {
            cirlePaint.color = inactiveColor
            textPaint.color = textInactiveColor
            if ((currentStep <= newStep && i <= currentStep) || (currentStep > newStep && i <= currentStep-1)) {
                cirlePaint.color = activeColor
                textPaint.color = textActiveColor
            }

            drawCirle(canvas, i)
        }
    }

    protected fun drawCirle(canvas: Canvas?, i: Int) {
        textPaint.textSize = cirleRadius.toFloat()
        var offset = screenPart * 2f * (i - 1) + screenPart
        canvas?.drawCircle(offset, elementHeight.toFloat(), cirleRadius.toFloat(), cirlePaint)
        val textY = elementHeight.toFloat()+(textPaint.descent()-textPaint.ascent())/4
        var textX = offset+(textPaint.descent()+textPaint.ascent())*0.4f
        canvas?.drawText(i.toString(),textX,textY,textPaint)
    }

    protected fun drawLines(canvas: Canvas?) {
        if (currentStep == newStep) {
            linePaint.color = inactiveColor
            for (i in 2..stepsCount) {
                if (i <= currentStep)
                    linePaint.color = activeColor
                drawLine(canvas, i)
            }
        } else {
            if (currentStep < newStep) {
                drawDefaultLines(canvas,currentStep)

                drawLineProgress(canvas, currentStep + 1, animationProgressForward)
                animationProgressForward += animationSpeed
                if (animationProgressForward > 1.0f) {
                    currentStep++
                    animationProgressForward = 0f
                }
            } else {
                drawDefaultLines(canvas,currentStep-1)

                drawLineProgress(canvas, currentStep, animationProgressBackward)
                Log.e(TAG,"animationProgressBackward = $animationProgressBackward")
                animationProgressBackward -= animationSpeed
                if (animationProgressBackward < 0f) {
                    currentStep--
                    animationProgressBackward = 1f
                }
            }

        }

        if (currentStep != newStep)
            invalidate()
    }

    protected fun drawLine(canvas: Canvas?, i: Int) {
        var startX = screenPart * 2f * (i - 2) + screenPart + cirleRadius * 0.95f
        var stopX = screenPart * 2f * (i - 1) + screenPart + cirleRadius *0.95f
        canvas?.drawLine(startX, elementHeight.toFloat(), stopX, elementHeight.toFloat(), linePaint)
    }

    protected fun drawLineProgress(canvas: Canvas?, i: Int, progress: Float) {
        linePaint.color = activeColor
        var startX = screenPart * 2f * (i - 2) + screenPart + cirleRadius * 0.95f
        var predictionStopX = (screenPart * 2f * (i - 1) + screenPart + cirleRadius) * progress
        var animation = (predictionStopX - startX) * progress
        var stopX = startX + animation
        canvas?.drawLine(startX, elementHeight.toFloat(), stopX, elementHeight.toFloat(), linePaint)
    }

    protected fun drawDefaultLines(canvas: Canvas?, limit:Int) {
        for (i in 2..stepsCount) {
            linePaint.color = inactiveColor
            drawLine(canvas, i)
        }
        for (i in 2..limit) {
            linePaint.color = activeColor
            drawLine(canvas, i)
        }
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

    protected fun solveClick(x: Float) {
        var position = 1
        for (i in 1..stepsCount) {
            if (x < screenPart * 2 * i) {
                position = i
                break
            }
        }
        if (clickListener == null) {
            setCurrentPosition(position)
        } else {
            clickListener?.click(position)
        }
    }


}

