package com.example.genia.gosteekassa.Dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.genia.gosteekassa.R;

public class Dialog1 extends DialogFragment implements View.OnClickListener{

    private Boolean isAgree = false;

    public interface ButtonOKListener{
        void onFinishButtonDialog(Boolean _isAgree);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog, null);
        v.findViewById(R.id.btnOKDialog).setOnClickListener(this);


        Glide.with(this)
                .load(getUrlWithHeaders("http://r2551241.beget.tech/icons/standartIcon.png"))
                .into((ImageView) v.findViewById(R.id.standartImage));

        return v;
    }

    @Override
    public void onClick(View view) {
        isAgree = true;
        ButtonOKListener buttonOKListener = (ButtonOKListener) getActivity();
        buttonOKListener.onFinishButtonDialog(isAgree);
        dismiss();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
    }

    private GlideUrl getUrlWithHeaders (String url){
        return new GlideUrl(url, new LazyHeaders.Builder()
                .addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_0)" +
                        " AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                .build());
    }
}
