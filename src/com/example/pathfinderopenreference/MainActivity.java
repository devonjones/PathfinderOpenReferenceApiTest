package com.example.pathfinderopenreference;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.evilsoft.pathfinder.reference.api.contracts.SectionContract;
import org.evilsoft.pathfinder.reference.api.contracts.SpellContract;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class MainActivity extends Activity {
	Button buttonClassList;
	Button buttonSpellList;
	Button buttonClassSpellList;
	Button buttonClassHtml;
	Button buttonSpellHtml;
	Button buttonSpellJson;
	Button buttonClassLaunch;
	Button buttonSpellLaunch;

	ContentResolver cr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		addListenerOnButtonClassList();
		addListenerOnButtonSpellList();
		addListenerOnButtonClassSpellList();
		addListenerOnButtonClassHtml();
		addListenerOnButtonSpellHtml();
		addListenerOnButtonSpellJson();
		addListenerOnButtonClassLaunch();
		addListenerOnButtonSpellLaunch();
		cr = this.getContentResolver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void setLog(String html) {
		String mime = "text/html";
		String encoding = "utf-8";

		WebView wv = (WebView) this.findViewById(R.id.logDisplay);
		wv.loadDataWithBaseURL(null, html, mime, encoding, null);
	}

	public void setLogJson(String json) {
		String mime = "application/json";
		String encoding = "utf-8";

		WebView wv = (WebView) this.findViewById(R.id.logDisplay);
		wv.loadDataWithBaseURL(null, json, mime, encoding, null);
	}

	public void addListenerOnButtonClassList() {
		buttonClassList = (Button) findViewById(R.id.buttonClassList);
		buttonClassList.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentProviderClient classListClient = cr
						.acquireContentProviderClient(SectionContract.AUTHORITY);
				try {
					String[] selectionArgs = new String[1];
					selectionArgs[0] = "class";
					Cursor curs = classListClient.query(
							SectionContract.SECTION_LIST_URI, null, "type = ?",
							selectionArgs, null);
					StringBuffer sb = new StringBuffer();
					sb.append("<H1>Class List</H1>");
					sb.append("<ul>");
					boolean hasNext = curs.moveToFirst();
					while (hasNext) {
						String name = curs.getString(4);
						String url = curs.getString(6);
						sb.append("<li>");
						sb.append(name + ": " + url);
						hasNext = curs.moveToNext();
						sb.append("</li>");
					}
					sb.append("</ul>");
					setLog(sb.toString());
				} catch (RemoteException e) {
					Log.e("ClassList", "Failed on load", e);
				}
			}
		});
	}

	public void addListenerOnButtonSpellList() {
		buttonSpellList = (Button) findViewById(R.id.buttonSpellList);
		buttonSpellList.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentProviderClient classListClient = cr
						.acquireContentProviderClient(SpellContract.AUTHORITY);
				try {
					Cursor curs = classListClient.query(
							SpellContract.SPELL_LIST_URI, null, null, null,
							null);
					StringBuffer sb = new StringBuffer();
					sb.append("<H1>Spell List</H1>");
					sb.append("<ul>");
					boolean hasNext = curs.moveToFirst();
					while (hasNext) {
						String name = curs.getString(4);
						String url = curs.getString(6);
						sb.append("<li>");
						sb.append(name + ": " + url);
						sb.append("</li>");
						hasNext = curs.moveToNext();
					}
					sb.append("</ul>");
					setLog(sb.toString());
				} catch (RemoteException e) {
					Log.e("SpellList", "Failed on load", e);
				}
			}
		});
	}

	public void addListenerOnButtonClassSpellList() {
		buttonClassSpellList = (Button) findViewById(R.id.buttonClassSpellList);
		buttonClassSpellList.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				ContentProviderClient spellListClient = cr
						.acquireContentProviderClient(SpellContract.AUTHORITY);
				try {
					String classId = getClassColumn("Sorcerer", 0);
					Uri uri = SpellContract.getClassSpellList(classId);
					Cursor curs = spellListClient.query(uri, null, null, null,
							null);
					StringBuffer sb = new StringBuffer();
					sb.append("<H1>Sorcerer Spell List</H1>");
					sb.append("<ul>");
					boolean hasNext = curs.moveToFirst();
					while (hasNext) {
						String name = curs.getString(4);
						String url = curs.getString(6);
						String level = curs.getString(8);
						sb.append("<li>");
						sb.append("(" + level + ") -" + name + ": " + url);
						sb.append("</li>");
						hasNext = curs.moveToNext();
					}
					sb.append("</ul>");
					setLog(sb.toString());
				} catch (RemoteException e) {
					Log.e("ClassSpellList", "Failed on load", e);
				}
			}
		});
	}

	public void addListenerOnButtonClassHtml() {
		buttonSpellHtml = (Button) findViewById(R.id.buttonClassHtml);
		final Context context = this.getApplicationContext();
		buttonSpellHtml.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					StringBuffer sb = new StringBuffer();
					String classId = getClassColumn("Sorcerer", 0);
					Uri uri = SectionContract.getSectionHtmlUri(classId);
					InputStream is = context.getContentResolver()
							.openInputStream(uri);
					int data = is.read();
					while (data != -1) {
						char theChar = (char) data;
						sb.append(theChar);
						data = is.read();
					}

					is.close();
					setLog(sb.toString());
				} catch (RemoteException e) {
					Log.e("GetClassHtml", "Failed on load", e);
				} catch (FileNotFoundException e) {
					Log.e("GetClassHtml", "Failed to find file", e);
				} catch (IOException e) {
					Log.e("GetClassHtml", "File read failed", e);
				}
			}
		});
	}

	public void addListenerOnButtonSpellHtml() {
		buttonSpellHtml = (Button) findViewById(R.id.buttonSpellHtml);
		final Context context = this.getApplicationContext();
		buttonSpellHtml.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					StringBuffer sb = new StringBuffer();
					String spellId = getSpellColumn("Teleport", 0);
					Uri uri = SpellContract.getSpellHtmlUri(spellId);
					InputStream is = context.getContentResolver()
							.openInputStream(uri);
					int data = is.read();
					while (data != -1) {
						char theChar = (char) data;
						sb.append(theChar);
						data = is.read();
					}

					is.close();
					setLog(sb.toString());
				} catch (RemoteException e) {
					Log.e("GetSpellHtml", "Failed on load", e);
				} catch (FileNotFoundException e) {
					Log.e("GetSpellHtml", "Failed to find file", e);
				} catch (IOException e) {
					Log.e("GetSpellHtml", "File read failed", e);
				}
			}
		});
	}

	public void addListenerOnButtonSpellJson() {
		buttonSpellJson = (Button) findViewById(R.id.buttonSpellJson);
		final Context context = this.getApplicationContext();
		buttonSpellJson.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					StringBuffer sb = new StringBuffer();
					String spellId = getSpellColumn("Teleport", 0);
					Uri uri = SpellContract.getSpellJsonUri(spellId);
					InputStream is = context.getContentResolver()
							.openInputStream(uri);
					int data = is.read();
					while (data != -1) {
						char theChar = (char) data;
						sb.append(theChar);
						data = is.read();
					}

					is.close();
					setLogJson(sb.toString());
				} catch (RemoteException e) {
					Log.e("GetSpellJson", "Failed on load", e);
				} catch (FileNotFoundException e) {
					Log.e("GetSpellJson", "Failed to find file", e);
				} catch (IOException e) {
					Log.e("GetSpellJson", "File read failed", e);
				}
			}
		});
	}

	public void addListenerOnButtonClassLaunch() {
		buttonClassLaunch = (Button) findViewById(R.id.buttonClassLaunch);
		buttonClassLaunch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					String url = getClassColumn("Ranger", 6);
					Intent intent = new Intent("android.intent.action.MAIN");
					intent.setComponent(ComponentName
							.unflattenFromString("org.evilsoft.pathfinder.reference/org.evilsoft.pathfinder.reference.DetailsActivity"));
					intent.setData(Uri.parse(url));
					intent.addCategory("android.intent.category.LAUNCHER");
					startActivity(intent);
				} catch (RemoteException e) {
					Log.e("LaunchClass", "Failed on search", e);
				}
			}
		});
	}

	public void addListenerOnButtonSpellLaunch() {
		buttonSpellLaunch = (Button) findViewById(R.id.buttonSpellLaunch);
		buttonSpellLaunch.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try {
					String url = getSpellColumn("Summon Monster I", 6);
					Intent intent = new Intent("android.intent.action.MAIN");
					intent.setComponent(ComponentName
							.unflattenFromString("org.evilsoft.pathfinder.reference/org.evilsoft.pathfinder.reference.DetailsActivity"));
					intent.setData(Uri.parse(url));
					intent.addCategory("android.intent.category.LAUNCHER");
					startActivity(intent);
				} catch (RemoteException e) {
					Log.e("LaunchSpell", "Failed on search", e);
				}
			}
		});
	}

	public String getClassColumn(String className, int column)
			throws RemoteException {
		ContentProviderClient classListClient = cr
				.acquireContentProviderClient(SectionContract.AUTHORITY);
		String[] selectionArgs = new String[2];
		selectionArgs[0] = "class";
		selectionArgs[1] = className;
		Cursor curs = classListClient.query(SectionContract.SECTION_LIST_URI,
				null, "type = ? AND name = ?", selectionArgs, null);
		boolean hasNext = curs.moveToFirst();
		if (hasNext) {
			return curs.getString(column);
		}
		return null;
	}

	public String getSpellColumn(String spellName, int column)
			throws RemoteException {
		ContentProviderClient spellListClient = cr
				.acquireContentProviderClient(SpellContract.AUTHORITY);
		String[] selectionArgs = new String[1];
		selectionArgs[0] = spellName;
		Cursor curs = spellListClient.query(SpellContract.SPELL_LIST_URI, null,
				"name = ?", selectionArgs, null);
		boolean hasNext = curs.moveToFirst();
		if (hasNext) {
			return curs.getString(column);
		}
		return null;
	}
}
