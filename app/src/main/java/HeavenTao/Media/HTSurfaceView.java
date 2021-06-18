package HeavenTao.Media;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceView;

//自定义SurfaceView类， for 了保持宽highratio。
public class HTSurfaceView extends SurfaceView
{
    public float m_WidthToHeightRatio = 1.0f; //存放宽highratio。

    public HTSurfaceView( Context context )
    {
        super( context );
    }

    public HTSurfaceView( Context context, AttributeSet attrs )
    {
        super( context, attrs );
    }

    public HTSurfaceView( Context context, AttributeSet attrs, int defStyleAttr )
    {
        super( context, attrs, defStyleAttr );
    }

    public HTSurfaceView( Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes )
    {
        super( context, attrs, defStyleAttr, defStyleRes );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        int width = MeasureSpec.getSize( widthMeasureSpec );
        float height = width / m_WidthToHeightRatio;
        heightMeasureSpec = MeasureSpec.makeMeasureSpec( ( int ) height, MeasureSpec.EXACTLY );
        super.onMeasure( widthMeasureSpec, heightMeasureSpec );
    }

    public void setWidthToHeightRatio( float WidthToHeightRatio )
    {
        if( WidthToHeightRatio != m_WidthToHeightRatio ) //如果指定的宽highratio与当前的宽highratio不一致。
        {
            m_WidthToHeightRatio = WidthToHeightRatio; //Настраиватьvideo预览SurfaceView类对象的宽highratio。

            post( new Runnable() //刷新SurfaceView类对象的尺寸显示。
            {
                @Override
                public void run()
                {
                    setLayoutParams( getLayoutParams() );
                }
            } );
        }
    }
}
