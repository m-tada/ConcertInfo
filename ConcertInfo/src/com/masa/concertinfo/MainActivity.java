package com.masa.concertinfo;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
private TextView mView;
private DatabaseHelper dbhelper = null;
static private String mch1 = "<dt>";
static private String mch2 ="<a href=";
static private String mch22 ="</a>";
static private String mch3="<dd>";
static private String mch33="</dd>";
static private String[] mArticleTitle = new String[100];
static private String[] mArticleLocation = new String[100];
static private String[] mArticleDay = new String[100];
static private String[] mArticleDetail = new String[100];
static private String[] mArticleWeb = new String[100];
static private int mArticleNum;
static private String tmp_data = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    	StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
    	dbhelper = new DatabaseHelper(this);
    	httpGet("http://www.2083.jp/concert/#next");
		ListView list = (ListView)findViewById(R.id.ListView01);
		ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
		for(int i=0;i< mArticleNum;i++){
			arrayList.add(new ListItem(mArticleDay[i],mArticleLocation[i],mArticleTitle[i],mArticleDetail[i],mArticleWeb[i]));
		}

		list.setAdapter(new ListArrayAdapter(this,arrayList));

       // mView=(TextView)findViewById(R.id.view);
       // mView.setText(new String(httpGet("http://www.2083.jp/concert/#next")));
    }

    public static void httpGet(String strURL){
     try{
        URL url=new URL(strURL);
        URLConnection connection=url.openConnection();
        connection.setDoInput(true);
        InputStream stream=connection.getInputStream();
        BufferedReader input=new BufferedReader(new InputStreamReader(stream,"SJIS"));
        String data="";
        String tmp="";
        int count = 0;
        String flag_s="<li><dl class=\"detail\">";
        String flag_e="</dl></li>";
        String flag_dt="<dt></dt>";
        String flag_last = "<dd>コンサート情報掲載の依頼は";
        int flag = 0;

        while((tmp=input.readLine()) !=null){
        	if (tmp.indexOf(flag_dt) != -1 || tmp.indexOf(flag_last) != -1){
        	} else {
        		if(flag==1){
        			dataParser(brChecker(tmp),count);
        		}
        	}

        	if (tmp.indexOf(flag_s) != -1){
        		flag=1;
        		count++;
        	}
        	if (tmp.indexOf(flag_e) != -1){
        		flag=0;
        	}
        }
         stream.close();
         input.close();

     }catch (Exception  e){
         e.toString();
     }
   }

    public static String brChecker(String data){
    	String freecheck= "<span class=\"free\">";
    	//titleの改行チェック
        if(data.indexOf(mch2) != -1){
     	}else if(data.indexOf(mch22) != -1){
        	tmp_data+=data;
        	return tmp_data;
     	}
      //detailの改行チェック
        if(data.indexOf(mch3) != -1){
     	}else if(data.indexOf(mch33) != -1){
     		if(data.indexOf(freecheck) != -1){
     			tmp_data+="</dd>";
     			return tmp_data;
     		}else{
     			tmp_data+=data;
     			return tmp_data;
     		}
     	}
    	tmp_data = data;
    	return data;
    }

    public static void dataParser(String data ,int cnt){
    	int ct = cnt - 1;
    	if (data.indexOf(mch1) != -1){
    		mArticleNum++;
        	//dayのデータ整形
        	String[] spday = data.split("【");
    		mArticleDay[ct] = spday[0].replaceAll("<dt>","");

    		//locationのデータ整形
    		String[] sploca = spday[1].split("・");
    		mArticleLocation[ct] = sploca[0];
    	}

    	if (data.indexOf(mch22) != -1 && data.indexOf(mch2) != -1){

    		//webのデータ整形
    		String[] spweb = data.split("\"");
    		mArticleWeb[ct] = spweb[1];

    		//titleのデータ整形
    		spweb[2] = spweb[2].replaceAll("<br />"," ");
    		spweb[2] = spweb[2].replaceAll("</a></dt>"," ");
    		spweb[2] = spweb[2].replaceAll(">"," ");
    		mArticleTitle[ct] = spweb[2];
    		Log.v("title",mArticleTitle[ct]);
    	}

    	if (data.indexOf(mch3) != -1 && data.indexOf(mch33) != -1){
    		String detail = data;
    		detail = detail.replaceAll("<br />"," ");
    		detail = detail.replaceAll("</dd>","");
    		detail = detail.replaceAll("<dd>","");
    		detail = detail.replaceAll("</span>","");
    		detail = detail.trim();
    		mArticleDetail[ct] = detail;
    	}
    }


 //-------------------表示用----------------------------------

    public class ListItem{
		public String day;
		public String location;
		public String title;
		public String detail;
		public String web;

		public ListItem(String day, String location, String title, String detail, String web){
			this.day=day;
			this.location=location;
			this.title=title;
			this.detail=detail;
			this.web=web;
		}
	}

	public class ListArrayAdapter extends ArrayAdapter<ListItem> implements View.OnClickListener {
		private ArrayList<ListItem> listItem;

		public ListArrayAdapter(Context context, ArrayList<ListItem> listItem){
			super(context,R.layout.rowitem,listItem);
			this.listItem=listItem;
		}

		@Override
		public View getView(int positopn,View view, ViewGroup parent){
			ListItem item = listItem.get(positopn);
			Context context=getContext();
			LinearLayout linearLayout=new LinearLayout(context);
			LinearLayout linearLayout2=new LinearLayout(context);
			LinearLayout linearLayout3=new LinearLayout(context);
			linearLayout2.setOrientation(LinearLayout.VERTICAL);
			linearLayout3.setOrientation(LinearLayout.VERTICAL);

			view=linearLayout;


			TextView textView= new TextView(context);
			textView.setSingleLine(true);
			textView.setText(item.day+"【"+item.location+"】");
			linearLayout3.addView(textView);

			TextView textView2= new TextView(context);
			TextView textView3= new TextView(context);
			textView2.setSingleLine(true);
			textView3.setSingleLine(true);
			textView2.setText(item.title);
			textView3.setText(item.detail);
			linearLayout2.addView(textView2);
			linearLayout2.addView(textView3);
			HorizontalScrollView ScrollView = new HorizontalScrollView(context);
			ScrollView.addView(linearLayout2);
			linearLayout3.addView(ScrollView);

			linearLayout.addView(linearLayout3);
			Button button=new Button(context);
			button.setText("詳細");
			button.setTag(String.valueOf(positopn));
			button.setOnClickListener(this);
			linearLayout.addView(button,0);
			return view;
		}


		public void onClick(View view){
			int tag = Integer.parseInt((String)view.getTag());
			ListItem item = listItem.get(tag);
			try{
				Intent intent = new Intent("android.intent.action.VIEW",Uri.parse(item.web));
	       		startActivity(intent);
	       	}catch(Exception e){
	       		e.printStackTrace();
	       	}
		}
	}
	//----------------------------------------------------------------------------------------

    //database関係メソッド
    public void doSave(String t) {
        String s_title = "";
        String s_day = "";
        String s_location ="";
        String s_web = "";
        String s_detail ="";

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseHelper.TITLE, s_title);
        values.put(DatabaseHelper.DAY, s_day);
        values.put(DatabaseHelper.LOCATION, s_location);
        values.put(DatabaseHelper.WEB, s_web);
        values.put(DatabaseHelper.DETAIL, s_detail);
        db.insert(DatabaseHelper.TABLE_NAME,null,values);
    }

    public void doFind(String v) {
    	String s_title = "";
        SQLiteDatabase db = dbhelper.getReadableDatabase();
    	String findset = DatabaseHelper.TITLE + " = ?";
    	String[] params = {s_title};
    	Cursor c = db.query(DatabaseHelper.TABLE_NAME,DatabaseHelper.COL_ARR,
    		findset,params,null,null,null,null);
    	if (c.moveToFirst()){
    		//name.setText(c.getString(1));
    		//tel.setText(c.getString(2));
    		//content.setText(c.getString(3));
    	} else {


    	}
    }
}