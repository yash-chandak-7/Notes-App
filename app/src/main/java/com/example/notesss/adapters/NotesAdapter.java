package com.example.notesss.adapters;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notesss.R;
import com.example.notesss.entities.Note;
import com.example.notesss.listeners.NoteListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
        private NoteListener notesListener;
        private List<Note> notes;
        private List<Note> notesSource;
        private Timer timer;

    public NotesAdapter(List<Note> notes, NoteListener noteListener) {
        this.notes = notes;
        this.notesListener= noteListener;
        notesSource=notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note,parent,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, final int position) {
    holder.setNote(notes.get(position));
    holder.layoutNote.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            notesListener.onNoteClick(notes.get(position),position);
        }
    });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder{

        TextView textTitle,textDateTime;
        LinearLayout layoutNote;
        ImageView imageNote;
        @SuppressLint("CutPasteId")
        NoteViewHolder(@NonNull View itemView)
        {
            super(itemView);
            textDateTime=itemView.findViewById(R.id.text_datetime);
            textTitle=itemView.findViewById(R.id.text_title);
            layoutNote=itemView.findViewById(R.id.notelayout);
            imageNote=itemView.findViewById(R.id.imagenote);
        }
        void setNote(Note note)
        {
            textTitle.setText(note.getTitle());
            textDateTime.setText(note.getDateTime());
           GradientDrawable gradientDrawable=(GradientDrawable) layoutNote.getBackground();
           if(note.getColor()!=null)
           {
               gradientDrawable.setColor(Color.parseColor(note.getColor()));
           }else{
               gradientDrawable.setColor(Color.parseColor("#333333"));
           }
           if(note.getImagePath()!=null)
           {
               imageNote.setImageBitmap(BitmapFactory.decodeFile(note.getImagePath()));
               imageNote.setVisibility(View.VISIBLE);
           }
           else
               imageNote.setVisibility(View.GONE);

        }
    }
    public void searchNotes(final String searchKeyWords)
    {
        timer=new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(searchKeyWords.trim().isEmpty())
                {
                    notes=notesSource;
                }
                else{
                    ArrayList<Note> temp= new ArrayList<>();
                     for(Note note:notesSource)
                     {
                         if(note.getTitle().toLowerCase().contains(searchKeyWords.toLowerCase()) ||
                                 note.getNoteText().toLowerCase().contains(searchKeyWords.toLowerCase())) {
                             temp.add(note);
                         }
                     }
                    notes = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }
        public void cancelTimer()
        {
            if(timer!=null)
            {
                timer.cancel();
            }
        }
}
