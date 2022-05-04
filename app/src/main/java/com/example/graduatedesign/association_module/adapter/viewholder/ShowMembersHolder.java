package com.example.graduatedesign.association_module.adapter.viewholder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.graduatedesign.R;
import com.example.graduatedesign.association_module.ui.AssociationViewModel;
import com.example.graduatedesign.personal_module.data.User;
import com.example.graduatedesign.utils.DataUtil;
import com.example.graduatedesign.utils.GlideUtils;

public class ShowMembersHolder extends RecyclerView.ViewHolder {

    private final View itemView;

    private ImageView portrait;
    private TextView nickname;
    private TextView admin_tag;

    private ShowMembersHolder(View itemView) {
        super(itemView);
        this.itemView = itemView;

        portrait = itemView.findViewById(R.id.portrait);
        nickname = itemView.findViewById(R.id.nickname);
        admin_tag = itemView.findViewById(R.id.admin_tag);
    }


    public static ShowMembersHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_association_member, parent, false);
        return new ShowMembersHolder(view);
    }

    public void bind(User user){
        if (user == null)
            return;

        Glide.with(itemView)
                .load(DataUtil.getImgDownloadUri(user.getPortrait()))
                .apply(GlideUtils.OPTIONS)
                .into(portrait);
        nickname.setText(user.getNickname());
        admin_tag.setVisibility(user.getState()==2?View.VISIBLE:View.INVISIBLE);
    }
}
