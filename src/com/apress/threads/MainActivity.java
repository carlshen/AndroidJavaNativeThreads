package com.apress.threads;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Main activity.
 *
 * @author carl shen
 */
public class MainActivity extends Activity {
	/** Threads edit. */
	private EditText threadsEdit;
	
	/** Iterations edit. */
	private EditText iterationsEdit;
	
	/** Start java threads button. */
	private Button startButton;
	/** Start native threads button. */
	private Button startNativeButton;
	
	/** Log view. */
	private TextView logView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initialize the native code
		nativeInit();
		
		threadsEdit = (EditText) findViewById(R.id.threads_edit);
		iterationsEdit = (EditText) findViewById(R.id.iterations_edit);
		startButton = (Button) findViewById(R.id.start_button);
		startNativeButton = (Button) findViewById(R.id.start_native);
		logView = (TextView) findViewById(R.id.log_view);
		
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				int threads = getNumber(threadsEdit, 0);
				int iterations = getNumber(iterationsEdit, 0);

				if (threads > 0 && iterations > 0) {
					javaThreads(threads, iterations);
				}				
			}			
		});
		startNativeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				int threads = getNumber(threadsEdit, 0);
				int iterations = getNumber(iterationsEdit, 0);

				if (threads > 0 && iterations > 0) {
					posixThreads(threads, iterations);
				}				
			}			
		});
	}

	@Override
	protected void onDestroy() {
		// Free the native resources
		nativeFree();
		
		super.onDestroy();
	}


	/**
	 * On native message callback.
	 * 
	 * @param message
	 *            native message.
	 */
	private void onNativeMessage(final String message) {
		runOnUiThread(new Runnable() {
			public void run() {
				logView.append(message);
				logView.append("\n");
			}
		});
	}
	
	/**
	 * Gets the value of edit text as integer. If the value
	 * is empty or count not be parsed, it returns the 
	 * default value.
	 * 
	 * @param editText edit text.
	 * @param defaultValue default value.
	 * @return numeric value.
	 */
	private static int getNumber(EditText editText, int defaultValue) {
		int value;
		
		try {
			value = Integer.parseInt(editText.getText().toString());
		} catch (NumberFormatException e) {
			value = defaultValue;
		}
		
		return value;
	}

	/**
	 * Starts the given number of threads for iterations.
	 * 
	 * @param threads thread count.
	 * @param iterations iteration count.
	 */
	private void startThreads(int threads, int iterations) {
		posixThreads(threads, iterations);
	}

	/**
	 * Initializes the native code.
	 */
	private native void nativeInit();
	
	/**
	 * Free the native resources.
	 */
	private native void nativeFree();
	
	/**
	 * Native worker.
	 * 
	 * @param id worker id.
	 * @param iterations iteration count.
	 */
	private native void nativeWorker(int id, int iterations);
	
	/**
	 * Using the POSIX threads.
	 * 
	 * @param threads thread count.
	 * @param iterations iteration count.
	 */
	private native void posixThreads(int threads, int iterations);
	
	/**
	 * Using Java based threads.
	 * 
	 * @param threads thread count.
	 * @param iterations iteration count.
	 */
	private void javaThreads(int threads, final int iterations) {
		// Create a Java based thread for each worker
		for (int i = 0; i < threads; i++) {
			final int id = i;
			
			Thread thread = new Thread() {
				public void run() {
					nativeWorker(id, iterations);
				}
			};
			
			thread.start();
		}
	}
	
	static {
		System.loadLibrary("Threads");
	}
}
