package fr.xgouchet.androidlib.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.mozilla.universalchardet.UniversalDetector;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * Misc file utilities
 * 
 * TODO code review
 * 
 */
public final class TextFileUtils {

	public static String readAssetFile(final Context ctx, final String asset) {

		BufferedReader input;
		final StringBuffer text = new StringBuffer();
		String result, line;

		try {
			input = new BufferedReader(new InputStreamReader(ctx.getAssets()
					.open(asset)));
			do {
				line = input.readLine();
				if (line != null) {
					text.append(line);
				}
			} while (line != null);
			input.close();
			result = text.toString();
		} catch (IOException e) {
			result = null;
		}
		return result;
	}

	/**
	 * @param file
	 *            a text file
	 * @return the encoding used by the file
	 */
	public static String getFileEncoding(final File file) {
		String encoding = null;
		final UniversalDetector detector = new UniversalDetector(null);
		final byte[] buf = new byte[1024];

		try {
			FileInputStream input;
			input = new FileInputStream(file);

			int len;
			do {
				len = input.read(buf);
				if (len > 0) {
					detector.handleData(buf, 0, len);
				}
			} while (len > 0 && !detector.isDone());
			detector.dataEnd();

			encoding = detector.getDetectedCharset();

			detector.reset();
			input.close();
		} catch (IOException e) {
			Log.w(TextFileUtils.class.getName(),
					"IO exception while detecting encoding");
		}

		return encoding;
	}

	/**
	 * @param path
	 *            the absolute path to the file to save
	 * @param text
	 *            the text to write
	 * @return if the file was saved successfully
	 */
	public static boolean writeTextFile(final String path, final String text,
			final String encoding) {
		final File file = new File(path);
		OutputStreamWriter writer;
		BufferedWriter out = null;
		String enc = encoding;
		if (TextUtils.isEmpty(enc)) {
			enc = "UTF-8";
		}

		boolean result;
		try {

			writer = new OutputStreamWriter(new FileOutputStream(file), enc);
			out = new BufferedWriter(writer);
			out.write(text);
			out.flush();

			result = true;
		} catch (OutOfMemoryError e) {
			Log.w(TextFileUtils.class.getName(), "Out of memory error", e);
			result = false;
		} catch (IOException e) {
			Log.w(TextFileUtils.class.getName(), "Can't write to file " + path,
					e);
			result = false;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	/**
	 * @param file
	 *            the file to read
	 * @return the content of the file as text
	 */
	public static String readTextFile(final File file, final String encoding) {
		InputStreamReader reader;
		BufferedReader input;
		final StringBuffer text = new StringBuffer();
		String line;
		try {
			reader = new InputStreamReader(new FileInputStream(file), encoding);
			input = new BufferedReader(reader);
			do {
				line = input.readLine();
				if (line != null) {
					text.append(line);
				}
			} while (line != null);
			input.close();
		} catch (IOException e) {
			Log.w(TextFileUtils.class.getName(),
					"Can't read file " + file.getName(), e);
		} catch (OutOfMemoryError e) {
			Log.w(TextFileUtils.class.getName(), "File is to big to read", e);
		}

		return text.toString();
	}

	/**
	 * Detect the end of lines
	 * 
	 * @param content
	 *            the current content
	 * @return the end of lines used
	 */
	public static String detectEOL(final String content) {
		final int windows = content.indexOf("\r\n");
		final int macos = content.indexOf('\r');
		String eol;

		if (windows != -1) {
			eol = "\r\n";
		} else {
			if (macos != -1) {
				eol = "\r";
			} else {
				eol = "\n";
			}
		}
		return eol;
	}

	/**
	 * @param context
	 *            the current context
	 * @param text
	 *            the text to write
	 * @return if the file was saved successfully
	 */
	public static boolean writeInternal(final Context context,
			final String text, final String name) {
		FileOutputStream fos;
		boolean result;

		try {
			fos = context.openFileOutput(name, Context.MODE_PRIVATE);
			fos.write(text.getBytes());
			fos.close();
			result = true;
		} catch (FileNotFoundException e) {
			Log.w(TextFileUtils.class.getName(),
					"Couldn't write to internal storage ", e);
			result = false;
		} catch (IOException e) {
			Log.w(TextFileUtils.class.getName(),
					"Error occured while writing to internal storage ", e);
			result = false;
		}
		return result;
	}

	/**
	 * @param context
	 *            the current context
	 * @return the content of the file as text
	 */
	public static String readInternal(final Context context, final String name) {
		FileInputStream fis;
		final StringBuffer text = new StringBuffer();
		String result = null;
		try {
			fis = context.openFileInput(name);
			while (fis.available() > 0) {
				text.append((char) fis.read());
			}
			result = text.toString();
		} catch (FileNotFoundException e) {
			Log.w(TextFileUtils.class.getName(), "No backup file available", e);
		} catch (IOException e) {
			Log.w(TextFileUtils.class.getName(), "Can't read backup file ", e);
		}

		return result;
	}

	private TextFileUtils() {
	}
}
