package com.IO.youdictionary;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.*;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.appbar.AppBarLayout;
import android.app.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.webkit.*;
import android.animation.*;
import android.view.animation.*;
import java.util.*;
import java.util.regex.*;
import java.text.*;
import org.json.*;
import java.util.HashMap;
import java.util.ArrayList;
import android.widget.LinearLayout;
import android.widget.HorizontalScrollView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import com.google.android.material.textfield.*;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.text.Editable;
import android.text.TextWatcher;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;


public class DcEditActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	private double cur_pos = 0;
	private String filter = "";
	private boolean auto_search = false;
	private HashMap<String, Object> props = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> dc = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> words = new ArrayList<>();
	
	private LinearLayout lin1;
	private LinearLayout add_lin;
	private HorizontalScrollView edit_scroll;
	private LinearLayout props_lin;
	private LinearLayout search_lin;
	private SwipeRefreshLayout swiperefreshlayout1;
	private Button add_word;
	private EditText search_edit;
	private Button search_but;
	private ListView wordlist;
	private LinearLayout add_lin2;
	private Button create_but;
	private EditText orig;
	private EditText trans;
	private LinearLayout edit_lin2;
	private EditText edit;
	private EditText edit2;
	private Button aply_edit_but;
	private TextInputLayout textinputlayout1;
	private Button props_but;
	private EditText props_edit;
	
	private AlertDialog.Builder dial;
	private Calendar day = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.dc_edit);
		initialize(_savedInstanceState);
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
		|| ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1000);
		}
		else {
			initializeLogic();
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == 1000) {
			initializeLogic();
		}
	}
	
	private void initialize(Bundle _savedInstanceState) {
		_app_bar = (AppBarLayout) findViewById(R.id._app_bar);
		_coordinator = (CoordinatorLayout) findViewById(R.id._coordinator);
		_toolbar = (Toolbar) findViewById(R.id._toolbar);
		setSupportActionBar(_toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _v) {
				onBackPressed();
			}
		});
		lin1 = (LinearLayout) findViewById(R.id.lin1);
		add_lin = (LinearLayout) findViewById(R.id.add_lin);
		edit_scroll = (HorizontalScrollView) findViewById(R.id.edit_scroll);
		props_lin = (LinearLayout) findViewById(R.id.props_lin);
		search_lin = (LinearLayout) findViewById(R.id.search_lin);
		swiperefreshlayout1 = (SwipeRefreshLayout) findViewById(R.id.swiperefreshlayout1);
		add_word = (Button) findViewById(R.id.add_word);
		search_edit = (EditText) findViewById(R.id.search_edit);
		search_but = (Button) findViewById(R.id.search_but);
		wordlist = (ListView) findViewById(R.id.wordlist);
		add_lin2 = (LinearLayout) findViewById(R.id.add_lin2);
		create_but = (Button) findViewById(R.id.create_but);
		orig = (EditText) findViewById(R.id.orig);
		trans = (EditText) findViewById(R.id.trans);
		edit_lin2 = (LinearLayout) findViewById(R.id.edit_lin2);
		edit = (EditText) findViewById(R.id.edit);
		edit2 = (EditText) findViewById(R.id.edit2);
		aply_edit_but = (Button) findViewById(R.id.aply_edit_but);
		textinputlayout1 = (TextInputLayout) findViewById(R.id.textinputlayout1);
		props_but = (Button) findViewById(R.id.props_but);
		props_edit = (EditText) findViewById(R.id.props_edit);
		dial = new AlertDialog.Builder(this);
		
		swiperefreshlayout1.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override 
			public void onRefresh() {
				filter = "";
				cur_pos = 0;
				_get_props();
				((BaseAdapter)wordlist.getAdapter()).notifyDataSetChanged();
				swiperefreshlayout1.setRefreshing(false);
			}
		});
		
		add_word.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				lin1.setVisibility(View.GONE);
				add_lin.setVisibility(View.VISIBLE);
			}
		});
		
		search_edit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				final String _charSeq = _param1.toString();
				if (props.get("as").toString().equals("1")) {
					_search();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence _param1, int _param2, int _param3, int _param4) {
				
			}
			
			@Override
			public void afterTextChanged(Editable _param1) {
				
			}
		});
		
		search_but.setOnLongClickListener(new View.OnLongClickListener() {
			 @Override
				public boolean onLongClick(View _view) {
				dial.setTitle("Search mods");
				dial.setMessage("Select searching mode");
				dial.setPositiveButton("Search in original", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						props.put("sm", "1");
						_write(new Gson().toJson(props), "props.data");
					}
				});
				dial.setNegativeButton("Search in translation", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						props.put("sm", "2");
						_write(new Gson().toJson(props), "props.data");
					}
				});
				dial.setNeutralButton("More", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						dial.setTitle("Search mods");
						dial.setMessage("More modes");
						dial.setPositiveButton("Search in properties", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface _dialog, int _which) {
								props.put("sm", "3");
								_write(new Gson().toJson(props), "props.data");
							}
						});
						dial.setNegativeButton("Auto search", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface _dialog, int _which) {
								if (props.get("as").toString().equals("0")) {
									props.put("as", "1");
									SketchwareUtil.showMessage(getApplicationContext(), "Enabled");
								}
								else {
									props.put("as", "0");
									SketchwareUtil.showMessage(getApplicationContext(), "Disabled");
								}
								_write(new Gson().toJson(props), "props.data");
							}
						});
						dial.setNeutralButton("Search in all", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface _dialog, int _which) {
								props.put("sm", "5");
								_write(new Gson().toJson(props), "props.data");
							}
						});
						dial.create().show();
					}
				});
				dial.create().show();
				return true;
				}
			 });
		
		search_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				_search();
				SketchwareUtil.hideKeyboard(getApplicationContext());
			}
		});
		
		wordlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				dial.setTitle(words.get((int)_position).get("orig").toString());
				dial.setMessage(words.get((int)_position).get("props").toString());
				dial.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						
					}
				});
				dial.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						
					}
				});
				dial.setNeutralButton("Neutral", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						
					}
				});
				dial.create().show();
			}
		});
		
		wordlist.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> _param1, View _param2, int _param3, long _param4) {
				final int _position = _param3;
				cur_pos = _position;
				dial.setTitle(words.get((int)_position).get("orig").toString());
				dial.setMessage(words.get((int)_position).get("props").toString());
				dial.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						words.remove((int)(_position));
						String prp = dc.get(Integer.valueOf(getIntent().getStringExtra("pos"))).get("props").toString();
						Integer n = Integer.valueOf(prp.split(" ")[1].split(":")[1]) - 1;
						prp = prp.split(":")[0] + ":" + n.toString();
						dc.get(Integer.valueOf(getIntent().getStringExtra("pos"))).put("props", prp);
						_save(new Gson().toJson(words), FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
						_update();
					}
				});
				dial.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						lin1.setVisibility(View.GONE);
						add_lin.setVisibility(View.GONE);
						edit_scroll.setVisibility(View.VISIBLE);
						props_lin.setVisibility(View.GONE);
						edit.setText(words.get((int)_position).get("orig").toString());
						edit2.setText(words.get((int)_position).get("trans").toString());
					}
				});
				dial.setNeutralButton("Add properties", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface _dialog, int _which) {
						lin1.setVisibility(View.GONE);
						add_lin.setVisibility(View.GONE);
						edit_scroll.setVisibility(View.GONE);
						props_lin.setVisibility(View.VISIBLE);
						props_edit.setText(words.get((int)_position).get("props").toString());
					}
				});
				dial.create().show();
				return true;
			}
		});
		
		create_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (!(orig.getText().toString().equals("") && trans.getText().toString().equals(""))) {
					String o = orig.getText().toString();
					String t = trans.getText().toString();
					Boolean cont = false;
					for(int i = 0; i < words.size(); i++)
					{
							if(words.get(i).get("orig").toString().equals(o))
							{
									cont = true;
									break;
							}
					}
					if (cont) {
						SketchwareUtil.showMessage(getApplicationContext(), "Word is already exists");
					}
					else {
						{
								HashMap<String, Object> _item = new HashMap<>();
								_item.put("orig", o);
								_item.put("trans", t);
								_item.put("props", "-");
								words.add(_item);
						}
						
						String prp = dc.get(Integer.valueOf(getIntent().getStringExtra("pos"))).get("props").toString();
						Integer n = Integer.valueOf(prp.split(" ")[1].split(":")[1]) + 1;
						prp = prp.split(":")[0] + ":" + n.toString();
						dc.get(Integer.valueOf(getIntent().getStringExtra("pos"))).put("props", prp);
						SketchwareUtil.sortListMap(words, "orig", false, true);
						for(int i = 0; i < words.size(); i++)
						{
								if(words.get(i).get("orig").toString().equals(o))
								{
										cur_pos = i;
										break;
								}
						}
						_save(new Gson().toJson(words), FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
						_update();
					}
				}
				else {
					SketchwareUtil.showMessage(getApplicationContext(), "Enter word at first");
				}
				lin1.setVisibility(View.VISIBLE);
				add_lin.setVisibility(View.GONE);
			}
		});
		
		aply_edit_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (!(edit.getText().toString().equals("") && edit2.getText().toString().equals(""))) {
					Boolean cont = false;
					for(int i = 0; i < words.size(); i++)
					{
							if(words.get(i).get("orig").toString().equals(edit.getText().toString()))
							{
									cont = true;
									break;
							}
					}
					if (cont) {
						SketchwareUtil.showMessage(getApplicationContext(), "Word is already exists");
					}
					else {
						words.get((int)cur_pos).put("orig", edit.getText().toString());
						words.get((int)cur_pos).put("trans", edit2.getText().toString());
						SketchwareUtil.sortListMap(words, "orig", false, true);
						_save(new Gson().toJson(words), FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
						_update();
					}
				}
				else {
					SketchwareUtil.showMessage(getApplicationContext(), "Enter word firstly");
				}
			}
		});
		
		props_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (props_edit.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), "This field must be filed");
				}
				else {
					words.get((int)cur_pos).put("props", props_edit.getText().toString());
					_save(new Gson().toJson(words), FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
					_update();
				}
			}
		});
	}
	
	private void initializeLogic() {
		try {
			cur_pos = 0;
			_update();
		} catch(Exception e) {
			dial.setTitle("Error occurred");
			dial.setMessage(e.toString());
			dial.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface _dialog, int _which) {
					
				}
			});
			dial.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface _dialog, int _which) {
					
				}
			});
			dial.setNegativeButton("Stop", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface _dialog, int _which) {
					finish();
				}
			});
			dial.create().show();
		}
	}
	
	@Override
	protected void onActivityResult(int _requestCode, int _resultCode, Intent _data) {
		super.onActivityResult(_requestCode, _resultCode, _data);
		switch (_requestCode) {
			
			default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	public void _get_data (final String _path) {
		if (FileUtil.isExistFile(_path)) {
			dc = new Gson().fromJson(FileUtil.readFile(_path), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
			words = new Gson().fromJson(dc.get((int)Double.parseDouble(getIntent().getStringExtra("pos"))).get("data").toString(), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
		}
		else {
			
		}
	}
	
	
	public void _save (final String _value, final String _path) {
		{
				HashMap<String, Object> _item = dc.get(Integer.valueOf(getIntent().getStringExtra("pos")));
				_item.put("data", _value);
				dc.set(Integer.valueOf(getIntent().getStringExtra("pos")), _item);
		}
		day = Calendar.getInstance();
		String f = FileUtil.readFile(_path);
		String f2 = _value;
		Boolean eql = f2.equals(f);
		if (!eql && FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"))) {
			FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext()).concat("/temp/").concat(new SimpleDateFormat("dd_MM_yyyy").format(day.getTime()).concat("/")));
			FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/temp/").concat(new SimpleDateFormat("dd_MM_yyyy").format(day.getTime()).concat("/ydc_")).concat(new SimpleDateFormat("HH_mm").format(day.getTime()).concat(".tmp")), FileUtil.readFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data")));
		}
		else {
			FileUtil.makeDir(FileUtil.getPackageDataDir(getApplicationContext()).concat("/temp/"));
		}
		FileUtil.writeFile(_path, new Gson().toJson(dc));
	}
	
	
	public void _update () {
		setTitle(getIntent().getStringExtra("nm"));
		SketchwareUtil.hideKeyboard(getApplicationContext());
		lin1.setVisibility(View.VISIBLE);
		add_lin.setVisibility(View.GONE);
		edit_scroll.setVisibility(View.GONE);
		props_lin.setVisibility(View.GONE);
		orig.setText("");
		trans.setText("");
		_get_data(FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
		_get_props();
		wordlist.setAdapter(new WordlistAdapter(words));
		wordlist.setSelection((int)cur_pos);
	}
	
	
	public void _search () {
		if (search_edit.getText().toString().equals("")) {
			filter = "";
		}
		else {
			filter = search_edit.getText().toString();
		}
		((BaseAdapter)wordlist.getAdapter()).notifyDataSetChanged();
	}
	
	
	public void _write (final String _data, final String _path) {
		FileUtil.writeFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/".concat(_path)), _data);
	}
	
	
	public void _get_props () {
		if (FileUtil.isExistFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/props.data"))) {
			props = new Gson().fromJson(FileUtil.readFile(FileUtil.getPackageDataDir(getApplicationContext()).concat("/props.data")), new TypeToken<HashMap<String, Object>>(){}.getType());
		}
		else {
			props = new HashMap<>();
			props.put("sm", "1");
			props.put("as", "0");
		}
	}
	
	
	public class WordlistAdapter extends BaseAdapter {
		ArrayList<HashMap<String, Object>> _data;
		public WordlistAdapter(ArrayList<HashMap<String, Object>> _arr) {
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public HashMap<String, Object> getItem(int _index) {
			return _data.get(_index);
		}
		
		@Override
		public long getItemId(int _index) {
			return _index;
		}
		@Override
		public View getView(final int _position, View _v, ViewGroup _container) {
			LayoutInflater _inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View _view = _v;
			if (_view == null) {
				_view = _inflater.inflate(R.layout.word, null);
			}
			
			final LinearLayout linear1 = (LinearLayout) _view.findViewById(R.id.linear1);
			final TextView original = (TextView) _view.findViewById(R.id.original);
			final TextView transition = (TextView) _view.findViewById(R.id.transition);
			
			if (filter.equals("")) {
				linear1.setVisibility(View.VISIBLE);
				original.setText(_data.get((int)_position).get("orig").toString());
				transition.setText(_data.get((int)_position).get("trans").toString());
				if (_position == 0) {
					linear1.setBackgroundColor(0xFFB2DFDB);
				}
				else {
					if (_data.get((int)_position).get("orig").toString().substring((int)(0), (int)(1)).equals(_data.get((int)_position - 1).get("orig").toString().substring((int)(0), (int)(1)))) {
						linear1.setBackgroundColor(0xFF4DB6AC);
					}
					else {
						linear1.setBackgroundColor(0xFFB2DFDB);
					}
				}
			}
			else {
				original.setText(_data.get((int)_position).get("orig").toString());
				transition.setText(_data.get((int)_position).get("trans").toString());
				if (props.get("sm").toString().equals("1")) {
					if (_data.get((int)_position).get("orig").toString().contains(filter)) {
						linear1.setVisibility(View.VISIBLE);
					}
					else {
						linear1.setVisibility(View.GONE);
					}
				}
				if (props.get("sm").toString().equals("2")) {
					if (_data.get((int)_position).get("trans").toString().contains(filter)) {
						linear1.setVisibility(View.VISIBLE);
					}
					else {
						linear1.setVisibility(View.GONE);
					}
				}
				if (props.get("sm").toString().equals("3")) {
					if (_data.get((int)_position).get("props").toString().contains(filter)) {
						linear1.setVisibility(View.VISIBLE);
					}
					else {
						linear1.setVisibility(View.GONE);
					}
				}
				if (props.get("sm").toString().equals("5")) {
					if (_data.get((int)_position).get("orig").toString().contains(filter) || (_data.get((int)_position).get("trans").toString().contains(filter) || _data.get((int)_position).get("props").toString().contains(filter))) {
						linear1.setVisibility(View.VISIBLE);
					}
					else {
						linear1.setVisibility(View.GONE);
					}
				}
			}
			
			return _view;
		}
	}
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}