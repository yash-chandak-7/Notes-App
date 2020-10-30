package com.example.notesss.activites;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.notesss.R;
import com.example.notesss.adapters.NotesAdapter;
import com.example.notesss.database.NotesDatabase;
import com.example.notesss.entities.Note;
import com.example.notesss.listeners.NoteListener;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {
        public static final int REQUEST_CODE_ADD_NOTE=1;
        public static final int REQUEST_CODE_UPDATE_NOTE=2;
        public static final int REQUEST_CODE_SHOW_NOTE=3;
       private RecyclerView notesrecyclerview;
       private List<Note> noteList;
       private NotesAdapter notesAdapter;
       private int notesClickedPosition=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView ImageAddmainNote= findViewById(R.id.addnotemain);
        ImageAddmainNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(getApplicationContext(), NotesActivity.class),
                        REQUEST_CODE_ADD_NOTE
                );
            }
        });
        notesrecyclerview = findViewById(R.id.NotesRecyclerView);
        notesrecyclerview.setLayoutManager(
                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL)
        );
        noteList= new ArrayList<>();
        notesAdapter=new NotesAdapter(noteList,this);
        notesrecyclerview.setAdapter(notesAdapter);
        getNotes(REQUEST_CODE_SHOW_NOTE,false);

        EditText inputSearch= findViewById(R.id.input_search);
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
            if(noteList.size()!=0)
            {
                notesAdapter.searchNotes(s.toString());
            }
            }
        });
    }

    @Override
    public void onNoteClick(Note note, int position) {
    notesClickedPosition=position;
        Intent intent= new Intent(getApplicationContext(), NotesActivity.class);
        intent.putExtra("isViewOrUpdate",true);
        intent.putExtra("note",note);
        startActivityForResult(intent,REQUEST_CODE_UPDATE_NOTE);

    }

    private void getNotes(final int requestCode, final boolean isNoteDeleted)
    {
        class getNotestask extends AsyncTask<Void,Void, List<Note>>
        {
            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NotesDatabase.getDatabase(
                        getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(requestCode==REQUEST_CODE_SHOW_NOTE)
                {
                    noteList.addAll(notes);
                    notesAdapter.notifyDataSetChanged();
                }
                else if(requestCode==REQUEST_CODE_UPDATE_NOTE)
                {
                    noteList.remove(notesClickedPosition);
                    if(isNoteDeleted)
                        notesAdapter.notifyItemRemoved(notesClickedPosition);
                    else{
                        noteList.add(notesClickedPosition,notes.get(notesClickedPosition));
                        notesAdapter.notifyItemChanged(notesClickedPosition);
                    }
                }
                else if(requestCode==REQUEST_CODE_ADD_NOTE)
                {
                    noteList.add(0,notes.get(0));
                    notesAdapter.notifyItemInserted(0);
                    notesrecyclerview.smoothScrollToPosition(0);
                }
            }
        }

        new getNotestask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== REQUEST_CODE_ADD_NOTE && resultCode==RESULT_OK)
        {
            getNotes(REQUEST_CODE_ADD_NOTE,false);
        }
        else if(requestCode==REQUEST_CODE_UPDATE_NOTE && resultCode==RESULT_OK)
        {
            if(data!=null)
            getNotes(REQUEST_CODE_UPDATE_NOTE,data.getBooleanExtra("isNoteDeleted", false));
        }
    }
}
