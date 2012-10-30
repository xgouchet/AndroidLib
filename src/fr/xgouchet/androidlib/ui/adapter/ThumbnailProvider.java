package fr.xgouchet.androidlib.ui.adapter;

import java.io.File;

import android.content.Context;
import android.graphics.drawable.Drawable;

public interface ThumbnailProvider {

	public Drawable getThumbnailForFile(Context context, File file);
}
