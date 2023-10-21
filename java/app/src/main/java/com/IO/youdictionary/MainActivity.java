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
import java.util.ArrayList;
import java.util.HashMap;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager.OnPageChangeListener;
import androidx.viewpager.widget.ViewPager.OnAdapterChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.net.Uri;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.DialogFragment;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.pm.PackageManager;


public class MainActivity extends AppCompatActivity {
	
	private Toolbar _toolbar;
	private AppBarLayout _app_bar;
	private CoordinatorLayout _coordinator;
	
	private ArrayList<HashMap<String, Object>> dc = new ArrayList<>();
	
	private LinearLayout lin1;
	private LinearLayout add_lin;
	private TextView main_text;
	private ViewPager pager1;
	private Button add_but;
	private EditText add_edit;
	private Button create_but;
	
	private Intent trans = new Intent();
	private AlertDialog.Builder dial;
	private Calendar day = Calendar.getInstance();
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
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
		main_text = (TextView) findViewById(R.id.main_text);
		pager1 = (ViewPager) findViewById(R.id.pager1);
		add_but = (Button) findViewById(R.id.add_but);
		add_edit = (EditText) findViewById(R.id.add_edit);
		create_but = (Button) findViewById(R.id.create_but);
		dial = new AlertDialog.Builder(this);
		
		add_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				add_lin.setVisibility(View.VISIBLE);
				lin1.setVisibility(View.GONE);
			}
		});
		
		create_but.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (add_edit.getText().toString().equals("")) {
					SketchwareUtil.showMessage(getApplicationContext(), "Enter name firstly");
				}
				else {
					{
							HashMap<String, Object> _item = new HashMap<>();
							
							_item.put("nm", add_edit.getText().toString());
							
							_item.put("data", "[]");
							
							Date date = new Date();
							SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
							_item.put("props", formatter.format(date) + " Words:0");
							
							dc.add(_item);
					}
					_save(new Gson().toJson(dc), FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
					_update();
					lin1.setVisibility(View.VISIBLE);
					add_lin.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private void initializeLogic() {
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
	public void onStart() {
		super.onStart();
		try {
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
	public void _update () {
		lin1.setVisibility(View.VISIBLE);
		add_lin.setVisibility(View.GONE);
		add_edit.setText("");
		_get_data(FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
		pager1.setAdapter(new Pager1Adapter(dc));
	}
	
	
	public void _get_data (final String _path) {
		if (FileUtil.isExistFile(_path)) {
			dc = new Gson().fromJson(FileUtil.readFile(_path), new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
		}
		else {
			
		}
	}
	
	
	public void _save (final String _value, final String _path) {
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
		FileUtil.writeFile(_path, _value);
	}
	
	
	public class Pager1Adapter extends PagerAdapter {
		Context _context;
		ArrayList<HashMap<String, Object>> _data;
		public Pager1Adapter(Context _ctx, ArrayList<HashMap<String, Object>> _arr) {
			_context = _ctx;
			_data = _arr;
		}
		
		public Pager1Adapter(ArrayList<HashMap<String, Object>> _arr) {
			_context = getApplicationContext();
			_data = _arr;
		}
		
		@Override
		public int getCount() {
			return _data.size();
		}
		
		@Override
		public boolean isViewFromObject(View _view, Object _object) {
			return _view == _object;
		}
		
		@Override
		public void destroyItem(ViewGroup _container, int _position, Object _object) {
			_container.removeView((View) _object);
		}
		
		@Override
		public int getItemPosition(Object _object) {
			return super.getItemPosition(_object);
		}
		
		@Override
		public CharSequence getPageTitle(int pos) {
			// use the activitiy event (onTabLayoutNewTabAdded) in order to use this method
			return "page " + String.valueOf(pos);
		}
		
		@Override
		public Object instantiateItem(ViewGroup _container,  final int _position) {
			View _view = LayoutInflater.from(_context).inflate(R.layout.page, _container, false);
			
			final LinearLayout linear1 = (LinearLayout) _view.findViewById(R.id.linear1);
			final TextView name = (TextView) _view.findViewById(R.id.name);
			final TextView props = (TextView) _view.findViewById(R.id.props);
			
			name.setText(_data.get((int)_position).get("nm").toString());
			props.setText(_data.get((int)_position).get("props").toString());
			linear1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View _view) {
					trans.putExtra("pos", String.valueOf((long)(_position)));
					trans.putExtra("nm", _data.get((int)_position).get("nm").toString());
					trans.setAction(Intent.ACTION_VIEW);
					trans.setClass(getApplicationContext(), DcEditActivity.class);
					startActivity(trans);
				}
			});
			linear1.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View _view) {
					dial.setTitle(_data.get((int)_position).get("nm").toString());
					dial.setMessage(_data.get((int)_position).get("props").toString());
					dial.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							dc.remove((int)(_position));
							_save(new Gson().toJson(dc), FileUtil.getPackageDataDir(getApplicationContext()).concat("/ydc.data"));
							_update();
						}
					});
					dial.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							
						}
					});
					dial.setNeutralButton("Edit", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface _dialog, int _which) {
							
						}
					});
					dial.create().show();
					return true;
				}
			});
			
			_container.addView(_view);
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