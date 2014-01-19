package com.weava.listed;

import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends ListActivity {
	private TasksDataSource datasource;
	final Context context = this;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		
		datasource = new TasksDataSource(this);
		datasource.open();
		
		List<Task> values = datasource.getAllTasks();
		
		ArrayAdapter<Task> adapter = new ArrayAdapter<Task>(this,
				android.R.layout.simple_list_item_1, values);
		setListAdapter(adapter);
		
		registerForContextMenu(getListView());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_main, menu);
		return (super.onCreateOptionsMenu(menu));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		case R.id.new_task:
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.edit_item);
			dialog.setTitle("Task Title");
			
			final EditText text = (EditText)dialog.findViewById(R.id.task_name);
			Button confirmButton = (Button)dialog.findViewById(R.id.confirm);
			Button cancelButton = (Button)dialog.findViewById(R.id.cancel);

			confirmButton.setOnClickListener(new OnClickListener()
			{
				ArrayAdapter<Task> adapter = (ArrayAdapter<Task>)getListAdapter();
				Task task = null;
				@Override
				public void onClick(View v)
				{
					task = datasource.createTask(text.getText().toString());
					adapter.add(task);
					dialog.dismiss();
				}
			});
			
			cancelButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.cancel();
				}
			});
			dialog.show();
			break;
			
		case R.id.clear_all:
			final Dialog dialogClear = new Dialog(context);
			dialogClear.setContentView(R.layout.clear_items);
			dialogClear.setTitle("Clear All");
			
			final TextView textV = (TextView)dialogClear.findViewById(R.id.clearing);
			textV.setTextSize(20.0f);
			Button confirmButtonB = (Button)dialogClear.findViewById(R.id.confirm);
			Button cancelButtonB = (Button)dialogClear.findViewById(R.id.cancel);

			confirmButtonB.setOnClickListener(new OnClickListener()
			{
					@Override
					public void onClick(View v)
					{
						ArrayAdapter<Task> adapter = (ArrayAdapter<Task>)getListAdapter();
						datasource.deleteAllTasks();
						adapter.clear();
						adapter.notifyDataSetChanged();
						dialogClear.dismiss();
					}
			});
			
			cancelButtonB.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialogClear.cancel();
				}
			});
			dialogClear.show();
			break;
		}
		return(super.onOptionsItemSelected(item));
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.context_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item)
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ArrayAdapter<Task> adapter = (ArrayAdapter<Task>)getListAdapter();
		Task task = null;
		switch(item.getItemId())
		{
		case R.id.delete_task:
			if(getListAdapter().getCount() > 0)
			{	
				//System.out.println(getListAdapter().getPosition(getItem(item.getItemId())));
				task = (Task) getListAdapter().getItem(info.position);
				datasource.deleteTask(task);
				adapter.remove(task);
			}
			break;
		case R.id.edit:
			final Dialog dialog = new Dialog(context);
			dialog.setContentView(R.layout.edit_item);
			dialog.setTitle("Task Title");
			
			final EditText text = (EditText)dialog.findViewById(R.id.task_name);
			Button confirmButton = (Button)dialog.findViewById(R.id.confirm);
			Button cancelButton = (Button)dialog.findViewById(R.id.cancel);

			confirmButton.setOnClickListener(new OnClickListener()
			{
				ArrayAdapter<Task> adapter = (ArrayAdapter<Task>)getListAdapter();
				Task task = null;
				@Override
				public void onClick(View v)
				{
					int editPos = info.position;
					task = (Task) getListAdapter().getItem(editPos);
					adapter.remove(task);
					datasource.editTask(text.getText().toString(), task);
					task = new Task();
					task.setId(editPos);
					task.setTask(text.getText().toString());
					adapter.insert(task, editPos);
					
					dialog.dismiss();
				}
			});
			
			cancelButton.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					dialog.cancel();
				}
			});
			dialog.show();
		}
		adapter.notifyDataSetChanged();
		return super.onContextItemSelected(item);
	}
	
	@Override
	protected void onResume()
	{
		datasource.open();
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		datasource.close();
		super.onPause();
	}
}
