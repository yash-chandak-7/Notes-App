package com.example.notesss.activites;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notesss.R;
import com.example.notesss.database.NotesDatabase;
import com.example.notesss.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NotesActivity extends AppCompatActivity {

    private EditText NoteTitle,InputText;
    private TextView textDateTime;
    private View viewColourIndicator;
    private String selectedNoteColour = "#333333";
    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int IMAGE_SELECT=2;
    private ImageView imageNote;
    private String SelectedImagePath;
    private TextView textWebUri;
    private LinearLayout layoutWebUrl;
    private AlertDialog dialogAddURL;
    private Note alreadyAvailableNote;
    private AlertDialog dialogDeleteNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        ImageView Back= findViewById(R.id.imageback);



        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        NoteTitle= findViewById(R.id.Title);
        InputText=findViewById(R.id.inputnote);
        textDateTime=findViewById(R.id.Timedate);
        viewColourIndicator=findViewById(R.id.viewsubtitleIndicator);
        imageNote=findViewById(R.id.ImageNote);
        textWebUri=findViewById(R.id.textWebUri);
        layoutWebUrl=findViewById(R.id.layoutWebUri);

        textDateTime.setText(
                new SimpleDateFormat("EEEE, dd MMMM yyyy, hh:mm a", Locale.getDefault()).format(new Date())
        );
        ImageView save= findViewById(R.id.Imagesave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savenote();
            }
        });


        selectedNoteColour="#333333";
        SelectedImagePath="";

        if(getIntent().getBooleanExtra("isViewOrUpdate",false))
        {
            alreadyAvailableNote= (Note) getIntent().getSerializableExtra("note");
            setViewORupdateNote();
        }

        findViewById(R.id.removeurl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutWebUrl.setVisibility(View.GONE);
                textWebUri.setText(null);
            }
        });
        findViewById(R.id.removeImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(null);
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.removeImage).setVisibility(View.GONE);
                SelectedImagePath="";
            }
        });

        initMiscellaneous();
        setSubtitleIndicatorColour();
    }
    private void setViewORupdateNote()
    {
        NoteTitle.setText(alreadyAvailableNote.getTitle());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
        InputText.setText(alreadyAvailableNote.getNoteText());
        if(alreadyAvailableNote.getImagePath()!=null && !alreadyAvailableNote.getImagePath().trim().isEmpty())
        {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.removeImage).setVisibility(View.VISIBLE);
            SelectedImagePath= alreadyAvailableNote.getImagePath();
        }
        if(alreadyAvailableNote.getWeblink()!=null && !alreadyAvailableNote.getWeblink().trim().isEmpty())
        {
            textWebUri.setText(alreadyAvailableNote.getWeblink());
            layoutWebUrl.setVisibility(View.VISIBLE);
        }

    }



    private void savenote()
    {
        if(NoteTitle.getText().toString().trim().isEmpty())
        {
            Toast.makeText(this,"Title can't be Empty",Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note= new Note();
        note.setTitle(NoteTitle.getText().toString());
        note.setDateTime(textDateTime.getText().toString());
        note.setNoteText(InputText.getText().toString());
        note.setColor(selectedNoteColour);
        note.setImagePath(SelectedImagePath);
        if(layoutWebUrl.getVisibility()==View.VISIBLE)
        {
            note.setWeblink(textWebUri.getText().toString());
        }
        if(alreadyAvailableNote!=null)
        {
            note.setId(alreadyAvailableNote.getId());
        }

        @SuppressLint("StaticFieldLeak")
        class Savenotetask extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                NotesDatabase.getDatabase(getApplicationContext()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent= new Intent();
                setResult(RESULT_OK ,intent);
                finish();
            }
        }
        new Savenotetask().execute();
    }
    private void initMiscellaneous()
    {
            final LinearLayout layoutMiscellaneous= findViewById(R.id.miscellenous);
            final BottomSheetBehavior<LinearLayout> bottomSheetBehavior=BottomSheetBehavior.from(layoutMiscellaneous);
            layoutMiscellaneous.findViewById(R.id.text_bar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(bottomSheetBehavior.getState()!= BottomSheetBehavior.STATE_EXPANDED)

                       bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    else
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });

            final ImageView imageColour1= layoutMiscellaneous.findViewById(R.id.color1);
            final ImageView imageColour2= layoutMiscellaneous.findViewById(R.id.color2);
            final ImageView imageColour3= layoutMiscellaneous.findViewById(R.id.color3);
            final ImageView imageColour4= layoutMiscellaneous.findViewById(R.id.color4);
            final ImageView imageColour5= layoutMiscellaneous.findViewById(R.id.color5);

            layoutMiscellaneous.findViewById(R.id.viewcolor1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedNoteColour="#333333";
                    imageColour1.setImageResource(R.drawable.ic_done);
                    imageColour2.setImageResource(0);
                    imageColour3.setImageResource(0);
                    imageColour4.setImageResource(0);
                    imageColour5.setImageResource(0);
                    setSubtitleIndicatorColour();
                }
            });
        layoutMiscellaneous.findViewById(R.id.viewcolor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColour="#FDBE3B";
                imageColour2.setImageResource(R.drawable.ic_done);
                imageColour1.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewcolor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColour="#FF4842";
                imageColour3.setImageResource(R.drawable.ic_done);
                imageColour2.setImageResource(0);
                imageColour1.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewcolor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColour="#3A52Fc";
                imageColour4.setImageResource(R.drawable.ic_done);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour1.setImageResource(0);
                imageColour5.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewcolor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColour="#000000";
                imageColour5.setImageResource(R.drawable.ic_done);
                imageColour2.setImageResource(0);
                imageColour3.setImageResource(0);
                imageColour4.setImageResource(0);
                imageColour1.setImageResource(0);
                setSubtitleIndicatorColour();
            }
        });
        if(alreadyAvailableNote!=null && alreadyAvailableNote.getColor()!=null && !alreadyAvailableNote.getColor().trim().isEmpty())
        {
            switch (alreadyAvailableNote.getColor())
            {
                case "#FDBE3B":
                    layoutMiscellaneous.findViewById(R.id.viewcolor2).performClick();
                    break;
                case "#FF4842":
                    layoutMiscellaneous.findViewById(R.id.viewcolor3).performClick();
                    break;
                case "#3A52Fc":
                    layoutMiscellaneous.findViewById(R.id.viewcolor4).performClick();
                    break;
                case "#000000":
                    layoutMiscellaneous.findViewById(R.id.viewcolor5).performClick();
                    break;
            }
        }

        layoutMiscellaneous.findViewById(R.id.layoutimageadd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(bottomSheetBehavior.STATE_COLLAPSED);
                if(ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
                {
                    ActivityCompat.requestPermissions(
                            NotesActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
                }else
                {
                    SelectImage();
                }
            }
        });
        layoutMiscellaneous.findViewById(R.id.layouturladd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showWebURLDialogue();
            }
        });
        if(alreadyAvailableNote!=null)
        {
            layoutMiscellaneous.findViewById(R.id.layout_Delete_note).setVisibility(View.VISIBLE);
            layoutMiscellaneous.findViewById(R.id.layout_Delete_note).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteDialogue();
                }
            });

        }
    }
        private void showDeleteDialogue()
        {
            if(dialogDeleteNote==null)
            {
                AlertDialog.Builder builder= new AlertDialog.Builder(NotesActivity.this);
                View view= LayoutInflater.from(this).inflate(R.layout.layout_delete_note,(ViewGroup)findViewById(R.id.Delete_note_container));
                builder.setView(view);
                dialogDeleteNote= builder.create();
                if(dialogDeleteNote.getWindow()!=null)
                {
                    dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                view.findViewById(R.id.deletebutton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        @SuppressLint("StaticFieldLeak")
                        class DeleteNoteTask extends AsyncTask<Void,Void,Void>{
                            @Override
                            protected Void doInBackground(Void... voids) {
                                NotesDatabase.getDatabase(getApplicationContext()).noteDao().deleteNote(alreadyAvailableNote);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);
                                Intent intent=new Intent();
                                intent.putExtra("isNoteDeleted",true);
                                setResult(RESULT_OK,intent);
                                finish();
                            }
                        }
                        new DeleteNoteTask().execute();
                    }
                });
                view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDeleteNote.dismiss();
                    }
                });
            }
            dialogDeleteNote.show();
        }

    private void setSubtitleIndicatorColour(){
        GradientDrawable gradientDrawable= (GradientDrawable) viewColourIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColour));

    }
    private void SelectImage()
    {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,IMAGE_SELECT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_STORAGE_PERMISSION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                SelectImage();
        }else
        {
            Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_SELECT && resultCode==RESULT_OK)
        {
            if(data!=null)
            {
                Uri SelectImageUri= data.getData();
                if(SelectImageUri!=null)
                    try {
                        InputStream inputStream= getContentResolver().openInputStream(SelectImageUri);
                        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        SelectedImagePath =getPathFromURL(SelectImageUri);
                        findViewById(R.id.removeImage).setVisibility(View.VISIBLE);
                    }catch(Exception exception)
                    {
                        Toast.makeText(this,exception.getMessage(),Toast.LENGTH_SHORT);
                    }
            }
        }
    }
    private String getPathFromURL(Uri contentURI)
    {
        String filePath;
        Cursor cursor=getContentResolver().query(contentURI,null,null,null,null);
        if(cursor==null)
        {
            filePath=contentURI.getPath();
        }
        else{
            cursor.moveToFirst();
            int index=cursor.getColumnIndex("_data");
            filePath=cursor.getString(index);
            cursor.close();
        }
        return filePath;
    }
    private void showWebURLDialogue()
    {
        if(dialogAddURL==null)
        {
            AlertDialog.Builder builder= new AlertDialog.Builder(NotesActivity.this);
            View view= LayoutInflater.from(this).inflate(
                    R.layout.add_url,
                    (ViewGroup) findViewById(R.id.Add_url_container)
            );
            builder.setView(view);
            dialogAddURL =builder.create();
            if(dialogAddURL.getWindow()!=null)
            {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL=view.findViewById(R.id.inputURL);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputURL.getText().toString().trim().isEmpty())
                    {
                        Toast.makeText(NotesActivity.this,"Enter URL",Toast.LENGTH_SHORT).show();
                    }
                    else if(!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches())
                    {
                        Toast.makeText(NotesActivity.this,"Enter Valid URL",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        textWebUri.setText(inputURL.getText().toString());
                        layoutWebUrl.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textBack).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }

}
