package com.app.classlogin;

import static android.provider.BaseColumns._ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.BELONG_CLASS_ID;
import static com.app.classlogin.ClassDataStructure_ClassNote.CONTENT;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_FILENAME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TIME;
import static com.app.classlogin.ClassDataStructure_ClassNote.NOTE_TYPE;
import static com.app.classlogin.ClassDataStructure_ClassNote.TABLE_NAME_NoteInformation;
import android.app.ListFragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.SimpleCursorAdapter;

public class ClassInformationPhotoNote_Fragment extends ListFragment{
	
	private DataBaseManager DBManager = null;
	public String ClassName;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.DBManager = new DataBaseManager(this.getActivity());
        
        Cursor cursor;
        
        SQLiteDatabase db = this.DBManager.getReadableDatabase();
		String SELECT = NOTE_FILENAME + " = '" + ClassName + "'";
		cursor = db.query(	TABLE_NAME_NoteInformation, 
							new String[] { _ID, NOTE_FILENAME, CONTENT, NOTE_TIME, BELONG_CLASS_ID, NOTE_TYPE}
							, SELECT, null, null, null, null);
		
		@SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.listview_noteitem,
									  cursor, new String[] { NOTE_FILENAME, NOTE_TIME}, 
									  new int[] {R.id.NoteName, R.id.NoteTime});

		setListAdapter(adapter);
    }

}
