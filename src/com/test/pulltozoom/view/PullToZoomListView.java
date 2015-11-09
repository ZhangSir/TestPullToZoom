package com.test.pulltozoom.view;

import com.test.pulltozoom.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * ListView���͵������������
 * @author zhangshuo
 */
public class PullToZoomListView extends PullToZoomBase<ListView> implements OnScrollListener, OnTouchListener{

	private static final String TAG = PullToZoomListView.class.getSimpleName();
	private FrameLayout mHeaderContainer;
	private int mHeaderHeight;
	private ScalingRunnable mScalingRunnable;
	
	/** �Ƿ���������ˢ��*/
	private boolean isRefreshable = true;
	/** �Ƿ�������������*/
	private boolean isLoadable = true;
	/** �Ƿ�����ˢ�±�ʾ*/
	private boolean isRefreshing = false;
	/** �Ƿ����ڼ��ظ����ʾ*/
	private boolean isLoading = false;
    
	private float lastY;
    private float lastX;
    private float initY;
    private float initX;
	
	 private static final Interpolator sInterpolator = new Interpolator() {
	        public float getInterpolation(float paramAnonymousFloat) {
	            float f = paramAnonymousFloat - 1.0F;
	            return 1.0F + f * (f * (f * (f * f)));
	        }
	    };
	    
	public PullToZoomListView(Context context) {
		this(context, null);
	}

