package com.app.classlogin;

import android.provider.BaseColumns;

public interface ClassDataStructure_ClassNote extends BaseColumns{
	public static final String TABLE_NAME_NoteInformation = "ClassNoteInformation";
	
	public static final String NOTE_TIME = "NoteTime";
	public static final String NOTE_TYPE = "NoteType";//0 = Text, 1 = Photo, 2 = Audio
	public static final String NOTE_TYPEICON = "NoteTypeIcon";
	public static final String NOTE_FILENAME = "NoteFilename";
	public static final String BELONG_CLASS_ID = "BelongClassID";
	public static final String CONTENT = "NoteTextContent";
	
}
