package de.tadris.fitness.view;

import android.app.Activity;
import android.app.Dialog;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.tadris.fitness.R;

public class ProgressDialogController {

    private Activity context;
    private Dialog dialog;
    private TextView infoView;
    private ProgressBar progressBar;

    public ProgressDialogController(Activity context, String title) {
        this(context);
        setTitle(title);
    }

    public ProgressDialogController(Activity context) {
        this.context = context;
        this.dialog= new Dialog(context);
        initDialog();
    }

    private void initDialog(){
        dialog.setContentView(R.layout.dialog_progress);
        infoView= dialog.findViewById(R.id.dialogProgressInfo);
        progressBar= dialog.findViewById(R.id.dialogProgressBar);
    }

    public void setTitle(String title){
        dialog.setTitle(title);
    }

    public void setIndeterminate(boolean indeterminate){
        progressBar.setIndeterminate(indeterminate);
    }

    public void setProgress(int progress){
        progressBar.setProgress(progress);
    }

    public void setProgress(int progress, String info){
        setProgress(progress);
        infoView.setText(info);
    }

    public void show(){
        dialog.show();
    }

    public void cancel(){
        dialog.dismiss();
    }
}
