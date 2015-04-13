package cn.fanfan.common;

import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

/* ���ܣ��첽����ͼƬ 
 * 
 * 1������Ȩ�ޣ�SD��дȨ�ޣ�
 *  <uses-permission android:name="android.permission.INTERNET"/>
 <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 AsyncImageGet��һ����������Ҫ��ȡͼƬ�ĵ�ַ���ڶ���������ͼƬ���������Ҫ����ImageView
 */
/*eg:
 AsyncImageGet asyncImageGet = new AsyncImageGet(url, myImageView);
 asyncImageGet.execute();
 */
public class AsyncImageGet extends AsyncTask<String, Integer, Bitmap> {

	private ImageView imageView;
	private String url;
	private Bitmap bitmap;

	public AsyncImageGet(String url, ImageView imageView) {
		// TODO Auto-generated constructor stub
		this.imageView = imageView;
		this.url = url;
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		// TODO Auto-generated method stub
		ImageGet imageGet = new ImageGet();
		Bitmap bitmap = imageGet.LoadPictureFromUrl(url);
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		bitmap = result;
		imageView.setImageBitmap(result);
		super.onPostExecute(result);
	}
	
	public void SaveBitmap(Context context){
		if (bitmap != null) {
			ImageFileUtils fileUtils = new ImageFileUtils(context);
			try {
				fileUtils.saveBitmap(url, bitmap);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
