package edu.utd.studyhelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class NoteAdapterOne extends ArrayAdapter<NoteOne> {

    public NoteAdapterOne(Context context, List<NoteOne> notes)
    {
        super(context, 0, notes);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        NoteOne note = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.noteone_cell, parent, false);

        TextView title = convertView.findViewById(R.id.cellTitle);

        title.setText(note.getTitle());

        return convertView;
    }

}
