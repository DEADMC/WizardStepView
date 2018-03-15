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
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View




open class WizardStepView : View {

    //public
    var clickListener: WizardClickListener? = null
    var stepsCount = 3
    //ViewPager integration
    var viewPager: ViewPager? = null
        set(value) {
            field = value
            initViewPager()
        }
    var viewPagerState = 0
    var viewPagerAnimation = 0f
    var viewPagerPosition = 0
    var startedTransition = false
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
    private var cirleRadius = 0 //radius of step cirles
    private var lineHeight = 0 //heigth of line between cirles
    private var screenPart = 0  //part of screen to center views
    //current state
    private var currentStep = 1
    private var newStep = 1
    private var clickedStep = false
    //animation
    private var animationProgressForward = 0f
    private var animationProgressBackward = 1f
    private var animationSpeed = 0.05f
    //check image
    private var showCheckImage = true


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
        clickedStep = true
        animationSpeed = 0.05f * Math.abs(currentStep - newStep)
        invalidate()
    }

    private fun initViewPager() {
        val pageListener = object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
                viewPagerState = state
                invalidate()
            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                Log.e(TAG, "onPageScrolled $position, viewPagerState $viewPagerState")
                viewPagerAnimation = positionOffset
                viewPagerPosition = position + 1

                if (viewPagerState == 1) {
                    clickedStep = false
                    if (viewPagerPosition < currentStep) {
                        currentStep = viewPagerPosition
                        newStep = viewPagerPosition
                        startedTransition = true
                    }
                }



                if (viewPagerState == 2) {
                    if (viewPagerPosition > currentStep && !clickedStep) {
                        currentStep = viewPagerPosition
                        newStep = currentStep
                    }
                }


                invalidate()
            }

            override fun onPageSelected(position: Int) {
                Log.e(TAG, "onPageSelected $position")
                currentStep = position + 1
                newStep = currentStep
                startedTransition = false
                invalidate()
                //Log.e(TAG,"page selected $currentStep")
                //setCurrentPosition(position + 1)
            }
        }
        viewPager?.addOnPageChangeListener(pageListener)
    }

    protected fun init() {
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
            inactiveColor = attr.getColor(R.styleable.WizardStepView_inactiveColor, ContextCompat.getColor(context, DEFAULT_INACTIVE_COLOR))
            textActiveColor = attr.getColor(R.styleable.WizardStepView_textActiveColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
            textInactiveColor = attr.getColor(R.styleable.WizardStepView_textInactiveColor, ContextCompat.getColor(context, DEFAULT_TEXT_COLOR))
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
        if (screenPart == 0)
            screenPart = width / (2 * stepsCount)
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
            if ((currentStep in i..newStep) || (currentStep > newStep && i <= currentStep - 1)) {
                cirlePaint.color = activeColor
                textPaint.color = textActiveColor
            }

            drawCirle(canvas, i)
        }
    }

    protected fun drawCirle(canvas: Canvas?, i: Int) {
        textPaint.textSize = cirleRadius.toFloat()
        val offset = screenPart * 2f * (i - 1) + screenPart
        //Log.e(TAG,"offset = $offset , screenPart = $screenPart")
        canvas?.drawCircle(offset, elementHeight.toFloat(), cirleRadius.toFloat(), cirlePaint)
        if (showCheckImage && currentStep > i) {
            drawImage(offset,canvas)
        } else {
            val textY = elementHeight.toFloat() + (textPaint.descent() - textPaint.ascent()) / 4
            val textX = offset + (textPaint.descent() + textPaint.ascent()) * 0.4f
            canvas?.drawText(i.toString(), textX, textY, textPaint)
        }

    }

    protected fun drawImage(offset:Float, canvas:Canvas?) {
        val d = ContextCompat.getDrawable(context,R.drawable.check)
        var ratio = d.intrinsicHeight/d.intrinsicWidth.toFloat()
        var widthModifier = cirleRadius*0.5
        var heightModifier = cirleRadius*0.5*ratio
        if (d.intrinsicHeight > d.intrinsicWidth) {
            ratio = d.intrinsicWidth/d.intrinsicHeight.toFloat()
            widthModifier = cirleRadius*0.5*ratio
            heightModifier = cirleRadius*0.5
        }
        val imgLeft = offset-widthModifier
        val imgRight = offset+widthModifier
        val imgTop = elementHeight.toFloat()-heightModifier
        val imgBottom = elementHeight.toFloat()+heightModifier
        d.setBounds(imgLeft.toInt(), imgTop.toInt(), imgRight.toInt(), imgBottom.toInt())
        d.draw(canvas)
    }

    protected fun drawLines(canvas: Canvas?) {

        if (currentStep == newStep) {
            //Log.e(TAG, "currentStep = $currentStep")
            for (i in 2..stepsCount) {
                linePaint.color = inactiveColor
                if (i <= currentStep)
                    linePaint.color = activeColor
                drawLine(canvas, i)
            }
        } else {
            if (viewPager == null) {
                if (currentStep < newStep) {
                    drawDefaultLines(canvas, currentStep)
                    drawLineProgress(canvas, currentStep + 1, animationProgressForward)
                    animationProgressForward += animationSpeed
                    if (animationProgressForward > 1.05f) {
                        currentStep++
                        animationProgressForward = 0f
                    }
                } else {
                    drawDefaultLines(canvas, currentStep - 1)
                    drawLineProgress(canvas, currentStep, animationProgressBackward)
                    animationProgressBackward -= animationSpeed
                    if (animationProgressBackward < 0f) {
                        currentStep--
                        animationProgressBackward = 1f
                    }
                }
            }

        }

        if (viewPagerState >= 1) {
            drawLineProgress(canvas, viewPagerPosition + 1, viewPagerAnimation)
        }

        if (currentStep != newStep)
            invalidate()
    }

    protected fun drawLine(canvas: Canvas?, i: Int) {
        val startX = screenPart * 2f * (i - 2) + screenPart + cirleRadius * 0.95f
        val stopX = screenPart * 2f * (i - 1) + screenPart + cirleRadius * 0.1f
        canvas?.drawLine(startX, elementHeight.toFloat(), stopX, elementHeight.toFloat(), linePaint)
    }

    protected fun drawLineProgress(canvas: Canvas?, i: Int, progress: Float) {
        //Log.e(TAG, "progress $progress , position $i")
        linePaint.color = activeColor
        val startX = screenPart * 1.95f * (i - 2) + screenPart + cirleRadius
        val predictionStopX = screenPart * 1.95f * (i - 1) + screenPart + cirleRadius*0.0f
        val animation = (predictionStopX - startX) * progress
        val stopX = startX + animation
        canvas?.drawLine(startX, elementHeight.toFloat(), stopX, elementHeight.toFloat(), linePaint)
    }

    protected fun drawDefaultLines(canvas: Canvas?, limit: Int) {
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
                performClick()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
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
            viewPager?.setCurrentItem(position - 1, true)
        } else {
            clickListener?.click(position)
        }
    }


}

