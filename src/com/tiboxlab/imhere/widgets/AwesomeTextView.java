package com.tiboxlab.imhere.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AwesomeTextView extends TextView
{
	public static Typeface font = null;

	public AwesomeTextView(Context context)
	{
		super(context);
		init();
	}

	public AwesomeTextView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}

	public AwesomeTextView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		init();
	}

	private void init()
	{
		setPadding(0, 0, 0, 0);
		if (isInEditMode())
		{
			setBackgroundColor(0xFFFF0000);
			return;
		}
		if (font == null) font = Typeface.createFromAsset(getContext().getAssets(), "fontawesome.ttf");
		setTypeface(font);
	}
}
