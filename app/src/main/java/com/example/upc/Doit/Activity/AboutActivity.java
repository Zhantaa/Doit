package com.example.upc.Doit.Activity;

import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.upc.Doit.R;

import me.drakeet.multitype.Items;
import me.drakeet.support.about.AbsAboutActivity;
import me.drakeet.support.about.Card;
import me.drakeet.support.about.Category;
import me.drakeet.support.about.Contributor;

public class AboutActivity extends AbsAboutActivity {

    @Override
    protected void onCreateHeader(@NonNull ImageView icon, @NonNull TextView slogan, @NonNull TextView version) {
        icon.setImageResource(R.mipmap.ic_launcher);
        slogan.setText("生命不息，奋斗不止");
        version.setText("v1.1");
    }


    @Override
    protected void onItemsCreated(@NonNull Items items) {
        items.add(new Category("介绍"));
        items.add(new Card(getString(R.string.about_app)));

        items.add(new Category("功能特性"));
        items.add(new Card(getString(R.string.about_function)));

        items.add(new Category("开发者"));
        items.add(new Contributor(R.drawable.designer1,"Bai", "Developer & designer", "https://github.com/Heyho-bai"));
        items.add(new Contributor(R.drawable.designer2,"Zhantaa", "Developer & designer","https://github.com/Zhantaa"));
        items.add(new Contributor(R.drawable.designer3,"TST", "Developer & designer","https://github.com/tst1111"));

        items.add(new Category("项目地址"));
        items.add(new Contributor(R.drawable.github,"Github","Doit","https://github.com/Zhantaa/Doit"));

    }
}