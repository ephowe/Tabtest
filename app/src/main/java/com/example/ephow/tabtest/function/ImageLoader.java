package com.example.ephow.tabtest.function;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.ImageView;

import com.example.ephow.tabtest.food.BaseCmenu;
import com.example.ephow.tabtest.food.CmenuSqlite;
import com.example.ephow.tabtest.R;

import java.io.File;
import java.io.FileInputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ephow on 2015/10/4.
 */

//带两级缓存的图片加载器

public class ImageLoader implements BaseCmenu{
    private static final String TAG = "ImageLoader";
    //private static final int MAX_CAPACITY = 10;// 一级缓存的最大空间
    private static final long DELAY_BEFORE_PURGE = 10 * 1000;// 定时清理缓存
    private String Path;
    private HashMap<String, Bitmap> mFirstLevelCache;
    private ConcurrentHashMap<String, SoftReference<Bitmap>> mSecondLevelCache;

    // 定时清理缓存
    private Runnable mClearCache = new Runnable() {
        @Override
        public void run() {
            clear();
        }
    };
    private Handler mPurgeHandler = new Handler();

    ////////////////
    public ImageLoader(final int cacheSize,String path) {
        Path = path;
        // 0.75是加载因子为经验值，true则表示按照最近访问量的高低排序，false则表示按照插入顺序排序
        mFirstLevelCache = new LinkedHashMap<String, Bitmap>( cacheSize >> 1  , 0.75f, true) {
            private static final long serialVersionUID = 1L;
            protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
                if (size() > cacheSize ) {// 当超过一级缓存阈值的时候，将老的值从一级缓存搬到二级缓存
                    mSecondLevelCache.put(eldest.getKey(), new SoftReference<>(eldest.getValue()));
                    return true;
                }
                return false;
            }
        };
        // 二级缓存，采用的是软应用，只有在内存吃紧的时候软应用才会被回收，有效的避免了oom
        mSecondLevelCache = new ConcurrentHashMap<>( cacheSize >> 1 );
    }

    // 重置缓存清理的timer
    private void resetPurgeTimer() {
        mPurgeHandler.removeCallbacks(mClearCache);
        mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
    }

    /**
     * 清理缓存
     */
    private void clear() {
        mFirstLevelCache.clear();
        mSecondLevelCache.clear();
    }

    /**
     * 返回缓存，如果没有则返回null
     * @param picId
     * @return
     */
    private Bitmap getBitmapFromCache(String picId) {
        Bitmap bitmap = null;
        bitmap = getFromFirstLevelCache(picId);// 从一级缓存中拿
        return bitmap != null ? bitmap : getFromSecondLevelCache(picId);  //从二级缓存中拿
    }

    /**
     * 从二级缓存中拿
     *
     * @param picId
     * @return
     */
    private Bitmap getFromSecondLevelCache(String picId) {
        Bitmap bitmap = null;
        SoftReference<Bitmap> softReference = mSecondLevelCache.get(picId);
        if (softReference != null) {
            bitmap = softReference.get();
            // 由于内存吃紧，软引用已经被gc回收了
            if (bitmap == null)  mSecondLevelCache.remove(picId);
        }
        return bitmap;
    }

    /**
     * 从一级缓存中拿
     *
     * @param picId
     * @return
     */
    private Bitmap getFromFirstLevelCache(String picId) {
        Bitmap bitmap = null;
        synchronized (mFirstLevelCache) {
            bitmap = mFirstLevelCache.get(picId);
            // 将最近访问的元素放到链的头部，提高下一次访问该元素的检索速度（LRU算法）
            if (bitmap != null) {
                mFirstLevelCache.remove(picId);
                mFirstLevelCache.put(picId, bitmap);
            }
        }
        return bitmap;
    }

    /**
     * 放入缓存
     *
     * @param picId
     * @param value
     */
    private void AddImage2Cache(String picId, Bitmap value) {
        if (value == null || picId == null) {
            return;
        }
        synchronized (mFirstLevelCache) {
            mFirstLevelCache.put(picId, value);
        }
    }

    /**
     * 从设备内存中读取图片,由ImageLoadTask调用
     * @param picId
     * @param isStream 流式读取图片还是直接读取图片
     */

    private Bitmap loadingImageFromSD(String picId, boolean isStream) {
        Bitmap bitmap = null;
        try {
            //加载图片
            BitmapFactory.Options opt = new  BitmapFactory.Options();
            opt.inPreferredConfig = Bitmap.Config.RGB_565; //ARGB_4444;//

            bitmap = ( isStream
                    ? BitmapFactory.decodeStream(new FileInputStream(
                    new File(Path + picId)), null, opt)
                    : BitmapFactory.decodeFile(Path + picId, opt) );
        }catch (Exception e) {
            //System.out.println(e.getMessage());
            e.printStackTrace();

        }
        return bitmap;
    }


    /**
     * 加载图片，如果缓存中有就直接从缓存中拿，缓存中没有就下载
     * @param iv
     * @param picId
     */
    public void loadImage(ImageView iv, String picId) {
        //熏置计数器
        resetPurgeTimer();
        //为了ontouch iv，暂时将绑定移到adapter中执行,并直接绑定对应food
        //将图片与对应imageview绑定
        //通用型
        //iv.setTag(picId);
        // 从缓存中读取
        Bitmap bitmap = getBitmapFromCache(picId);
        if (bitmap == null) {
            //缓存中没有对应图片,设置预加载临时图片
            iv.setImageResource(R.drawable.loadimg);
            ImageLoadTask imageLoadTask = new ImageLoadTask();
            /////////////
            imageLoadTask.execute(iv, picId);
        } else iv.setImageBitmap(bitmap); //设为缓存图片
    }

    class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
        String picId;
        ImageView iv;
        //InterfaceShell shell;
        @Override
        protected Bitmap doInBackground(Object... params) {
            iv = (ImageView)params[0];
            picId = (String)params[1];
            //params.length
            //shell = (InterfaceShell)params[2];
            Bitmap drawable = loadingImageFromSD(picId, true);// 获取图片
            return drawable;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            if (result == null) {
                return;
            }
            AddImage2Cache(picId, result);// 放入缓存
            //UI动作
            //////////
            //可以尝试通过接口(纯虚函数),来处理控件等UI设置
            //shell.onShell(iv, picId, result);
            //////
            //检查IV是否显示对应的大小图片，避免出现图片加载错位
            Food food = (Food)iv.getTag();
            if (food != null && (food.PicsmallId.equals(picId) || food.PicbigId.equals(picId)) )
                iv.setImageBitmap(result);
            //通用型
            //检查imageview是否应该显示对应图片，避免出现图片加载错位
            //if ( iv.getTag() != null && iv.getTag().equals(picId) ) iv.setImageBitmap(result);
        }
    }

    public interface InterfaceShell {
        public void onShell(ImageView iv, String picId, Bitmap result);
    }

}
