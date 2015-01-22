package net.yazeed44.groceryshopping.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.gc.materialdesign.views.ButtonRectangle;

import net.yazeed44.groceryshopping.R;
import net.yazeed44.groceryshopping.utils.ItemsMsgFormatter;

/**
 * Created by yazeed44 on 1/21/15.
 */
public class ItemsMsgActivity extends BaseActivity implements View.OnClickListener {

    private EditText mMsgTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_msg);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.actionbar_title_share_items);

        mMsgTxt = (EditText) findViewById(R.id.items_msg_txt);
        setupMsgText();

        final ButtonRectangle copyBtn = (ButtonRectangle) findViewById(R.id.items_msg_copy_btn);
        copyBtn.setOnClickListener(this);

        final ButtonRectangle shareBtn = (ButtonRectangle) findViewById(R.id.items_msg_share_btn);
        shareBtn.setOnClickListener(this);
    }

    private void setupMsgText() {
        mMsgTxt.setText(ItemsMsgFormatter.from(MainActivity.sCheckedItems).format());
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.items_msg_copy_btn:
                copy();
                break;

            case R.id.items_msg_share_btn:
                openShareDialog();
                break;

        }

    }

    private void openShareDialog() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, mMsgTxt.getText().toString());
        startActivity(Intent.createChooser(share, getResources().getString(R.string.title_share_item)));
    }

    private void copy() {
        //TODO implement copy
    }
}
