package com.imax.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.imax.app.R;
import java.util.List;

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.FileViewHolder> {

    private List<String> filesList;  // Lista de nombres de archivos
    private OnFileRemoveListener listener;

    public FilesAdapter(List<String> filesList, OnFileRemoveListener listener) {
        this.filesList = filesList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cliente, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        String fileName = filesList.get(position);
        holder.tvFileName.setText(fileName);

        // Manejamos el click del botón de eliminar
        holder.imgDelete.setOnClickListener(v -> {
            listener.onFileRemoved(position);
            removeFile(position);  // Eliminar el archivo de la lista y notificar al adaptador
        });
    }

    @Override
    public int getItemCount() {
        return filesList.size();
    }

    // Método para eliminar un archivo de la lista
    public void removeFile(int position) {
        if (position >= 0 && position < filesList.size()) {
            filesList.remove(position);  // Elimina el archivo de la lista
            notifyItemRemoved(position);  // Notifica al adaptador que un archivo ha sido eliminado
        }
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        TextView tvFileName;
        ImageView imgDelete;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }

    public interface OnFileRemoveListener {
        void onFileRemoved(int position);
    }
}
