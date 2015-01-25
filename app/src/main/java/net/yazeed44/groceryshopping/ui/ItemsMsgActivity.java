package net.yazeed44.groceryshopping.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonRectangle;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.ItemsMsgFormatter;
import net.yazeed44.groceryshopping.utils.ViewUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by yazeed44 on 1/21/15.
 */
public class ItemsMsgActivity extends BaseActivity {

    @InjectView(R.id.items_msg_txt)
    EditText mMsgTxt;

    @InjectView(R.id.items_msg_copy_btn)
    ButtonRectangle mCopyBtn;

    @InjectView(R.id.items_msg_share_btn)
    ButtonRectangle mShareBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.actionbar_title_share_items);

        ButterKnife.inject(this);
        setupMsgText();
        setupButtons();
    }

    private void setupMsgText() {
        mMsgTxt.setText(ItemsMsgFormatter.from(MainActivity.CHOSEN_ITEMS).format());
    }

    private void setupButtons() {


        mShareBtn.getTextView().setTypeface(ViewUtil.getRegularDefaultTypeface(), Typeface.BOLD);
        mCopyBtn.getTextView().setTypeface(ViewUtil.getRegularDefaultTypeface(), Typeface.BOLD);

        mShareBtn.setTextColor(Color.WHITE);
        mCopyBtn.setTextColor(Color.parseColor("#202020"));

        final float textSize = getResources().getDimension(R.dimen.items_msg_btn_text_size);
        mShareBtn.getTextView().setTextSize(textSize);
        mCopyBtn.getTextView().setTextSize(textSize);

        final Drawable shareDrawable = getResources().getDrawable(R.drawable.ic_share);
        mShareBtn.getTextView().setCompoundDrawablesWithIntrinsicBounds(shareDrawable, null, null, null);

        final Drawable copyDrawable = getResources().getDrawable(R.drawable.ic_content_copy);
        mCopyBtn.getTextView().setCompoundDrawablesWithIntrinsicBounds(copyDrawable, null, null, null);
    }


    @OnClick(R.id.items_msg_share_btn)
    public void share(View view) {
        openShareDialog();
    }



    private void openShareDialog() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, mMsgTxt.getText().toString());
        startActivity(Intent.createChooser(share, getResources().getString(R.string.title_share_item)));
    }

    @OnClick(R.id.items_msg_copy_btn)
    public void copy(View view) {
        copyMsgToClipboard();
    }


    private void copyMsgToClipboard() {
        //TODO implement copy
    }
}
