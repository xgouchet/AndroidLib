package fr.xgouchet.androidlib.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.util.Log;

public final class LogUtils {

	/** Does not insert a link to the source code */
	public static final int INSERT_NONE = 0;
	/** Insert the link at the beginning of the message */
	public static final int INSERT_FIRST = 1;
	/** Insert the link at the end of the message */
	public static final int INSERT_LAST = 2;

	private static int sInsertMode = INSERT_LAST;

	/**
	 * Send a {@link Log#VERBOSE} log message, linking to the line in the source
	 * code calling this method.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static final void v(final String tag, final String msg) {
		Log.v(tag, getLinkedMessage(msg));
	}

	/**
	 * Send a {@link Log#DEBUG} log message, linking to the line in the source
	 * code calling this method.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static final void d(final String tag, final String msg) {
		Log.v(tag, getLinkedMessage(msg));
	}

	/**
	 * Send a {@link Log#INFO} log message, linking to the line in the source
	 * code calling this method.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static final void i(final String tag, final String msg) {
		Log.v(tag, getLinkedMessage(msg));
	}

	/**
	 * Send a {@link Log#WARN} log message, linking to the line in the source
	 * code calling this method.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static final void w(final String tag, final String msg) {
		Log.v(tag, getLinkedMessage(msg));
	}

	/**
	 * Send a {@link Log#ERROR} log message, linking to the line in the source
	 * code calling this method.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static final void e(final String tag, final String msg) {
		Log.v(tag, getLinkedMessage(msg));
	}

	/**
	 * Low-level logging call.
	 * 
	 * @param priority
	 *            The priority/type of this log message
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 * @return The number of bytes written.
	 */
	public static final int println(final int priority, final String tag,
			final String msg) {
		return Log.println(priority, tag, getLinkedMessage(msg));
	}

	/**
	 * Send a {@link Log#VERBOSE} log message, linking to the line in the source
	 * code calling this method.
	 * 
	 * @param tag
	 *            Used to identify the source of a log message. It usually
	 *            identifies the class or activity where the log call occurs.
	 * @param msg
	 *            The message you would like logged.
	 */
	public static final void wtf(final String tag, final String msg) {
		Log.v(tag, getLinkedMessage(msg));
	}

	/**
	 * @param mode
	 *            how to insert the link in the log message (one of
	 *            {@link #INSERT_NONE}, {@link #INSERT_FIRST} or
	 *            {@link #INSERT_LAST}) Default is {@link #INSERT_LAST} and you
	 *            can call this method only once to setup the LogUtils
	 */
	public static void setInsertMode(final int mode) {
		sInsertMode = mode;
	}

	private static final String getLinkedMessage(final String msg) {
		String link;
		if (sInsertMode == INSERT_NONE) {
			link = msg;
		} else {

			StringBuilder builder = new StringBuilder();

			// 0 = native getThreadStackTrace()
			// 1 = Thread.getStackTrace()
			// 2 = LogUtils.getSourceLine()
			// 3 = LogUtils.LogX()
			// 4 = Caller
			StackTraceElement[] stack = Thread.currentThread().getStackTrace();

			if (sInsertMode == INSERT_FIRST) {
				buildLink(stack[3], builder);
				builder.append(' ');
			}

			builder.append(msg);

			if (sInsertMode == INSERT_LAST) {
				builder.append(' ');
				buildLink(stack[4], builder);
			}

			link = builder.toString();
		}
		return link;
	}

	private static void buildLink(final StackTraceElement element,
			final StringBuilder builder) {

		builder.append("at (");
		builder.append(element.getFileName());
		builder.append(':');
		builder.append(element.getLineNumber());
		builder.append(')');
	}

	private LogUtils() {
	}

	public static OutputStream getOutputStream(final String tag, final int level) {
		return new LogOutputStream(tag, level);
	}

	private static class LogOutputStream extends ByteArrayOutputStream {

		public LogOutputStream(final String tag, final int level) {
			mLevel = level;
			mTag = tag;
		}

		@Override
		public void flush() throws IOException {
			Log.println(mLevel, mTag, "Flush");

			String data = toString();
			String[] lines = data.split("[\\f\\r\\n]+");

			for (String line : lines) {
				Log.println(mLevel, mTag, line);
			}
		}

		private final String mTag;
		private final int mLevel;
	}
}