	public PullToZoomListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mRootView.setOnTouchListener(this);
		mRootView.setOnScrollListener(this);
        mScalingRunnable = new ScalingRunnable();
	}

	/**
     * �Ƿ���ʾheaderView
     *
     * @param isHideHeader true: show false: hide
     */
    @Override
    public void setHideHeader(boolean isHideHeader) {
        if (isHideHeader != isHideHeader()) {
            super.setHideHeader(isHideHeader);
            if (isHideHeader) {
                removeHeaderView();
            } else {
                updateHeaderView();
            }
        }
    }
    
    @Override
    public void setHeaderView(View headerView) {
        if (headerView != null) {
            this.mHeaderView = headerView;
            updateHeaderView();
        }
    }

    @Override
    public void setZoomView(View zoomView) {
        if (zoomView != null) {
            this.mZoomView = zoomView;
            updateHeaderView();
        }
    }
    
    /**
     * �Ƴ�HeaderView
     * ���Ҫ����API 9,��Ҫ�޸Ĵ˴��߼���API 11���²�֧�ֶ�̬���header
     */
    private void removeHeaderView() {
        if (mHeaderContainer != null) {
            mRootView.removeHeaderView(mHeaderContainer);
        }
    }

    /**
     * ����HeaderView  ���Ƴ�-->�����zoomView��HeaderView -->Ȼ����ӵ�listView��head
     * ���Ҫ����API 9,��Ҫ�޸Ĵ˴��߼���API 11���²�֧�ֶ�̬���header
     */
    private void updateHeaderView() {
        if (mHeaderContainer != null) {
            mRootView.removeHeaderView(mHeaderContainer);

            mHeaderContainer.removeAllViews();

            if (mZoomView != null) {
                mHeaderContainer.addView(mZoomView);
            }

            if (mHeaderView != null) {
                mHeaderContainer.addView(mHeaderView);
            }

            mHeaderHeight = mHeaderContainer.getHeight();
            mRootView.addHeaderView(mHeaderContainer);
        }
    }
    
    public void setAdapter(ListAdapter adapter) {
        mRootView.setAdapter(adapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mRootView.setOnItemClickListener(listener);
    }
    /**
     * ����listView ���Ҫ����API9,��Ҫ�޸Ĵ˴�
     *
     * @param context ������
     * @param attrs   AttributeSet
     * @return ListView
     */
    @Override
    protected ListView createRootView(Context context, AttributeSet attrs) {
        ListView lv = new ListView(context, attrs);
        // Set it to this so it can be used in ListActivity/ListFragment
        lv.setId(android.R.id.list);
        return lv;
    }

    /**
     * ���ö������Զ�����������
     */
    @Override
    protected void smoothScrollToTop() {
        Log.d(TAG, "smoothScrollToTop --> ");
        mScalingRunnable.startAnimation(200L);
    }

    /**
     * zoomView�����߼�
     *
     * @param newScrollValue ��ָY���ƶ�����ֵ
     */
    @Override
    protected void pullHeaderToZoom(int newScrollValue) {
        Log.d(TAG, "pullHeaderToZoom --> newScrollValue = " + newScrollValue);
        Log.d(TAG, "pullHeaderToZoom --> mHeaderHeight = " + mHeaderHeight);
        if (mScalingRunnable != null && !mScalingRunnable.isFinished()) {
            mScalingRunnable.abortAnimation();
        }

        ViewGroup.LayoutParams localLayoutParams = mHeaderContainer.getLayoutParams();
        localLayoutParams.height = Math.abs(newScrollValue) + mHeaderHeight;
        mHeaderContainer.setLayoutParams(localLayoutParams);
    }
    
    /**
     * �����Ƿ�����ˢ�µ�״̬Ϊfalse
     */
    public void resetRefreshing(){
    	isRefreshing = false;
    }
    
    /**
     * �����Ƿ����ڼ��ظ����״̬Ϊfalse
     */
    public void resetLoading(){
    	isLoading = false;
    }
    
    /**
     * �Ƿ���������ˢ��
     * @return
     */
    public boolean isRefreshable() {
		return isRefreshable;
	}

    /**
     * �����Ƿ���������ˢ��
     * @param isRefreshable
     */
	public void setRefreshable(boolean isRefreshable) {
		this.isRefreshable = isRefreshable;
	}

	/**
	 * �Ƿ�������������
	 * @return
	 */
	public boolean isLoadable() {
		return isLoadable;
	}

	/**
	 * �����Ƿ�������������
	 * @param isLoadable
	 */
	public void setLoadable(boolean isLoadable) {
		this.isLoadable = isLoadable;
	}

	/**
     * ǿ�������Ƿ�����ˢ�µ�״̬
     * @param isRefreshing
     */
    public void setRefreshing(boolean isRefreshing){
    	this.isRefreshing = isRefreshing;
    }
    
    /**
     * ǿ�������Ƿ����ڼ��ظ����״̬
     * @param isLoading
     */
    public void setLoading(boolean isLoading){
    	this.isLoading = isLoading;
    }
    
	@Override
	protected boolean isReadyForPullUp() {
		// TODO Auto-generated method stub
		return isLastItemVisible();
	}
	
	private boolean isLastItemVisible(){
		Adapter adapter = mRootView.getAdapter();
		if (null == adapter || adapter.isEmpty()) return true;
		
		int lastItemPosition = mRootView.getCount() - 1;
		int lastVisiblePosition = mRootView.getLastVisiblePosition();
		if (lastVisiblePosition >= lastItemPosition - 1) {
			final int childIndex = lastVisiblePosition - mRootView.getFirstVisiblePosition();
			final View lastVisibleChild = mRootView.getChildAt(childIndex);
			if (lastVisibleChild != null) {
				return lastVisibleChild.getBottom() <= mRootView.getBottom();
			}
		}
		return false;
	}

    @Override
    protected boolean isReadyForPullDown() {
        return isFirstItemVisible();
    }

    private boolean isFirstItemVisible() {
        final Adapter adapter = mRootView.getAdapter();

        if (null == adapter || adapter.isEmpty()) {
            return true;
        } else {
            /**
             * This check should really just be:
             * mRootView.getFirstVisiblePosition() == 0, but PtRListView
             * internally use a HeaderView which messes the positions up. For
             * now we'll just add one to account for it and rely on the inner
             * condition which checks getTop().
             */
            if (mRootView.getFirstVisiblePosition() <= 1) {
                final View firstVisibleChild = mRootView.getChildAt(0);
                if (firstVisibleChild != null) {
                    return firstVisibleChild.getTop() >= mRootView.getTop();
                }
            }
        }

        return false;
    }

    @Override
    public void handleStyledAttributes(TypedArray a) {
    	
    	isRefreshable = a.getBoolean(R.styleable.PullToZoomView_isRefreshable, true);
        
        isLoadable = a.getBoolean(R.styleable.PullToZoomView_isLoadable, true);
        
        mHeaderContainer = new FrameLayout(getContext());
        if (mZoomView != null) {
            mHeaderContainer.addView(mZoomView);
        }
        if (mHeaderView != null) {
            mHeaderContainer.addView(mHeaderView);
        }

        mRootView.addHeaderView(mHeaderContainer);
    }

    /**
     * ����HeaderView�߶�
     *
     * @param width  ��
     * @param height ��
     */
    public void setHeaderViewSize(int width, int height) {
        if (mHeaderContainer != null) {
            Object localObject = mHeaderContainer.getLayoutParams();
            if (localObject == null) {
                localObject = new AbsListView.LayoutParams(width, height);
            }
            ((ViewGroup.LayoutParams) localObject).width = width;
            ((ViewGroup.LayoutParams) localObject).height = height;
            mHeaderContainer.setLayoutParams((ViewGroup.LayoutParams) localObject);
            mHeaderHeight = height;
        }
    }

    public void setHeaderLayoutParams(AbsListView.LayoutParams layoutParams) {
        if (mHeaderContainer != null) {
            mHeaderContainer.setLayoutParams(layoutParams);
            mHeaderHeight = layoutParams.height;
        }
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2,
                            int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        Log.d(TAG, "onLayout --> ");
        if (mHeaderHeight == 0 && mHeaderContainer != null) {
            mHeaderHeight = mHeaderContainer.getHeight();
        }
    }

    @Override
	protected void onZoomRelease(float initMotionY, float lastMotionY) {
		//�����ͷ�ʱ���ж��Ƿ�����ˢ�£��������������ﵽ1/4 HeaderView�ĸ߶�)
        if(isRefreshable && !isRefreshing 
        		&& Math.abs(lastMotionY - initMotionY) >= (mHeaderHeight/4)){
        	if (onPullZoomListener != null) {
                onPullZoomListener.onRefresh();
                isRefreshing = true;
            }
        }
	}
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        Log.d(TAG, "onScrollStateChanged --> ");
        if(scrollState == SCROLL_STATE_IDLE){
        	//�ж��Ƿ��������ظ���
        	if(isLoadable && !isLoading && lastY - initY < 0 && isReadyForPullUp()){
				if (onPullZoomListener != null) {
                    onPullZoomListener.onLoad();
                    isLoading = true;
                }
			}
        	
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mZoomView != null && !isHideHeader() && isPullToZoomEnabled()) {
            float f = mHeaderHeight - mHeaderContainer.getBottom();
            if (isParallax()) {
                if ((f > 0.0F) && (f < mHeaderHeight)) {
                    int i = (int) (0.65D * f);
                    mHeaderContainer.scrollTo(0, -i);
                } else if (mHeaderContainer.getScrollY() != 0) {
                    mHeaderContainer.scrollTo(0, 0);
                }
            }
        }
    }
    
    @Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		if(!isLoadable || isLoading) return false;
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			initX = lastX = event.getX();
			initY = lastY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			lastX = event.getX();
			lastY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		default:
			break;
		}
		
		return false;
	}

    /**
     * ���û�����������Ļص��ӿ�
     */
    public void setOnPullZoomListener(OnPullZoomListener onPullZoomListener){
    	super.onPullZoomListener = onPullZoomListener;
    }
    
    class ScalingRunnable implements Runnable {
        protected long mDuration;
        protected boolean mIsFinished = true;
        protected float mScale;
        protected long mStartTime;

        ScalingRunnable() {
        }

        public void abortAnimation() {
            mIsFinished = true;
        }

        public boolean isFinished() {
            return mIsFinished;
        }

        public void run() {
            if (mZoomView != null) {
                float f2;
                ViewGroup.LayoutParams localLayoutParams;
                if ((!mIsFinished) && (mScale > 1.0D)) {
                    float f1 = ((float) SystemClock.currentThreadTimeMillis() - (float) mStartTime) / (float) mDuration;
                    f2 = mScale - (mScale - 1.0F) * PullToZoomListView.sInterpolator.getInterpolation(f1);
                    localLayoutParams = mHeaderContainer.getLayoutParams();
                    Log.d(TAG, "ScalingRunnable --> f2 = " + f2);
                    if (f2 > 1.0F) {
                        localLayoutParams.height = ((int) (f2 * mHeaderHeight));
                        mHeaderContainer.setLayoutParams(localLayoutParams);
                        post(this);
                        return;
                    }
                    mIsFinished = true;
                }
            }
        }

        public void startAnimation(long paramLong) {
            if (mZoomView != null) {
                mStartTime = SystemClock.currentThreadTimeMillis();
                mDuration = paramLong;
                mScale = ((float) (mHeaderContainer.getBottom()) / mHeaderHeight);
                mIsFinished = false;
                post(this);
            }
        }
    }

}
